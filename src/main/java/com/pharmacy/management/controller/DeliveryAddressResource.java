package com.pharmacy.management.controller;

import com.pharmacy.management.dto.request.DeliveryAddressRequestDTO;
import com.pharmacy.management.model.DeliveryAddress;
import com.pharmacy.management.model.Users;
import com.pharmacy.management.repository.DeliveryAddressRepository;
import com.pharmacy.management.service.DeliveryAddressService;
import com.pharmacy.management.service.UserService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class DeliveryAddressResource {

    private final Logger log = LoggerFactory.getLogger(DeliveryAddressResource.class);
    private final DeliveryAddressService deliveryAddressService;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final UserService userService;


    @PostMapping("/delivery-addresses")
    public ResponseEntity<DeliveryAddress> createDeliveryAddress(@RequestBody DeliveryAddress deliveryAddress) throws URISyntaxException {
        log.debug("REST request to save DeliveryAddress : {}", deliveryAddress);
        if (deliveryAddress.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new deliveryAddress cannot already have an ID");
        }
        DeliveryAddress result = deliveryAddressService.save(deliveryAddress);
        return ResponseEntity
            .created(new URI("/api/delivery-addresses/" + result.getId()))
            .body(result);
    }

    @PutMapping("/delivery-addresses/{id}")
    public ResponseEntity<DeliveryAddress> updateDeliveryAddress(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DeliveryAddressRequestDTO deliveryAddress
    )  {
        log.debug("REST request to update DeliveryAddress : {}, {}", id, deliveryAddress);
        if (deliveryAddress.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        if (!Objects.equals(id, deliveryAddress.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }

        if (!deliveryAddressRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }
        DeliveryAddress result = deliveryAddressService.saveDeliveryAddress(deliveryAddress);
        return ResponseEntity
            .ok()
            .body(result);
    }

//    @GetMapping("/delivery-addresses")
//    public List<DeliveryAddress> getAllDeliveryAddresses() {
//        log.debug("REST request to get all DeliveryAddresses");
//        return deliveryAddressService.findAll();
//    }

    @GetMapping("/delivery-addresses-by-user")
    public List<DeliveryAddress> getAllDeliveryAddressesByUserId() {
        log.debug("REST request to get all DeliveryAddresses");
        Users users = userService.getCurrentUser();
        return deliveryAddressService.findAllByUserId(users.getId());
    }


    @GetMapping("/delivery-addresses/{id}")
    public ResponseEntity<DeliveryAddress> getDeliveryAddress(@PathVariable Long id) {
        log.debug("REST request to get DeliveryAddress : {}", id);
        Optional<DeliveryAddress> deliveryAddress = deliveryAddressService.findOne(id);
        return ResponseEntity.ok(deliveryAddress.get());
    }

    @DeleteMapping("/delivery-addresses/{id}")
    public ResponseEntity<Void> deleteDeliveryAddress(@PathVariable Long id) {
        log.debug("REST request to delete DeliveryAddress : {}", id);
        deliveryAddressService.delete(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
