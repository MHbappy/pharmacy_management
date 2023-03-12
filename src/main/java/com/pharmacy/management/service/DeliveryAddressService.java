package com.pharmacy.management.service;

import com.pharmacy.management.dto.request.DeliveryAddressRequestDTO;
import com.pharmacy.management.model.*;
import com.pharmacy.management.repository.CityRepository;
import com.pharmacy.management.repository.CountryRepository;
import com.pharmacy.management.repository.DeliveryAddressRepository;
import com.pharmacy.management.repository.RegionRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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
@AllArgsConstructor
public class DeliveryAddressService {

    private final Logger log = LoggerFactory.getLogger(DeliveryAddressService.class);
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final ModelMapper modelMapper;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;
    private final UserService userService;

    public DeliveryAddress save(DeliveryAddress deliveryAddress) {
        log.debug("Request to save DeliveryAddress : {}", deliveryAddress);
        return deliveryAddressRepository.save(deliveryAddress);
    }

    public DeliveryAddress saveDeliveryAddress(DeliveryAddressRequestDTO deliveryAddressRequestDTO) {
        Optional<Country> countryOptional = Optional.empty();
        Optional<City> cityOptional = Optional.empty();
        Optional<Region> regionOptional = Optional.empty();

        if (deliveryAddressRequestDTO.getCountryId() != null){
            countryOptional = countryRepository.findById(deliveryAddressRequestDTO.getCountryId());
            if (countryOptional.isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Country not found");
            }
        }

        if (deliveryAddressRequestDTO.getRegionId() != null){
            regionOptional = regionRepository.findById(deliveryAddressRequestDTO.getRegionId());
            if (countryOptional.isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Region not found");
            }
        }

        if (deliveryAddressRequestDTO.getCityId() != null){
            cityOptional = cityRepository.findById(deliveryAddressRequestDTO.getCityId());
            if (countryOptional.isEmpty()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "City not found");
            }
        }

        DeliveryAddress deliveryAddress = modelMapper.map(deliveryAddressRequestDTO, DeliveryAddress.class);
        deliveryAddress.setIsActive(true);
        deliveryAddress.setCountry(countryOptional.get());
        deliveryAddress.setCity(cityOptional.get());
        deliveryAddress.setRegion(regionOptional.get());
        deliveryAddress.setUsers(userService.getCurrentUser());
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
        return deliveryAddressRepository.findAllByIsActive(true);
    }


    @Transactional(readOnly = true)
    public List<DeliveryAddress> findAllByUserId(Long userId) {
        log.debug("Request to get all DeliveryAddresses");
        return deliveryAddressRepository.findAllByIsActiveAndUsers_Id(true, userId);
    }

    @Transactional(readOnly = true)
    public Optional<DeliveryAddress> findOne(Long id) {
        log.debug("Request to get DeliveryAddress : {}", id);
        return deliveryAddressRepository.findById(id);
    }

    public void delete(Long id) {
        log.debug("Request to delete DeliveryAddress : {}", id);
        Optional<DeliveryAddress> deliveryAddressOptional = deliveryAddressRepository.findById(id);
        Users users = userService.getCurrentUser();
        deliveryAddressOptional.ifPresentOrElse(deliveryAddress -> {
            if (!users.getId().equals(deliveryAddress.getUsers().getId())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have no access to delete this!");
            }
            deliveryAddress.setIsActive(false);
            deliveryAddressRepository.save(deliveryAddress);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no category!");
        } );
    }
}
