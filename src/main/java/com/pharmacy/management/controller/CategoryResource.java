package com.pharmacy.management.controller;

import com.pharmacy.management.model.Category;
import com.pharmacy.management.repository.CategoryRepository;
import com.pharmacy.management.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class CategoryResource {
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody @Valid Category category) throws URISyntaxException {
        log.debug("REST request to save Category : {}", category);
        if (category.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new category cannot already have an ID");
        }
        category.setIsActive(true);
        Optional<Category> category1 = categoryRepository.findByNameAndIsActive(category.getName(), true);

        if (category1.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category Name Should be unique");
        }
        Category result = categoryService.save(category);
        return ResponseEntity
                .created(new URI("/api/categories/" + result.getId()))
                .body(result);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody Category category
    ) throws URISyntaxException {
        log.debug("REST request to update Category : {}, {}", id, category);
        if (category.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new category cannot already have an ID");
        }
        if (!Objects.equals(id, category.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new category cannot already have an ID");
        }

        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new category cannot already have an ID");
        }

        Category result = categoryService.save(category);
        return ResponseEntity
                .ok()
                .body(result);
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/categories")
    public Page<Category> getAllCategories(Pageable pageable) {
        log.debug("REST request to get all Categories");
        return categoryService.findAll(pageable);
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all-categories")
    public List<Category> getAllCategories() {
        log.debug("REST request to get all Categories");
        return categoryService.findAll();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        log.debug("REST request to get Category : {}", id);
        Optional<Category> category = categoryService.findOne(id);
        category.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));
        return ResponseEntity.ok(category.get());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.debug("REST request to delete Category : {}", id);
        categoryService.delete(id);
        return ResponseEntity
                .noContent()
                .build();
    }

}
