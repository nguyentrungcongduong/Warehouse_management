package com.wms.backend.masterdata.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDTO implements Serializable {

    Long id;

    @NotBlank(message = "{product.code.required}")
    @Size(max = 50, message = "{product.code.max}")
    String productCode;

    @NotBlank(message = "{product.name.required}")
    @Size(max = 200, message = "{product.name.max}")
    String productName;

    Long categoryId;

    String categoryName;

    String description;

    @Size(max = 20)
    String unitOfMeasure;

    BigDecimal weight;

    BigDecimal length;

    BigDecimal width;

    BigDecimal height;

    BigDecimal volume;

    Boolean requiresExpiryTracking;

    Boolean isTemperatureSensitive;

    BigDecimal storageTemperatureMin;

    BigDecimal storageTemperatureMax;

    @Size(max = 100)
    String barcode;

    @Size(max = 100)
    String rfidTag;

    @Size(max = 20)
    String status;

    Instant createdAt;
    Instant updatedAt;

    String createdBy;
    String updatedBy;
}
