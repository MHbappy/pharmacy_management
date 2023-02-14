package com.pharmacy.management.service;

import com.pharmacy.management.model.City;
import com.pharmacy.management.model.Orders;
import com.pharmacy.management.repository.OrdersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class OrdersService {

    private final Logger log = LoggerFactory.getLogger(OrdersService.class);

    private final OrdersRepository ordersRepository;

    public OrdersService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    
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
