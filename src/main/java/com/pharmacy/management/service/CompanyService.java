package com.pharmacy.management.service;

import com.pharmacy.management.model.Company;
import com.pharmacy.management.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CompanyService {
    private final Logger log = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    
    public Company save(Company company) {
        log.debug("Request to save Company : {}", company);
        company.setIsActive(true);
        return companyRepository.save(company);
    }

    
    public Optional<Company> partialUpdate(Company company) {
        log.debug("Request to partially update Company : {}", company);

        return companyRepository
            .findById(company.getId())
            .map(
                existingCompany -> {
                    company.setIsActive(true);
                    if (company.getName() != null) {
                        existingCompany.setName(company.getName());
                    }
                    if (company.getAddress1() != null) {
                        existingCompany.setAddress1(company.getAddress1());
                    }
                    if (company.getAddress() != null) {
                        existingCompany.setAddress(company.getAddress());
                    }
                    if (company.getPhone() != null) {
                        existingCompany.setPhone(company.getPhone());
                    }
                    if (company.getMobile() != null) {
                        existingCompany.setMobile(company.getMobile());
                    }
                    if (company.getMobilePhone() != null) {
                        existingCompany.setMobilePhone(company.getMobilePhone());
                    }
                    if (company.getEmail() != null) {
                        existingCompany.setEmail(company.getEmail());
                    }
                    if (company.getIsActive() != null) {
                        existingCompany.setIsActive(company.getIsActive());
                    }

                    return existingCompany;
                }
            )
            .map(companyRepository::save);
    }

    
    @Transactional(readOnly = true)
    public List<Company> findAll() {
        log.debug("Request to get all Companies");
        return companyRepository.findAllByIsActive(true);
    }

    
    @Transactional(readOnly = true)
    public Optional<Company> findOne(Long id) {
        log.debug("Request to get Company : {}", id);
        return companyRepository.findById(id);
    }

    
    public void delete(Long id) {
        log.debug("Request to delete Category : {}", id);
        Optional<Company> companyOptional = companyRepository.findById(id);
        companyOptional.ifPresentOrElse(category -> {
            category.setIsActive(false);
            companyRepository.save(category);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no category!");
        } );
    }
}
