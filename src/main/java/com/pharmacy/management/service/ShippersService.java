package com.pharmacy.management.service;

import com.pharmacy.management.model.Shippers;
import com.pharmacy.management.repository.ShippersRepository;
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
public class ShippersService {

    private final Logger log = LoggerFactory.getLogger(ShippersService.class);

    private final ShippersRepository shippersRepository;

    public ShippersService(ShippersRepository shippersRepository) {
        this.shippersRepository = shippersRepository;
    }
    
    public Shippers save(Shippers shippers) {
        log.debug("Request to save Shippers : {}", shippers);
        return shippersRepository.save(shippers);
    }

    public Optional<Shippers> partialUpdate(Shippers shippers) {
        log.debug("Request to partially update Shippers : {}", shippers);

        return shippersRepository
            .findById(shippers.getId())
            .map(
                existingShippers -> {
                    if (shippers.getCompanyName() != null) {
                        existingShippers.setCompanyName(shippers.getCompanyName());
                    }
                    if (shippers.getPhone() != null) {
                        existingShippers.setPhone(shippers.getPhone());
                    }

                    return existingShippers;
                }
            )
            .map(shippersRepository::save);
    }

    @Transactional(readOnly = true)
    public List<Shippers> findAll() {
        log.debug("Request to get all Shippers");
        return shippersRepository.findAllByIsActive(true);
    }

    @Transactional(readOnly = true)
    public Optional<Shippers> findOne(Long id) {
        log.debug("Request to get Shippers : {}", id);
        return shippersRepository.findById(id);
    }
    
    public void delete(Long id) {
        log.debug("Request to delete Shippers : {}", id);

        log.debug("Request to delete Orders : {}", id);
        Optional<Shippers> shippersOptional = shippersRepository.findById(id);
        shippersOptional.ifPresentOrElse(shippers -> {
            shippers.setIsActive(false);
            shippersRepository.save(shippers);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no region!");
        } );

    }
}
