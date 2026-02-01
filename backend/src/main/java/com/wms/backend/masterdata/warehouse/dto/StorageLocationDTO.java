package com.wms.backend.masterdata.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StorageLocationDTO implements Serializable {

    Long id;

    @NotNull(message = "{location.warehouse.required}")
    Long warehouseId;

    String warehouseName;

    @NotNull(message = "{location.zone.required}")
    Long zoneId;

    String zoneName;

    @NotBlank(message = "{location.code.required}")
    @Size(max = 50, message = "{location.code.max}")
    String locationCode;

    @Size(max = 30)
    String locationType;

    @Size(max = 10)
    String aisle;

    @Size(max = 10)
    String rack;

    @Size(max = 10)
    String shelf;

    @Size(max = 10)
    String bin;

    BigDecimal maxCapacity;

    BigDecimal currentCapacity;

    Boolean isAvailable;

    @Size(max = 100)
    String barcode;

    Instant createdAt;
}
