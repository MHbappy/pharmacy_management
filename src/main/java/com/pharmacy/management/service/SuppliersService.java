package com.pharmacy.management.service;

import com.pharmacy.management.model.Suppliers;
import com.pharmacy.management.repository.SuppliersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class SuppliersService {

    private final Logger log = LoggerFactory.getLogger(SuppliersService.class);

    private final SuppliersRepository suppliersRepository;

    public SuppliersService(SuppliersRepository suppliersRepository) {
        this.suppliersRepository = suppliersRepository;
    }

    
    public Suppliers save(Suppliers suppliers) {
        log.debug("Request to save Suppliers : {}", suppliers);
        return suppliersRepository.save(suppliers);
    }

    
    public Optional<Suppliers> partialUpdate(Suppliers suppliers) {
        log.debug("Request to partially update Suppliers : {}", suppliers);

        return suppliersRepository
            .findById(suppliers.getId())
            .map(
                existingSuppliers -> {
                    if (suppliers.getCompanyName() != null) {
                        existingSuppliers.setCompanyName(suppliers.getCompanyName());
                    }
                    if (suppliers.getContactTitle() != null) {
                        existingSuppliers.setContactTitle(suppliers.getContactTitle());
                    }
                    if (suppliers.getAddress() != null) {
                        existingSuppliers.setAddress(suppliers.getAddress());
                    }
                    if (suppliers.getPostalCode() != null) {
                        existingSuppliers.setPostalCode(suppliers.getPostalCode());
                    }
                    if (suppliers.getPhone() != null) {
                        existingSuppliers.setPhone(suppliers.getPhone());
                    }
                    if (suppliers.getFax() != null) {
                        existingSuppliers.setFax(suppliers.getFax());
                    }
                    if (suppliers.getIsActive() != null) {
                        existingSuppliers.setIsActive(suppliers.getIsActive());
                    }

                    return existingSuppliers;
                }
            )
            .map(suppliersRepository::save);
    }

    
    @Transactional(readOnly = true)
    public List<Suppliers> findAll() {
        log.debug("Request to get all Suppliers");
        return suppliersRepository.findAll();
    }

    
    @Transactional(readOnly = true)
    public Optional<Suppliers> findOne(Long id) {
        log.debug("Request to get Suppliers : {}", id);
        return suppliersRepository.findById(id);
    }

    
    public void delete(Long id) {
        log.debug("Request to delete Suppliers : {}", id);
        suppliersRepository.deleteById(id);
    }
}