package com.pharmacy.management.service;

import com.pharmacy.management.model.DeliveryAddress;
import com.pharmacy.management.repository.DeliveryAddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeliveryAddressService {

    private final Logger log = LoggerFactory.getLogger(DeliveryAddressService.class);

    private final DeliveryAddressRepository deliveryAddressRepository;

    public DeliveryAddressService(DeliveryAddressRepository deliveryAddressRepository) {
        this.deliveryAddressRepository = deliveryAddressRepository;
    }

    public DeliveryAddress save(DeliveryAddress deliveryAddress) {
        log.debug("Request to save DeliveryAddress : {}", deliveryAddress);
        return deliveryAddressRepository.save(deliveryAddress);
    }
    
    public Optional<DeliveryAddress> partialUpdate(DeliveryAddress deliveryAddress) {
        log.debug("Request to partially update DeliveryAddress : {}", deliveryAddress);

        return deliveryAddressRepository
            .findById(deliveryAddress.getId())
            .map(
                existingDeliveryAddress -> {
                    if (deliveryAddress.getShipName() != null) {
                        existingDeliveryAddress.setShipName(deliveryAddress.getShipName());
                    }
                    if (deliveryAddress.getShipAddress() != null) {
                        existingDeliveryAddress.setShipAddress(deliveryAddress.getShipAddress());
                    }
                    if (deliveryAddress.getShipCity() != null) {
                        existingDeliveryAddress.setShipCity(deliveryAddress.getShipCity());
                    }
                    if (deliveryAddress.getPostalCode() != null) {
                        existingDeliveryAddress.setPostalCode(deliveryAddress.getPostalCode());
                    }

                    return existingDeliveryAddress;
                }
            )
            .map(deliveryAddressRepository::save);
    }

    @Transactional(readOnly = true)
    public List<DeliveryAddress> findAll() {
        log.debug("Request to get all DeliveryAddresses");
        return deliveryAddressRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<DeliveryAddress> findOne(Long id) {
        log.debug("Request to get DeliveryAddress : {}", id);
        return deliveryAddressRepository.findById(id);
    }

    public void delete(Long id) {
        log.debug("Request to delete DeliveryAddress : {}", id);
        deliveryAddressRepository.deleteById(id);
    }
}
