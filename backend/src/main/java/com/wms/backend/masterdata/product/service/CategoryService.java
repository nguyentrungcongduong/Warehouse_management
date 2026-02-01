package com.wms.backend.masterdata.product.service;

import com.wms.backend.masterdata.product.dto.CategoryDTO;
import com.wms.backend.masterdata.product.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO getCategoryById(Long id);

    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);

    void deleteCategory(Long id);

    Page<CategoryDTO> getAllCategories(Specification<Category> spec, Pageable pageable);
}
