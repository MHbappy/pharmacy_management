package com.pharmacy.management.service;

import com.pharmacy.management.model.Country;
import com.pharmacy.management.repository.CountryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CountryService {

    private final Logger log = LoggerFactory.getLogger(CountryService.class);

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    
    public Country save(Country country) {
        log.debug("Request to save Country : {}", country);
        country.setIsActive(true);
        return countryRepository.save(country);
    }

    
    public Optional<Country> partialUpdate(Country country) {
        log.debug("Request to partially update Country : {}", country);

        return countryRepository
            .findById(country.getId())
            .map(
                existingCountry -> {
                    if (country.getName() != null) {
                        existingCountry.setName(country.getName());
                    }
                    if (country.getIsActive() != null) {
                        existingCountry.setIsActive(country.getIsActive());
                    }

                    existingCountry.setIsActive(true);
                    return existingCountry;
                }
            )
            .map(countryRepository::save);
    }

    
    @Transactional(readOnly = true)
    public Page<Country> findAll(String name, Pageable pageable) {
        log.debug("Request to get all Countries");
        return countryRepository.findAllByIsActiveAndNameContaining(true, name, pageable);
    }


    @Transactional(readOnly = true)
    public List<Country> findAll(String name) {
        log.debug("Request to get all Countries");
        return countryRepository.findAllByIsActiveAndNameContaining(true, name);
    }

    @Transactional(readOnly = true)
    public Optional<Country> findOne(Long id) {
        log.debug("Request to get Country : {}", id);
        return countryRepository.findById(id);
    }

    public void delete(Long id) {
        log.debug("Request to delete Country : {}", id);
        Optional<Country> countryOptional = countryRepository.findById(id);
        countryOptional.ifPresentOrElse(country -> {
            country.setIsActive(false);
            countryRepository.save(country);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no category!");
        } );
    }
}
