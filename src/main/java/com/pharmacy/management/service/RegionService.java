package com.pharmacy.management.service;

import com.pharmacy.management.model.Company;
import com.pharmacy.management.model.Region;
import com.pharmacy.management.repository.RegionRepository;
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
public class RegionService {

    private final Logger log = LoggerFactory.getLogger(RegionService.class);

    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    
    public Region save(Region region) {
        log.debug("Request to save Region : {}", region);
        region.setIsActive(true);
        return regionRepository.save(region);
    }

    
    public Optional<Region> partialUpdate(Region region) {
        log.debug("Request to partially update Region : {}", region);

        return regionRepository
            .findById(region.getId())
            .map(
                existingRegion -> {
                    if (region.getName() != null) {
                        existingRegion.setName(region.getName());
                    }
                    if (region.getIsActive() != null) {
                        existingRegion.setIsActive(region.getIsActive());
                    }

                    return existingRegion;
                }
            )
            .map(regionRepository::save);
    }

    
    @Transactional(readOnly = true)
    public Page<Region> findAll(String name, Pageable pageable) {
        log.debug("Request to get all Regions");
        return regionRepository.findAllByIsActiveAndNameContaining(true, name, pageable);
    }


    @Transactional(readOnly = true)
    public List<Region> findAllByCityId(Long cityId) {
        log.debug("Request to get all Regions");
        return regionRepository.findAllByCity_IdAndCity_IsActiveAndIsActive(cityId, true, true);
    }

    
    @Transactional(readOnly = true)
    public Optional<Region> findOne(Long id) {
        log.debug("Request to get Region : {}", id);
        return regionRepository.findById(id);
    }

    public List<Region> findByCityIdAndCityIsActiveAndRegionIsActive(Long cityId, Boolean cityIsActive, Boolean regionIsActive){
        return regionRepository.findAllByCity_IdAndCity_IsActiveAndIsActive(cityId, cityIsActive, regionIsActive);
    }

    
    public void delete(Long id) {
        log.debug("Request to delete Region : {}", id);
        Optional<Region> regionOptional = regionRepository.findById(id);
        regionOptional.ifPresentOrElse(region -> {
            region.setIsActive(false);
            regionRepository.save(region);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no region!");
        } );
    }
}
