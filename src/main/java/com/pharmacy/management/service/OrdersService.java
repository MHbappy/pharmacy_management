package com.pharmacy.management.service;

import com.pharmacy.management.config.StripeClient;
import com.pharmacy.management.dto.request.OrderPlaceProductDto;
import com.pharmacy.management.dto.request.OrderPlaceRequest;
import com.pharmacy.management.dto.response.OrderDetailsDTO;
import com.pharmacy.management.model.*;
import com.pharmacy.management.model.enumeration.OrderStatus;
import com.pharmacy.management.model.enumeration.PaymentStatus;
import com.pharmacy.management.model.enumeration.ROLE;
import com.pharmacy.management.projection.OrderDetailsProjection;
import com.pharmacy.management.projection.OrderItemsProjection;
import com.pharmacy.management.repository.*;
import com.pharmacy.management.security.SecurityUtils;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private final StripeClient stripeClient;
    private final OrderApproveRepository orderApproveRepository;

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


    public Boolean createOrders(@RequestBody OrderPlaceRequest orderPlaceRequest) throws StripeException {
        if (orderPlaceRequest.getProductAndQuantityList() == null || orderPlaceRequest.getProductAndQuantityList().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please add some product!");
        }
        if (orderPlaceRequest.getDeliveryAddressId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please add address!");
        }
        if (orderPlaceRequest.getCategoryId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found");
        }

        if (orderPlaceRequest.getProviderId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provider not found");
        }

        Users users = userService.getCurrentUser();
        if (users.getIsSelfPayment() != null && users.getIsSelfPayment() && (orderPlaceRequest.getPaymentToken() == null || orderPlaceRequest.getPaymentToken().isEmpty())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment token can not be empty");
        }

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByIdAndUsersAndIsActive(orderPlaceRequest.getDeliveryAddressId(), users, true);
        if (deliveryAddress == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect delivery address!");
        }



        Double totalPrice = 0d;
        List<OrdersItem> ordersItemList = new ArrayList<>();
        for (OrderPlaceProductDto orderPlaceRequest1 : orderPlaceRequest.getProductAndQuantityList()) {
            OrdersItem ordersItem = new OrdersItem();
            Optional<Product> productOptional = productRepository.findById(orderPlaceRequest1.getProductId());
            //set onOrder and stock
            if (!productOptional.isEmpty()) {
                Double unitPrice = productOptional.get().getUnitPrice() == null ? 0 : productOptional.get().getUnitPrice();
                Double singleProductTotalPrice = unitPrice * orderPlaceRequest1.getQuantity();
                totalPrice += singleProductTotalPrice;
                ordersItem.setProduct(productOptional.get());
                ordersItem.setUnit(orderPlaceRequest1.getQuantity());
                ordersItem.setPrice(singleProductTotalPrice);
                ordersItem.setIsActive(true);
                ordersItemList.add(ordersItem);

                //Stock adjustment
                if (productOptional.get().getOnStock() < orderPlaceRequest1.getQuantity()){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product " + productOptional.get().getName() + " out of stock");
                }
                productOptional.get().setOnStock(productOptional.get().getOnStock() - orderPlaceRequest1.getQuantity());
                productOptional.get().setUnitsOnOrder(productOptional.get().getUnitsOnOrder() + orderPlaceRequest1.getQuantity());
            }
        }

        Orders orders1 = null;
        if (ordersItemList != null || !ordersItemList.isEmpty()) {
            Orders orders = new Orders();
            orders.setOrderDate(LocalDateTime.now());
            orders.setRequiredDate(LocalDate.now().plusDays(3));
            orders.setOrderStatus(OrderStatus.PENDING);
            orders.setShippedDate(null);
            orders.setIsActive(true);
            orders.setUsers(users);
            orders.setDeliveryAddress(deliveryAddress);
            orders.setTotalPrice(totalPrice);
            orders.setIsSelfPayment(users.getIsSelfPayment());
            orders1 = ordersRepository.save(orders);
            orders1.setOrderNo("ORD-23" + orders.getId());
            orders1.setPaymentStatus(PaymentStatus.PENDING);
            orders1 = ordersRepository.save(orders1);
        }

        List<OrdersItem> ordersItemListWitOrder = new ArrayList<>();
        if (orders1 != null) {
            for (OrdersItem ordersItem : ordersItemList) {
                ordersItem.setOrders(orders1);
                ordersItemListWitOrder.add(ordersItem);
            }
        }

        if (ordersItemListWitOrder != null || !ordersItemListWitOrder.isEmpty()) {
            ordersItemRepository.saveAll(ordersItemListWitOrder);
            //Stripe Payment
            if(users.getIsSelfPayment() != null && users.getIsSelfPayment()){
                StripePaymentHistory paymentHistory = stripeClient.chargeNewCard(orderPlaceRequest.getPaymentToken(), totalPrice, orders1.getId());
                if (paymentHistory.getStatus().equals("succeeded")){
                    orders1.setPaymentStatus(PaymentStatus.PAID);
                }else {
                    orders1.setPaymentStatus(PaymentStatus.FAILED);
                }
                ordersRepository.save(orders1);
            }
        }

        return true;
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

        if (companyPolicy == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company policy not found");
        }

        if (currentMonthSale > companyPolicy.getLimitCost()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company policy cost limit cross");
        }
    }


    @Transactional
    public boolean changeOrderStatus(Long orderId, OrderStatus orderStatus, String comments) {


        Boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority(ROLE.ADMIN.toString());
        Boolean isMedicalStaff = SecurityUtils.hasCurrentUserThisAuthority(ROLE.MEDICAL_STUFF.toString());
        Boolean isTechnicalStaff = SecurityUtils.hasCurrentUserThisAuthority(ROLE.TECHNICAL_STAFF.toString());
        Boolean isEmployee = SecurityUtils.hasCurrentUserThisAuthority(ROLE.EMPLOYEE.toString());

        Optional<Orders> orders = ordersRepository.findById(orderId);
        Users users = userService.getCurrentUser();
        Boolean isChanged = false;

        if (!orders.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is not found");
        }

        if (isAdmin) {
            orders.get().setOrderStatus(orderStatus);
            isChanged = true;
        }

        if (isMedicalStaff) {
            if (orders.get().getOrderStatus() != null && orders.get().getOrderStatus().equals(OrderStatus.PENDING) && (orderStatus.equals(OrderStatus.APPROVED) || orderStatus.equals(OrderStatus.DENIED))) {
                orders.get().setOrderStatus(orderStatus);
                isChanged = true;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have no access");
            }
        }


        //Technical staff can do only approved to delivered
        if (isTechnicalStaff) {
            if (orders.get().getOrderStatus() != null && orders.get().getOrderStatus().equals(OrderStatus.APPROVED) && orderStatus.equals(OrderStatus.DELIVERED)) {
                orders.get().setOrderStatus(orderStatus);
                isChanged = true;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have no access");
            }
        }

        //Technical staff can do only approved to delivered
        if (isEmployee) {
            //if not own user then thow a bad request
            if (!users.getId().equals(orders.get().getUsers().getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have no access");
            }

            if (orders.get().getOrderStatus().equals(OrderStatus.PENDING) && orderStatus.equals(OrderStatus.CANCELLED)) {
                orders.get().setOrderStatus(orderStatus);
                isChanged = true;
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have no access");
            }
        }

        if (isChanged && (orderStatus.equals(OrderStatus.DELIVERED) || orderStatus.equals(OrderStatus.CANCELLED) || orderStatus.equals(OrderStatus.DENIED))){
            List<OrdersItem> ordersItemList = ordersItemRepository.findAllByOrders_Id(orderId);
            for (OrdersItem ordersItem : ordersItemList){
                if ( ordersItem.getProduct() != null){
                    Product product = ordersItem.getProduct();
                    product.setUnitsOnOrder(product.getUnitsOnOrder() - ordersItem.getUnit());
                }
            }
        }

        if (isChanged) {
            OrderApprove orderApprove = new OrderApprove();
            orderApprove.setOrders(orders.get());
            orderApprove.setApprovedBy(users);
            orderApprove.setComments(comments);
            orderApprove.setOrders(orders.get());
            orderApprove.setIsActive(true);
            orderApprove.setOrderStatus(orderStatus);
            orderApproveRepository.save(orderApprove);
        }
        return isChanged;

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
