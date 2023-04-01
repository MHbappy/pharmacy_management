package com.pharmacy.management.controller;

import com.pharmacy.management.dto.response.OrderDetailsDTO;
import com.pharmacy.management.model.OrdersItem;
import com.pharmacy.management.projection.OrderItemsProjection;
import com.pharmacy.management.repository.OrdersItemRepository;
import com.pharmacy.management.service.OrdersItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class OrdersItemResource {

    private final Logger log = LoggerFactory.getLogger(OrdersItemResource.class);

    private final OrdersItemService ordersItemService;

    private final OrdersItemRepository ordersItemRepository;

    public OrdersItemResource(OrdersItemService ordersItemService, OrdersItemRepository ordersItemRepository) {
        this.ordersItemService = ordersItemService;
        this.ordersItemRepository = ordersItemRepository;
    }


    @GetMapping("/orders-items-by-order")
    public List<OrderItemsProjection> getAllOrdersItemsByOrders(@RequestParam Long orderId) {
        log.debug("REST request to get all OrdersItems");
        List<OrderItemsProjection> ordersItemList = ordersItemRepository.getAllOrderItemByOrderId(orderId);
        if (ordersItemList == null || ordersItemList.isEmpty()){
            return new ArrayList<>();
        }
        return ordersItemList;
    }





//    @PostMapping("/orders-items")
//    public ResponseEntity<OrdersItem> createOrdersItem(@RequestBody OrdersItem ordersItem) throws URISyntaxException {
//        log.debug("REST request to save OrdersItem : {}", ordersItem);
//        if (ordersItem.getId() != null) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new ordersItem cannot already have an ID");
//        }
//        OrdersItem result = ordersItemService.save(ordersItem);
//        return ResponseEntity
//            .created(new URI("/api/orders-items/" + result.getId()))
//            .body(result);
//    }
//
//    @PutMapping("/orders-items/{id}")
//    public ResponseEntity<OrdersItem> updateOrdersItem(
//        @PathVariable(value = "id", required = false) final Long id,
//        @RequestBody OrdersItem ordersItem
//    ) throws URISyntaxException {
//        log.debug("REST request to update OrdersItem : {}, {}", id, ordersItem);
//        if (ordersItem.getId() == null) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
//        }
//        if (!Objects.equals(id, ordersItem.getId())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
//        }
//
//        if (!ordersItemRepository.existsById(id)) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
//        }
//
//        OrdersItem result = ordersItemService.save(ordersItem);
//        return ResponseEntity
//            .ok()
//            .body(result);
//    }
//
//    @GetMapping("/orders-items")
//    public List<OrdersItem> getAllOrdersItems() {
//        log.debug("REST request to get all OrdersItems");
//        return ordersItemService.findAll();
//    }
//
//    @GetMapping("/orders-items/{id}")
//    public ResponseEntity<OrdersItem> getOrdersItem(@PathVariable Long id) {
//        log.debug("REST request to get OrdersItem : {}", id);
//        Optional<OrdersItem> ordersItem = ordersItemService.findOne(id);
//        return ResponseEntity.ok(ordersItem.get());
//    }

}
