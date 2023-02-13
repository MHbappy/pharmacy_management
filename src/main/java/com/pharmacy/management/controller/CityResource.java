package com.pharmacy.management.controller;

import com.pharmacy.management.model.City;
import com.pharmacy.management.repository.CityRepository;
import com.pharmacy.management.service.CityService;
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
public class CityResource {

    private final Logger log = LoggerFactory.getLogger(CityResource.class);


    private final CityService cityService;

    private final CityRepository cityRepository;

    public CityResource(CityService cityService, CityRepository cityRepository) {
        this.cityService = cityService;
        this.cityRepository = cityRepository;
    }

    @PostMapping("/cities")
    public ResponseEntity<City> createCity(@RequestBody City city) throws URISyntaxException {
        log.debug("REST request to save City : {}", city);
        if (city.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new city cannot already have an ID");
        }
        City result = cityService.save(city);
        return ResponseEntity
            .created(new URI("/api/cities/" + result.getId()))
            .body(result);
    }

    @PutMapping("/cities/{id}")
    public ResponseEntity<City> updateCity(@PathVariable(value = "id", required = false) final Long id, @RequestBody City city)
        throws URISyntaxException {
        log.debug("REST request to update City : {}, {}", id, city);
        if (city.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        if (!Objects.equals(id, city.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }

        if (!cityRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }

        City result = cityService.save(city);
        return ResponseEntity
            .ok()
            .body(result);
    }


    @GetMapping("/cities")
    public List<City> getAllCities() {
        log.debug("REST request to get all Cities");
        return cityService.findAll();
    }

    @GetMapping("/cities/{id}")
    public ResponseEntity<City> getCity(@PathVariable Long id) {
        log.debug("REST request to get City : {}", id);
        Optional<City> city = cityService.findOne(id);
        return ResponseEntity.ok(city.get());
    }

    @DeleteMapping("/cities/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        log.debug("REST request to delete City : {}", id);
        cityService.delete(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
