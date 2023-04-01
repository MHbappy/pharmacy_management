package com.pharmacy.management.controller;

import com.pharmacy.management.model.CompanyPolicy;
import com.pharmacy.management.repository.CompanyPolicyRepository;
import com.pharmacy.management.service.CompanyPolicyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
public class CompanyPolicyResource {

    private final Logger log = LoggerFactory.getLogger(CompanyPolicyResource.class);
    private final CompanyPolicyService companyPolicyService;
    private final CompanyPolicyRepository companyPolicyRepository;

    public CompanyPolicyResource(CompanyPolicyService companyPolicyService, CompanyPolicyRepository companyPolicyRepository) {
        this.companyPolicyService = companyPolicyService;
        this.companyPolicyRepository = companyPolicyRepository;
    }

    @PostMapping("/company-policies")
    public ResponseEntity<CompanyPolicy> createCompanyPolicy(@RequestBody @Valid CompanyPolicy companyPolicy) throws URISyntaxException {
        log.debug("REST request to save CompanyPolicy : {}", companyPolicy);
        if (companyPolicy.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new companyPolicy cannot already have an ID");
        }
        CompanyPolicy result = companyPolicyService.save(companyPolicy);
        return ResponseEntity
            .created(new URI("/api/company-policies/" + result.getId()))
            .body(result);
    }

    @PutMapping("/company-policies/{id}")
    public ResponseEntity<CompanyPolicy> updateCompanyPolicy(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody @Valid CompanyPolicy companyPolicy
    ) {
        log.debug("REST request to update CompanyPolicy : {}, {}", id, companyPolicy);
        if (companyPolicy.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        if (!Objects.equals(id, companyPolicy.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }

        if (!companyPolicyRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }

        CompanyPolicy result = companyPolicyService.save(companyPolicy);
        return ResponseEntity
            .ok()
            .body(result);
    }

    @GetMapping("/company-policies")
    public List<CompanyPolicy> getAllCompanyPolicies() {
        log.debug("REST request to get all CompanyPolicies");
        return companyPolicyService.findAll();
    }

    @GetMapping("/company-policies-by-company-id")
    public List<CompanyPolicy> getAllCompanyPoliciesByCompanyId(@RequestParam Long companyId) {
        log.debug("REST request to get all CompanyPolicies by company Id");
        return companyPolicyService.findAllCompanyPolicyByCompanyId(companyId);
    }

    @GetMapping("/company-policies/{id}")
    public ResponseEntity<CompanyPolicy> getCompanyPolicy(@PathVariable Long id) {
        log.debug("REST request to get CompanyPolicy : {}", id);
        Optional<CompanyPolicy> companyPolicy = companyPolicyService.findOne(id);
        return ResponseEntity.ok(companyPolicy.get());
    }

    @DeleteMapping("/company-policies/{id}")
    public ResponseEntity<Void> deleteCompanyPolicy(@PathVariable Long id) {
        companyPolicyService.delete(id);
        return ResponseEntity
            .noContent()
            .build();
    }
}
