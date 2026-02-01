package com.wms.backend.masterdata.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDTO implements Serializable {

    Long id;

    @NotBlank(message = "{category.name.required}")
    @Size(max = 100, message = "{category.name.max}")
    String categoryName;

    String description;

    Long parentId;

    String parentName;

    Instant createdAt;
    Instant updatedAt;
}
