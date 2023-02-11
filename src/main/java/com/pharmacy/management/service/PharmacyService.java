package com.pharmacy.management.service;

import com.pharmacy.management.model.Pharmacy;
import com.pharmacy.management.repository.PharmacyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class PharmacyService {

    private final Logger log = LoggerFactory.getLogger(PharmacyService.class);

    private final PharmacyRepository pharmacyRepository;

    public PharmacyService(PharmacyRepository pharmacyRepository) {
        this.pharmacyRepository = pharmacyRepository;
    }

    
    public Pharmacy save(Pharmacy pharmacy) {
        log.debug("Request to save Pharmacy : {}", pharmacy);
        return pharmacyRepository.save(pharmacy);
    }

    
    public Optional<Pharmacy> partialUpdate(Pharmacy pharmacy) {
        log.debug("Request to partially update Pharmacy : {}", pharmacy);

        return pharmacyRepository
            .findById(pharmacy.getId())
            .map(
                existingPharmacy -> {
                    if (pharmacy.getName() != null) {
                        existingPharmacy.setName(pharmacy.getName());
                    }
                    if (pharmacy.getIsActive() != null) {
                        existingPharmacy.setIsActive(pharmacy.getIsActive());
                    }

                    return existingPharmacy;
                }
            )
            .map(pharmacyRepository::save);
    }

    
    @Transactional(readOnly = true)
    public List<Pharmacy> findAll() {
        log.debug("Request to get all Pharmacies");
        return pharmacyRepository.findAll();
    }

    
    @Transactional(readOnly = true)
    public Optional<Pharmacy> findOne(Long id) {
        log.debug("Request to get Pharmacy : {}", id);
        return pharmacyRepository.findById(id);
    }

    
    public void delete(Long id) {
        log.debug("Request to delete Pharmacy : {}", id);
        pharmacyRepository.deleteById(id);
    }
}
