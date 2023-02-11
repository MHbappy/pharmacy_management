package com.pharmacy.management.service;

import com.pharmacy.management.model.OrdersItem;
import com.pharmacy.management.repository.OrdersItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class OrdersItemService {

    private final Logger log = LoggerFactory.getLogger(OrdersItemService.class);

    private final OrdersItemRepository ordersItemRepository;

    public OrdersItemService(OrdersItemRepository ordersItemRepository) {
        this.ordersItemRepository = ordersItemRepository;
    }

    
    public OrdersItem save(OrdersItem ordersItem) {
        log.debug("Request to save OrdersItem : {}", ordersItem);
        return ordersItemRepository.save(ordersItem);
    }

    
    public Optional<OrdersItem> partialUpdate(OrdersItem ordersItem) {
        log.debug("Request to partially update OrdersItem : {}", ordersItem);

        return ordersItemRepository
            .findById(ordersItem.getId())
            .map(
                existingOrdersItem -> {
                    if (ordersItem.getPrice() != null) {
                        existingOrdersItem.setPrice(ordersItem.getPrice());
                    }
                    if (ordersItem.getIsActive() != null) {
                        existingOrdersItem.setIsActive(ordersItem.getIsActive());
                    }

                    return existingOrdersItem;
                }
            )
            .map(ordersItemRepository::save);
    }

    
    @Transactional(readOnly = true)
    public List<OrdersItem> findAll() {
        log.debug("Request to get all OrdersItems");
        return ordersItemRepository.findAll();
    }

    
    @Transactional(readOnly = true)
    public Optional<OrdersItem> findOne(Long id) {
        log.debug("Request to get OrdersItem : {}", id);
        return ordersItemRepository.findById(id);
    }

    
    public void delete(Long id) {
        log.debug("Request to delete OrdersItem : {}", id);
        ordersItemRepository.deleteById(id);
    }
}
