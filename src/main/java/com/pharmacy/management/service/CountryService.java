package com.pharmacy.management.service;

import com.pharmacy.management.model.Country;
import com.pharmacy.management.repository.CountryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

                    return existingCountry;
                }
            )
            .map(countryRepository::save);
    }

    
    @Transactional(readOnly = true)
    public List<Country> findAll() {
        log.debug("Request to get all Countries");
        return countryRepository.findAll();
    }

    
    @Transactional(readOnly = true)
    public Optional<Country> findOne(Long id) {
        log.debug("Request to get Country : {}", id);
        return countryRepository.findById(id);
    }

    
    public void delete(Long id) {
        log.debug("Request to delete Country : {}", id);
        countryRepository.deleteById(id);
    }
}
