package com.pharmacy.management.service;

import com.pharmacy.management.model.Provider;
import com.pharmacy.management.repository.ProviderRepository;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ProviderService {

    private final ProviderRepository providerRepository;
    
    public Provider save(Provider provider) {
        return providerRepository.save(provider);
    }
    
    public Optional<Provider> partialUpdate(Provider provider) {

        return providerRepository
                .findById(provider.getId())
                .map(
                        existingProvider -> {
                            if (provider.getName() != null) {
                                existingProvider.setName(provider.getName());
                            }
                            if (provider.getIsActive() != null) {
                                existingProvider.setIsActive(provider.getIsActive());
                            }

                            return existingProvider;
                        }
                )
                .map(providerRepository::save);
    }

    
    @Transactional(readOnly = true)
    public Page<Provider> findAll(Pageable pageable) {
        return providerRepository.findAllByIsActive(true, pageable);
    }


    @Transactional(readOnly = true)
    public List<Provider> allProvider() {
        return providerRepository.findAllByIsActive(true);
    }

    @Transactional(readOnly = true)
    public List<Provider> allProviderByCategoryId(Long categoryId) {
        return providerRepository.findAllProviderListByCategoryId(categoryId);
    }


    @Transactional(readOnly = true)
    public Optional<Provider> findOne(Long id) {
        return providerRepository.findById(id);
    }

    public void delete(Long id) {
        Optional<Provider> providerOptional = providerRepository.findById(id);
        providerOptional.ifPresentOrElse(provider -> {
            provider.setIsActive(false);
            providerRepository.save(provider);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provider not found");
        });
    }
    
}
