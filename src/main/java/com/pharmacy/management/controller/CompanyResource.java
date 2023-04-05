package com.pharmacy.management.controller;

import com.pharmacy.management.model.Company;
import com.pharmacy.management.repository.CompanyRepository;
import com.pharmacy.management.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CompanyResource {

    private final Logger log = LoggerFactory.getLogger(CompanyResource.class);

    private final CompanyService companyService;

    private final CompanyRepository companyRepository;

    public CompanyResource(CompanyService companyService, CompanyRepository companyRepository) {
        this.companyService = companyService;
        this.companyRepository = companyRepository;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@RequestBody @Valid Company company) throws URISyntaxException {
        log.debug("REST request to save Company : {}", company);
        if (company.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new company cannot already have an ID");
        }
        Optional<Company> companyOptional = companyRepository.findByNameAndIsActive(company.getName(), true);
        if (companyOptional.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company name should be unique");
        }

        Company result = companyService.save(company);
        return ResponseEntity
            .created(new URI("/api/companies/" + result.getId()))
            .body(result);
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable(value = "id", required = false) final Long id, @RequestBody Company company)
        throws URISyntaxException {
        log.debug("REST request to update Company : {}, {}", id, company);
        if (company.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id");
        }
        if (!Objects.equals(id, company.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID");
        }

        if (!companyRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entity not found");
        }

        Company result = companyService.save(company);
        return ResponseEntity
            .ok()
            .body(result);
    }

    @GetMapping("/companies")
    public Page<Company> getAllCompanies(@RequestParam(required = false, defaultValue = "") String name, Pageable pageable) {
        log.debug("REST request to get all Companies");
        return companyService.findAll(name, pageable);
    }



    @GetMapping("/all-companies")
    public List<Company> getAllCompanies(@RequestParam(required = false, defaultValue = "") String name) {
        log.debug("REST request to get all Companies");
        return companyService.findAll(name);
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompany(@PathVariable Long id) {
        log.debug("REST request to get Company : {}", id);
        Optional<Company> company = companyService.findOne(id);
        return ResponseEntity.ok(company.get());
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        log.debug("REST request to delete Company : {}", id);
        companyService.delete(id);

        return ResponseEntity
            .noContent()
            .build();
    }
}
