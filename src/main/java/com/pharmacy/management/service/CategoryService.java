package com.pharmacy.management.service;

import com.pharmacy.management.model.Category;
import com.pharmacy.management.repository.CategoryRepository;
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
public class CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    
    public Category save(Category category) {
        log.debug("Request to save Category : {}", category);
        category.setIsActive(true);
        return categoryRepository.save(category);
    }

    
    public Optional<Category> partialUpdate(Category category) {
        log.debug("Request to partially update Category : {}", category);

        return categoryRepository
            .findById(category.getId())
            .map(
                existingCategory -> {
                    if (category.getName() != null) {
                        existingCategory.setName(category.getName());
                    }
                    if (category.getDescription() != null) {
                        existingCategory.setDescription(category.getDescription());
                    }
                    if (category.getPhoto() != null) {
                        existingCategory.setPhoto(category.getPhoto());
                    }
                    if (category.getIsActive() != null) {
                        existingCategory.setIsActive(category.getIsActive());
                    }

                    return existingCategory;
                }
            )
            .map(categoryRepository::save);
    }

    
    @Transactional(readOnly = true)
    public Page<Category> findAll(Pageable pageable) {
        log.debug("Request to get all Categories");
        return categoryRepository.findAllByIsActive(true, pageable);
    }

    
    @Transactional(readOnly = true)
    public Optional<Category> findOne(Long id) {
        log.debug("Request to get Category : {}", id);
        return categoryRepository.findById(id);
    }

    
    public void delete(Long id) {
        log.debug("Request to delete Category : {}", id);
        Optional<Category> categoryOptional = categoryRepository.findById(id);

        categoryOptional.ifPresentOrElse(category -> {
            category.setIsActive(false);
            categoryRepository.save(category);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no category!");
        } );

    }
}
