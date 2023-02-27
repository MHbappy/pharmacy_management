package com.pharmacy.management.controller;

import com.pharmacy.management.model.Country;
import com.pharmacy.management.repository.CountryRepository;
import com.pharmacy.management.service.CountryService;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CountryResource {

    private final Logger log = LoggerFactory.getLogger(CountryResource.class);

    private final CountryService countryService;

    private final CountryRepository countryRepository;

    public CountryResource(CountryService countryService, CountryRepository countryRepository) {
        this.countryService = countryService;
        this.countryRepository = countryRepository;
    }

    @PostMapping("/countries")
    public ResponseEntity<Country> createCountry(@RequestBody Country country) throws URISyntaxException {
        log.debug("REST request to save Country : {}", country);
        if (country.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new country cannot already have an ID");
        }
        Country result = countryService.save(country);
        return ResponseEntity
            .created(new URI("/api/countries/" + result.getId()))
            .body(result);
    }

    @PutMapping("/countries/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable(value = "id", required = false) final Long id, @RequestBody Country country)
        throws URISyntaxException {
        log.debug("REST request to update Country : {}, {}", id, country);
        if (country.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        if (!Objects.equals(id, country.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }

        if (!countryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }

        Country result = countryService.save(country);
        return ResponseEntity
            .ok()
            .body(result);
    }

    @GetMapping("/countries")
    public Page<Country> getAllCountries(@RequestParam(required = false, defaultValue = "") String name, Pageable pageable) {
        log.debug("REST request to get all Countries");
        return countryService.findAll(name, pageable);
    }

    @GetMapping("/countries/{id}")
    public ResponseEntity<Country> getCountry(@PathVariable Long id) {
        log.debug("REST request to get Country : {}", id);
        Optional<Country> country = countryService.findOne(id);
        return ResponseEntity.ok(country.get());
    }

    @DeleteMapping("/countries/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {
        log.debug("REST request to delete Country : {}", id);
        countryService.delete(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
