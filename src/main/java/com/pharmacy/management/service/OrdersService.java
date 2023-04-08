package com.pharmacy.management.service;

import com.pharmacy.management.dto.request.OrderPlaceProductDto;
import com.pharmacy.management.dto.response.OrderDetailsDTO;
import com.pharmacy.management.model.*;
import com.pharmacy.management.projection.OrderDetailsProjection;
import com.pharmacy.management.projection.OrderItemsProjection;
import com.pharmacy.management.repository.*;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
@AllArgsConstructor
public class OrdersService {

    private final Logger log = LoggerFactory.getLogger(OrdersService.class);
    private final OrdersRepository ordersRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final OrdersItemRepository ordersItemRepository;
    private final ModelMapper modelMapper;

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public Orders save(Orders orders) {
        log.debug("Request to save Orders : {}", orders);
        return ordersRepository.save(orders);
    }


    public Optional<Orders> partialUpdate(Orders orders) {
        log.debug("Request to partially update Orders : {}", orders);

        return ordersRepository
                .findById(orders.getId())
                .map(
                        existingOrders -> {
                            if (orders.getOrderDate() != null) {
                                existingOrders.setOrderDate(orders.getOrderDate());
                            }
                            if (orders.getShippedDate() != null) {
                                existingOrders.setShippedDate(orders.getShippedDate());
                            }
                            if (orders.getRequiredDate() != null) {
                                existingOrders.setRequiredDate(orders.getRequiredDate());
                            }
                            if (orders.getTotalPrice() != null) {
                                existingOrders.setTotalPrice(orders.getTotalPrice());
                            }
                            return existingOrders;
                        }
                )
                .map(ordersRepository::save);
    }


    @Transactional(readOnly = true)
    public List<Orders> findAll() {
        log.debug("Request to get all Orders");
        return ordersRepository.findAllByIsActive(true);
    }


    @Transactional(readOnly = true)
    public Optional<Orders> findOne(Long id) {
        log.debug("Request to get Orders : {}", id);
        return ordersRepository.findById(id);
    }


    public void checkLimitation(List<OrderPlaceProductDto> productAndQuantityList, Long categoryId) {
        LocalDate todayDate = LocalDate.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM");
        String yearMonth = myFormatObj.format(todayDate);
        Double currentOrderTotalPrice = 0d;

        Users users = userService.getCurrentUser();
        CompanyPolicy companyPolicy = users.getCompanyPolicy();

        Optional<Category> category = categoryRepository.findById(categoryId);
        if (!category.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found");
        }

        //check every product limitation
        for (OrderPlaceProductDto productDto : productAndQuantityList) {
            Optional<Product> productOptional = productRepository.findById(productDto.getProductId());

            //Current order total price
            Double unitPrice = productOptional.get().getUnitPrice() == null ? 0 : productOptional.get().getUnitPrice();
            Double singleProductTotalPrice = unitPrice * productDto.getQuantity();
            currentOrderTotalPrice += singleProductTotalPrice;

            if (!productOptional.isEmpty()) {
                if ((category.get().getIsLimitUnit() != null && category.get().getIsLimitUnit())
                        && (productOptional.get().getLimitUnit() != null && productOptional.get().getLimitUnit() > 0)) {
                    if (productDto.getQuantity() > productOptional.get().getLimitUnit()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product " + productOptional.get().getName() + " unit limit is cross");
                    }
                }
                //check limit for individual product price
                if ((category.get().getIsLimitCost() != null && category.get().getIsLimitCost()) && (productOptional.get().getLimitCost() != null && productOptional.get().getLimitCost() > 0)) {
                    Double price = productOptional.get().getUnitPrice() * productDto.getQuantity();
                    if (price > productOptional.get().getLimitCost()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product " + productOptional.get().getName() + " cost limit is cross");
                    }
                }
            }
        }

        Double currentMonthSaleAmount = productRepository.currentMonthSalesSum("%" + yearMonth +"%", users.getId());
        Double currentMonthSale = (currentMonthSaleAmount == null ? 0 :  currentMonthSaleAmount) + currentOrderTotalPrice;
        log.info("Total cost : " + currentMonthSale);

        if (currentMonthSale > companyPolicy.getLimitCost()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company policy cost limit cross");
        }
    }

    public OrderDetailsDTO getOrdersFullDetailsByOrderId(Long orderId) {
        OrderDetailsProjection orderDetailsProjection = ordersRepository.getOrderDetailsByOrderId(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order not found!"));
        DeliveryAddress deliveryAddress = deliveryAddressRepository.getDeliveryAddressByOrderId(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery address not found!"));
        List<OrderItemsProjection> allOrderItemsByOrderId = ordersItemRepository.getAllOrderItemByOrderId(orderId);
        if (allOrderItemsByOrderId == null || allOrderItemsByOrderId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order item not found!");
        }
        OrderDetailsDTO orderDetailsDTO = modelMapper.map(orderDetailsProjection, OrderDetailsDTO.class);
        orderDetailsDTO.setDeliveryAddress(deliveryAddress);
        orderDetailsDTO.setOrderItems(allOrderItemsByOrderId);
        return orderDetailsDTO;
    }

    public void delete(Long id) {
        log.debug("Request to delete Orders : {}", id);
        Optional<Orders> ordersOptional = ordersRepository.findById(id);
        ordersOptional.ifPresentOrElse(orders -> {
            orders.setIsActive(false);
            ordersRepository.save(orders);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no region!");
        });
    }
}
