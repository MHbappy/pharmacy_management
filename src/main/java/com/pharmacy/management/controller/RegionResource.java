package com.pharmacy.management.controller;

import com.pharmacy.management.model.Region;
import com.pharmacy.management.repository.RegionRepository;
import com.pharmacy.management.service.RegionService;
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
public class RegionResource {
    private final Logger log = LoggerFactory.getLogger(RegionResource.class);

    private final RegionService regionService;

    private final RegionRepository regionRepository;

    public RegionResource(RegionService regionService, RegionRepository regionRepository) {
        this.regionService = regionService;
        this.regionRepository = regionRepository;
    }

    @PostMapping("/regions")
    public ResponseEntity<Region> createRegion(@RequestBody Region region) throws URISyntaxException {
        log.debug("REST request to save Region : {}", region);
        if (region.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new region cannot already have an ID");
        }
        Region result = regionService.save(region);
        return ResponseEntity
            .created(new URI("/api/regions/" + result.getId()))
            .body(result);
    }

    @PutMapping("/regions/{id}")
    public ResponseEntity<Region> updateRegion(@PathVariable(value = "id", required = false) final Long id, @RequestBody Region region)
        throws URISyntaxException {
        log.debug("REST request to update Region : {}, {}", id, region);
        if (region.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }

        if (!Objects.equals(id, region.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }

        if (!regionRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }

        Region result = regionService.save(region);
        return ResponseEntity
            .ok()
            .body(result);
    }

    @GetMapping("/regions")
    public Page<Region> getAllRegions(@RequestParam(defaultValue = "", required = false) String name, Pageable pageable) {
        log.debug("REST request to get all Regions");
        return regionService.findAll(name, pageable);
    }

    @GetMapping("/regions-by-city-id/{cityId}")
    public List<Region> getAllRegionsByCityId(@RequestParam("cityId") Long cityId) {
        log.debug("REST request to get all Regions");
        return regionService.findAllByCityId(cityId);
    }

    @GetMapping("/regions/{id}")
    public ResponseEntity<Region> getRegion(@PathVariable Long id) {
        log.debug("REST request to get Region : {}", id);
        Optional<Region> region = regionService.findOne(id);
        return ResponseEntity.ok(region.get());
    }

    @DeleteMapping("/regions/{id}")
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
        log.debug("REST request to delete Region : {}", id);
        regionService.delete(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
