package com.pharmacy.management.controller;

import com.pharmacy.management.dto.request.OrderPlaceProductDto;
import com.pharmacy.management.dto.request.OrderPlaceRequest;
import com.pharmacy.management.model.Orders;
import com.pharmacy.management.model.OrdersItem;
import com.pharmacy.management.model.Product;
import com.pharmacy.management.model.Users;
import com.pharmacy.management.model.enumeration.DeliveryStatus;
import com.pharmacy.management.repository.OrdersItemRepository;
import com.pharmacy.management.repository.OrdersRepository;
import com.pharmacy.management.repository.ProductRepository;
import com.pharmacy.management.service.OrdersService;
import com.pharmacy.management.service.UserService;
import lombok.AllArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class OrdersResource {
    private final Logger log = LoggerFactory.getLogger(OrdersResource.class);
    private final OrdersService ordersService;
    private final OrdersRepository ordersRepository;
    private final ProductRepository productRepository;
    private final OrdersItemRepository ordersItemRepository;
    private final UserService userService;

    @PostMapping("/place-orders")
    public ResponseEntity<?> createOrders(@RequestBody OrderPlaceRequest orderPlaceRequest) {
        if (orderPlaceRequest.getProductAndQuantityList() == null || orderPlaceRequest.getProductAndQuantityList().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please add some product!");
        }
        if (orderPlaceRequest.getDeliveryAddressId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please add address!");
        }
        if (orderPlaceRequest.getCategoryId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found");
        }
        Users users = userService.getCurrentUser();

        Double totalPrice = 0d;
        List<OrdersItem> ordersItemList = new ArrayList<>();
        for (OrderPlaceProductDto orderPlaceRequest1 : orderPlaceRequest.getProductAndQuantityList()) {
            OrdersItem ordersItem = new OrdersItem();
            Optional<Product> productOptional = productRepository.findById(orderPlaceRequest1.getProductId());
            if (!productOptional.isEmpty()) {
                Double unitPrice = productOptional.get().getUnitPrice() == null ? 0 : productOptional.get().getUnitPrice();
                Double singleProductTotalPrice = unitPrice * orderPlaceRequest1.getQuantity();
                totalPrice += singleProductTotalPrice;
                ordersItem.setProduct(productOptional.get());
                ordersItem.setUnit(orderPlaceRequest1.getQuantity());
                ordersItem.setPrice(singleProductTotalPrice);
                ordersItem.setIsActive(true);
                ordersItemList.add(ordersItem);
            }
        }

        Orders orders1 = null;
        if (ordersItemList != null || !ordersItemList.isEmpty()) {
            Orders orders = new Orders();
            orders.setOrderDate(LocalDateTime.now());
            orders.setRequiredDate(LocalDate.now().plusDays(3));
            orders.setDeliveryStatus(DeliveryStatus.PENDING);
            orders.setShippedDate(null);
            orders.setIsActive(true);
            orders.setUsers(users);
            orders.setTotalPrice(totalPrice);
            orders1 = ordersRepository.save(orders);
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
        }
        return ResponseEntity.ok(true);
    }


    @GetMapping("/orders-by-user")
    public Page<Orders> getAllOrders(Pageable pageable) {
        log.debug("REST request to get all Orders");
        Users users = userService.getCurrentUser();
        Page<Orders> ordersList = ordersRepository.findAllByUsers(users, pageable);
        return ordersList;
    }


//    @PostMapping("/orders")
//    public ResponseEntity<Orders> createOrders(@RequestBody Orders orders) throws URISyntaxException {
//        log.debug("REST request to save Orders : {}", orders);
//        if (orders.getId() != null) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new orders cannot already have an ID");
//        }
//        Orders result = ordersService.save(orders);
//        return ResponseEntity
//            .created(new URI("/api/orders/" + result.getId()))
//            .body(result);
//    }
//
//    @PutMapping("/orders/{id}")
//    public ResponseEntity<Orders> updateOrders(@PathVariable(value = "id", required = false) final Long id, @RequestBody Orders orders)
//        throws URISyntaxException {
//        log.debug("REST request to update Orders : {}, {}", id, orders);
//        if (orders.getId() == null) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
//        }
//        if (!Objects.equals(id, orders.getId())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
//        }
//
//        if (!ordersRepository.existsById(id)) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
//        }
//
//        Orders result = ordersService.save(orders);
//        return ResponseEntity
//            .ok()
//            .body(result);
//    }
//
//    @GetMapping("/orders")
//    public List<Orders> getAllOrders() {
//        log.debug("REST request to get all Orders");
//        return ordersService.findAll();
//    }
//
//    @GetMapping("/orders/{id}")
//    public ResponseEntity<Orders> getOrders(@PathVariable Long id) {
//        log.debug("REST request to get Orders : {}", id);
//        Optional<Orders> orders = ordersService.findOne(id);
//        return ResponseEntity.ok(orders.get());
//    }
//
//    @DeleteMapping("/orders/{id}")
//    public ResponseEntity<Void> deleteOrders(@PathVariable Long id) {
//        log.debug("REST request to delete Orders : {}", id);
//        ordersService.delete(id);
//        return ResponseEntity
//            .noContent()
//            .build();
//    }
}
