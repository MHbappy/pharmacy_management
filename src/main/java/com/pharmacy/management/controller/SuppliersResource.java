package com.pharmacy.management.controller;


import com.pharmacy.management.model.Suppliers;
import com.pharmacy.management.repository.SuppliersRepository;
import com.pharmacy.management.service.SuppliersService;
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
public class SuppliersResource {
    private final Logger log = LoggerFactory.getLogger(SuppliersResource.class);

    private final SuppliersService suppliersService;

    private final SuppliersRepository suppliersRepository;

    public SuppliersResource(SuppliersService suppliersService, SuppliersRepository suppliersRepository) {
        this.suppliersService = suppliersService;
        this.suppliersRepository = suppliersRepository;
    }

    @PostMapping("/suppliers")
    public ResponseEntity<Suppliers> createSuppliers(@RequestBody Suppliers suppliers) throws URISyntaxException {
        log.debug("REST request to save Suppliers : {}", suppliers);
        if (suppliers.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new suppliers cannot already have an ID");
        }
        Suppliers result = suppliersService.save(suppliers);
        return ResponseEntity
            .created(new URI("/api/suppliers/" + result.getId()))
            .body(result);
    }


    @PutMapping("/suppliers/{id}")
    public ResponseEntity<Suppliers> updateSuppliers(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Suppliers suppliers
    ) {
        log.debug("REST request to update Suppliers : {}, {}", id, suppliers);
        if (suppliers.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        if (!Objects.equals(id, suppliers.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");        }

        if (!suppliersRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }

        Suppliers result = suppliersService.save(suppliers);
        return ResponseEntity
            .ok()
            .body(result);
    }

    @GetMapping("/suppliers")
    public List<Suppliers> getAllSuppliers() {
        return suppliersService.findAll();
    }

    @GetMapping("/suppliers/{id}")
    public ResponseEntity<Suppliers> getSuppliers(@PathVariable Long id) {
        log.debug("REST request to get Suppliers : {}", id);
        Optional<Suppliers> suppliers = suppliersService.findOne(id);
        return ResponseEntity.ok(suppliers.get());
    }

    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<Void> deleteSuppliers(@PathVariable Long id) {
        log.debug("REST request to delete Suppliers : {}", id);
        suppliersService.delete(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
