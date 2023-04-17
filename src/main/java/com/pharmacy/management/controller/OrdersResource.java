package com.pharmacy.management.controller;

import com.pharmacy.management.config.StripeClient;
import com.pharmacy.management.dto.request.OrderPlaceProductDto;
import com.pharmacy.management.dto.request.OrderPlaceRequest;
import com.pharmacy.management.dto.response.OrderDetailsDTO;
import com.pharmacy.management.model.*;
import com.pharmacy.management.model.enumeration.OrderStatus;
import com.pharmacy.management.model.enumeration.PaymentStatus;
import com.pharmacy.management.model.enumeration.ROLE;
import com.pharmacy.management.repository.*;
import com.pharmacy.management.security.SecurityUtils;
import com.pharmacy.management.service.OrdersService;
import com.pharmacy.management.service.UserService;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final OrderApproveRepository orderApproveRepository;
    private final StripeClient stripeClient;

    @PostMapping("/place-orders")
    public ResponseEntity<?> createOrders(@RequestBody OrderPlaceRequest orderPlaceRequest) throws StripeException {
        return ResponseEntity.ok(ordersService.createOrders(orderPlaceRequest));
    }

    @GetMapping("/orders-by-user")
    public Page<Orders> getAllOrders(@RequestParam(value = "orderNo", required = false) String orderNo, Pageable pageable) {
        log.debug("REST request to get all Orders");
        Boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority(ROLE.ADMIN.toString());

        //for admin user
        Users users = userService.getCurrentUser();
        if (isAdmin){
            if (orderNo != null && !orderNo.isEmpty()){
                return ordersRepository.findAllByOrderNo(orderNo, pageable);
            }
            return ordersRepository.findAll(pageable);
        }

        //for non admin user
        if (orderNo != null && !orderNo.isEmpty()) {
            return ordersRepository.findAllByUsersAndOrderNo(users, orderNo, pageable);
        }
        Page<Orders> ordersList = ordersRepository.findAllByUsers(users, pageable);
        return ordersList;
    }


    @GetMapping("/order-list-for-approve")
    public Page<Orders> getAllForApproveOrders(Pageable pageable) {
        log.debug("REST request to get all Orders");
        Boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority(ROLE.ADMIN.toString());
        Boolean isMedicalStaff = SecurityUtils.hasCurrentUserThisAuthority(ROLE.MEDICAL_STUFF.toString());
        Boolean isTechnicalStaff = SecurityUtils.hasCurrentUserThisAuthority(ROLE.TECHNICAL_STAFF.toString());
        Page<Orders> ordersList = new PageImpl<>(Collections.emptyList());
        ;

        if (isAdmin) {
            ordersList = ordersRepository.findAll(pageable);
        } else if (isMedicalStaff) {
            List<OrderStatus> orderStatuses = new ArrayList<>();
            orderStatuses.add(OrderStatus.APPROVED);
            orderStatuses.add(OrderStatus.PENDING);
            orderStatuses.add(OrderStatus.DENIED);
            ordersList = ordersRepository.findByOrderStatusIn(orderStatuses, pageable);
        } else if (isTechnicalStaff) {
            List<OrderStatus> orderStatuses = new ArrayList<>();
            orderStatuses.add(OrderStatus.APPROVED);
            orderStatuses.add(OrderStatus.DELIVERED);
            ordersList = ordersRepository.findByOrderStatusIn(orderStatuses, pageable);
        }
        return ordersList;
    }

    @GetMapping("/order-status-change-history")
    public List<OrderApprove> getAllForApproveOrders(@RequestParam("orderId") Long orderId) {
        return orderApproveRepository.findAllByOrders_Id(orderId);
    }

    //This is for print purpose
    @PostMapping("/current-order-check-limitation")
    public void getCurrentOrderDetailsDTO(@RequestBody List<OrderPlaceProductDto> productAndQuantityList, @RequestParam("categoryId") Long categoryId) {
        ordersService.checkLimitation(productAndQuantityList, categoryId);
    }

    @PostMapping("/change-order-status")
    public boolean changeOrderStatus(@RequestParam("orderId") Long orderId, @RequestParam(value = "orderStatus", required = false) OrderStatus orderStatus, @RequestParam(value = "comments", defaultValue = "") String comments) {
        return ordersService.changeOrderStatus(orderId, orderStatus, comments);
    }

    @GetMapping("/order-full-info-by-order")
    public OrderDetailsDTO getOrderDetailsDTO(@RequestParam Long orderId) {
        return ordersService.getOrdersFullDetailsByOrderId(orderId);
    }

    @GetMapping("/search-with-multiple-field")
    public Page<Orders> multiSearch(@RequestParam(required = false) Long companyId, @RequestParam(required = false) OrderStatus orderStatus, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, Pageable pageable) {
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (companyId == null && orderStatus == null && (startDate == null || !startDate.isEmpty()) && (endDate == null || endDate.isEmpty())) {
            return ordersRepository.findAll(pageable);
        } else if (companyId != null && orderStatus == null && startDate.isEmpty() && endDate.isEmpty()) {
            return ordersRepository.findAllOrdersByCompanyId(companyId, pageable);
        } else if (companyId != null && orderStatus != null && startDate.isEmpty() && endDate.isEmpty()) {
            return ordersRepository.findAllCompanyIdAndStatus(companyId, orderStatus.toString(), pageable);
        } else if (companyId != null && orderStatus != null && startDate != null) {
            LocalDate startDateParam = LocalDate.parse(startDate, myFormatObj);
            LocalDate endDateParam = LocalDate.now();
            try {
                endDateParam = LocalDate.parse(endDate, myFormatObj);
            }catch (Exception e){
            }
            if (endDate == null || endDate.isEmpty()) {
                endDateParam = LocalDate.now();
            }
            return ordersRepository.findAllCompanyIdAndStatusAndDate(companyId, orderStatus.toString(), startDateParam, endDateParam, pageable);
        }
        return ordersRepository.findAll(pageable);
    }

//

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
