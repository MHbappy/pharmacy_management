package com.pharmacy.management.controller;

import com.pharmacy.management.dto.request.OrderPlaceProductDto;
import com.pharmacy.management.dto.request.OrderPlaceRequest;
import com.pharmacy.management.dto.response.OrderDetailsDTO;
import com.pharmacy.management.model.*;
import com.pharmacy.management.model.enumeration.OrderStatus;
import com.pharmacy.management.model.enumeration.ROLE;
import com.pharmacy.management.repository.*;
import com.pharmacy.management.security.SecurityUtils;
import com.pharmacy.management.service.OrdersService;
import com.pharmacy.management.service.UserService;
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

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByIdAndUsersAndIsActive(orderPlaceRequest.getDeliveryAddressId(), users, true);
        if (deliveryAddress == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "In correct delivery address!");
        }

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
            orders.setOrderStatus(OrderStatus.PENDING);
            orders.setShippedDate(null);
            orders.setIsActive(true);
            orders.setUsers(users);
            orders.setDeliveryAddress(deliveryAddress);
            orders.setTotalPrice(totalPrice);
            orders1 = ordersRepository.save(orders);
            orders1.setOrderNo("ORD-23" + orders.getId());
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
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping("/orders-by-user")
    public Page<Orders> getAllOrders(@RequestParam(value = "orderNo", required = false) String orderNo, Pageable pageable) {
        log.debug("REST request to get all Orders");
        Boolean isAdmin = SecurityUtils.hasCurrentUserThisAuthority(ROLE.ADMIN.toString());

        //for admin user
        Users users = userService.getCurrentUser();
        if (isAdmin) {
            if (orderNo != null && !orderNo.isEmpty()) {
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

    @GetMapping("/order-full-info-by-order")
    public OrderDetailsDTO getOrderDetailsDTO(@RequestParam Long orderId) {
        return ordersService.getOrdersFullDetailsByOrderId(orderId);
    }

    @GetMapping("/search-with-multiple-field")
    public Page<Orders> multiSearch(@RequestParam(required = false) Long companyId, @RequestParam(required = false) OrderStatus orderStatus, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, Pageable pageable) {
        if (companyId == null && orderStatus == null && startDate == null && endDate == null) {
            return ordersRepository.findAll(pageable);
        } else if (companyId != null && orderStatus == null && startDate == null && endDate == null) {
            return ordersRepository.findAllOrdersByCompanyId(companyId, pageable);
        } else if (companyId != null && orderStatus != null && startDate == null && endDate == null) {
            return ordersRepository.findAllCompanyIdAndStatus(companyId, orderStatus.toString(), pageable);
        } else if (companyId != null && orderStatus != null && startDate != null) {
            if (endDate == null) {
                endDate = LocalDate.now().toString();
            }
            return ordersRepository.findAllCompanyIdAndStatusAndDate(companyId, orderStatus.toString(), startDate, endDate, pageable);
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
