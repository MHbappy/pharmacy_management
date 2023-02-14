package com.pharmacy.management.controller;

import com.pharmacy.management.model.Orders;
import com.pharmacy.management.repository.OrdersRepository;
import com.pharmacy.management.service.OrdersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class OrdersResource {

    private final Logger log = LoggerFactory.getLogger(OrdersResource.class);

    private final OrdersService ordersService;

    private final OrdersRepository ordersRepository;

    public OrdersResource(OrdersService ordersService, OrdersRepository ordersRepository) {
        this.ordersService = ordersService;
        this.ordersRepository = ordersRepository;
    }

    @PostMapping("/orders")
    public ResponseEntity<Orders> createOrders(@RequestBody Orders orders) throws URISyntaxException {
        log.debug("REST request to save Orders : {}", orders);
        if (orders.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new orders cannot already have an ID");
        }
        Orders result = ordersService.save(orders);
        return ResponseEntity
            .created(new URI("/api/orders/" + result.getId()))
            .body(result);
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<Orders> updateOrders(@PathVariable(value = "id", required = false) final Long id, @RequestBody Orders orders)
        throws URISyntaxException {
        log.debug("REST request to update Orders : {}, {}", id, orders);
        if (orders.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        if (!Objects.equals(id, orders.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }

        if (!ordersRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }

        Orders result = ordersService.save(orders);
        return ResponseEntity
            .ok()
            .body(result);
    }

    @GetMapping("/orders")
    public List<Orders> getAllOrders() {
        log.debug("REST request to get all Orders");
        return ordersService.findAll();
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Orders> getOrders(@PathVariable Long id) {
        log.debug("REST request to get Orders : {}", id);
        Optional<Orders> orders = ordersService.findOne(id);
        return ResponseEntity.ok(orders.get());
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrders(@PathVariable Long id) {
        log.debug("REST request to delete Orders : {}", id);
        ordersService.delete(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
