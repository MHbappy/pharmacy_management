package com.pharmacy.management.service;

import com.pharmacy.management.dto.response.OrderDetailsDTO;
import com.pharmacy.management.model.City;
import com.pharmacy.management.model.DeliveryAddress;
import com.pharmacy.management.model.Orders;
import com.pharmacy.management.model.OrdersItem;
import com.pharmacy.management.projection.OrderDetailsProjection;
import com.pharmacy.management.projection.OrderItemsProjection;
import com.pharmacy.management.repository.DeliveryAddressRepository;
import com.pharmacy.management.repository.OrdersItemRepository;
import com.pharmacy.management.repository.OrdersRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
@AllArgsConstructor
public class OrdersService {

    private final Logger log = LoggerFactory.getLogger(OrdersService.class);
    private final OrdersRepository ordersRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final OrdersItemRepository ordersItemRepository;
    private final ModelMapper modelMapper;

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




    public OrderDetailsDTO getOrdersFullDetailsByOrderId(Long orderId){
        OrderDetailsProjection orderDetailsProjection = ordersRepository.getOrderDetailsByOrderId(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order not found!"));
        DeliveryAddress deliveryAddress = deliveryAddressRepository.getDeliveryAddressByOrderId(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Delivery address not found!"));
        List<OrderItemsProjection> allOrderItemsByOrderId = ordersItemRepository.getAllOrderItemByOrderId(orderId);
        if (allOrderItemsByOrderId == null || allOrderItemsByOrderId.isEmpty()){
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
        } );
    }
}
