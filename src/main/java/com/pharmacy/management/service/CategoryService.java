package com.pharmacy.management.service;

import com.pharmacy.management.model.Category;
import com.pharmacy.management.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                    if (category.getDecription() != null) {
                        existingCategory.setDecription(category.getDecription());
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
    public List<Category> findAll() {
        log.debug("Request to get all Categories");
        return categoryRepository.findAll();
    }

    
    @Transactional(readOnly = true)
    public Optional<Category> findOne(Long id) {
        log.debug("Request to get Category : {}", id);
        return categoryRepository.findById(id);
    }

    
    public void delete(Long id) {
        log.debug("Request to delete Category : {}", id);
        categoryRepository.deleteById(id);
    }
}
