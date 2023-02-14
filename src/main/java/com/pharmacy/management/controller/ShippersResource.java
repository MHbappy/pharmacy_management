package com.pharmacy.management.controller;

import com.pharmacy.management.model.Shippers;
import com.pharmacy.management.repository.ShippersRepository;
import com.pharmacy.management.service.ShippersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
public class ShippersResource {

    private final Logger log = LoggerFactory.getLogger(ShippersResource.class);

    private final ShippersService shippersService;

    private final ShippersRepository shippersRepository;

    public ShippersResource(ShippersService shippersService, ShippersRepository shippersRepository) {
        this.shippersService = shippersService;
        this.shippersRepository = shippersRepository;
    }

    @PostMapping("/shippers")
    public ResponseEntity<Shippers> createShippers(@RequestBody Shippers shippers) throws URISyntaxException {
        log.debug("REST request to save Shippers : {}", shippers);
        if (shippers.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new shippers cannot already have an ID");
        }
        Shippers result = shippersService.save(shippers);
        return ResponseEntity
            .created(new URI("/api/shippers/" + result.getId()))
            .body(result);
    }

    @PutMapping("/shippers/{id}")
    public ResponseEntity<Shippers> updateShippers(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Shippers shippers
    ) throws URISyntaxException {
        log.debug("REST request to update Shippers : {}, {}", id, shippers);
        if (shippers.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        if (!Objects.equals(id, shippers.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }

        if (!shippersRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }

        Shippers result = shippersService.save(shippers);
        return ResponseEntity
            .ok()
            .body(result);
    }

    @GetMapping("/shippers")
    public List<Shippers> getAllShippers() {
        log.debug("REST request to get all Shippers");
        return shippersService.findAll();
    }

    @GetMapping("/shippers/{id}")
    public ResponseEntity<Shippers> getShippers(@PathVariable Long id) {
        log.debug("REST request to get Shippers : {}", id);
        Optional<Shippers> shippers = shippersService.findOne(id);
        return ResponseEntity.ok(shippers.get());
    }

    @DeleteMapping("/shippers/{id}")
    public ResponseEntity<Void> deleteShippers(@PathVariable Long id) {
        log.debug("REST request to delete Shippers : {}", id);
        shippersService.delete(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
