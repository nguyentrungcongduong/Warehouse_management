package com.wms.backend.masterdata.product.service.impl;

import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.masterdata.product.dto.CategoryDTO;
import com.wms.backend.masterdata.product.entity.Category;
import com.wms.backend.masterdata.product.mapper.CategoryMapper;
import com.wms.backend.masterdata.product.repository.CategoryRepository;
import com.wms.backend.masterdata.product.service.CategoryService;
import com.wms.backend.shared.dto.response.PagedResponse;
import com.wms.backend.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final AuditLogService auditLogService;

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        log.info("Creating category: {}", categoryDTO.getCategoryName());

        if (categoryDTO.getParentId() != null && !categoryRepository.existsById(categoryDTO.getParentId())) {
            throw new EntityNotFoundException("Parent Category not found", "Category", "notfound");
        }

        Category category = categoryMapper.toEntity(categoryDTO);
        category = categoryRepository.save(category);
        auditLogService.log("CREATE", "CATEGORY", category.getId(), null, categoryDTO);
        return categoryMapper.toDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        log.info("Getting category by id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found", "Category", "notfound"));
        return categoryMapper.toDTO(category);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        log.info("Updating category id: {}", id);
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found", "Category", "notfound"));

        if (categoryDTO.getParentId() != null && !categoryRepository.existsById(categoryDTO.getParentId())) {
            throw new EntityNotFoundException("Parent Category not found", "Category", "notfound");
        }

        // Prevent circular dependency (simplified check: parent != self)
        if (categoryDTO.getParentId() != null && categoryDTO.getParentId().equals(id)) {
            throw new IllegalArgumentException("Category cannot be its own parent");
        }

        CategoryDTO oldData = categoryMapper.toDTO(existingCategory);
        categoryMapper.partialUpdate(existingCategory, categoryDTO);
        Category updatedCategory = categoryRepository.save(existingCategory);

        auditLogService.log("UPDATE", "CATEGORY", id, oldData, categoryDTO);
        return categoryMapper.toDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        log.info("Deleting category id: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found", "Category", "notfound");
        }
        categoryRepository.deleteById(id);
        auditLogService.log("DELETE", "CATEGORY", id, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CategoryDTO> getAllCategories(Specification<Category> spec, Pageable pageable) {
        log.info("Fetching all categories with filter");
        Page<Category> page = categoryRepository.findAll(spec, pageable);
        return new PagedResponse<>(page.map(categoryMapper::toDTO));
    }
}
