package com.pharmacy.management.controller;

import com.pharmacy.management.model.Provider;
import com.pharmacy.management.repository.ProviderRepository;
import com.pharmacy.management.service.ProviderService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ProviderResource {
    private final Logger log = LoggerFactory.getLogger(ProviderResource.class);

    private final ProviderService providerService;

    private final ProviderRepository providerRepository;

    @PostMapping("/providers")
    public ResponseEntity<Provider> createProvider(@RequestBody @Valid Provider provider) throws URISyntaxException {
        log.debug("REST request to save Provider : {}", provider);
        if (provider.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new provider cannot already have an ID");
        }
        Provider result = providerService.save(provider);
        return ResponseEntity
            .created(new URI("/api/providers/" + result.getId()))
            .body(result);
    }

    @PutMapping("/providers/{id}")
    public ResponseEntity<Provider> updateProvider(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Provider provider
    ) throws URISyntaxException {
        log.debug("REST request to update Provider : {}, {}", id, provider);
        if (provider.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID");
        }
        if (!Objects.equals(id, provider.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID");
        }

        if (!providerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }

        Provider result = providerService.save(provider);
        return ResponseEntity
            .ok()
            .body(result);
    }


    @GetMapping("/providers")
    public Page<Provider> getAllProviders(Pageable pageable) {
        return providerService.findAll(pageable);
    }


    @GetMapping("/all-providers")
    public List<Provider> getAllProviders() {
        return providerService.allProvider();
    }


    @GetMapping("/all-providers-by-categoryId")
    public List<Provider> getAllProviderByCategoryId(@RequestParam Long categoryId) {
        return providerService.allProviderByCategoryId(categoryId);
    }

    @GetMapping("/providers/{id}")
    public ResponseEntity<Provider> getProvider(@PathVariable Long id) {
        log.debug("REST request to get Provider : {}", id);
        Optional<Provider> provider = providerService.findOne(id);
        return ResponseEntity.ok(provider.get());
    }


    @DeleteMapping("/providers/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id) {
        log.debug("REST request to delete Provider : {}", id);
        providerService.delete(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
