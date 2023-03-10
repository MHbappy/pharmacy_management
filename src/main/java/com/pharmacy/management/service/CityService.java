package com.pharmacy.management.service;

import com.pharmacy.management.model.City;
import com.pharmacy.management.model.Region;
import com.pharmacy.management.repository.CityRepository;
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
public class CityService {

    private final Logger log = LoggerFactory.getLogger(CityService.class);

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    
    public City save(City city) {
        log.debug("Request to save City : {}", city);
        city.setIsActive(true);
        return cityRepository.save(city);
    }

    
    public Optional<City> partialUpdate(City city) {
        log.debug("Request to partially update City : {}", city);

        return cityRepository
            .findById(city.getId())
            .map(
                existingCity -> {
                    if (city.getName() != null) {
                        existingCity.setName(city.getName());
                    }
                    if (city.getIsActive() != null) {
                        existingCity.setIsActive(city.getIsActive());
                    }
                    return existingCity;
                }
            )
            .map(cityRepository::save);
    }

    @Transactional(readOnly = true)
    public Page<City> findAll(String name, Pageable pageable) {
        log.debug("Request to get all Cities");
        return cityRepository.findAllByIsActiveAndNameContaining(true, name, pageable);
    }

    @Transactional(readOnly = true)
    public List<City> findAll(String name) {
        log.debug("Request to get all Cities");
        return cityRepository.findAllByIsActiveAndNameContaining(true, name);
    }
    
    @Transactional(readOnly = true)
    public Optional<City> findOne(Long id) {
        log.debug("Request to get City : {}", id);
        return cityRepository.findById(id);
    }
    
    public void delete(Long id) {
        log.debug("Request to delete City : {}", id);
        Optional<City> cityOptional = cityRepository.findById(id);
        cityOptional.ifPresentOrElse(city -> {
            city.setIsActive(false);
            cityRepository.save(city);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no region!");
        } );
    }
}
