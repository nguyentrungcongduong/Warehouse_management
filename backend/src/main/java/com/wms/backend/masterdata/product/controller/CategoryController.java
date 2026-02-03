package com.wms.backend.masterdata.product.controller;

import com.turkraft.springfilter.boot.Filter;
import com.wms.backend.masterdata.product.dto.CategoryDTO;
import com.wms.backend.masterdata.product.entity.Category;
import com.wms.backend.masterdata.product.service.CategoryService;
import com.wms.backend.shared.util.anotation.ApiMessage;
import com.wms.backend.shared.dto.response.PagedResponse;
import com.wms.backend.shared.exception.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category Management APIs")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/warehouse/categories")
    @PreAuthorize("hasAuthority('CATEGORY_CREATE')")
    @Operation(summary = "Create a new category")
    @ApiMessage("Create category successfully")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        if (categoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new category cannot already have an ID", "Category", "idexists");
        }
        CategoryDTO result = categoryService.createCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/warehouse/categories/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_READ')")
    @Operation(summary = "Get category by ID")
    @ApiMessage("Get category success")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "Category", "idinvalid");
        }
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PutMapping("/warehouse/categories/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_UPDATE')")
    @Operation(summary = "Update category")
    @ApiMessage("Update category success")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable("id") Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "Category", "idinvalid");
        }
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
    }

    @DeleteMapping("/warehouse/categories/{id}")
    @PreAuthorize("hasAuthority('CATEGORY_DELETE')")
    @Operation(summary = "Delete category")
    @ApiMessage("Delete category success")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "Category", "idinvalid");
        }
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/warehouse/categories")
    @PreAuthorize("hasAuthority('CATEGORY_READ')")
    @Operation(summary = "Get all categories with filter and pagination")
    @ApiMessage("Get all categories success")
    public ResponseEntity<PagedResponse<CategoryDTO>> getAllCategories(
            @Filter Specification<Category> spec,
            Pageable pageable) {
        return ResponseEntity.ok(categoryService.getAllCategories(spec, pageable));
    }
}
