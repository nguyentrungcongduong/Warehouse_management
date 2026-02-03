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
public class WarehouseZoneDTO implements Serializable {

    Long id;

    @NotNull(message = "{zone.warehouse.required}")
    Long warehouseId;

    String warehouseName;

    @NotBlank(message = "{zone.code.required}")
    @Size(max = 50, message = "{zone.code.max}")
    String zoneCode;

    @Size(max = 100)
    String zoneName;

    @Size(max = 30)
    String zoneType;

    Boolean temperatureControlled;

    BigDecimal temperatureMin;

    BigDecimal temperatureMax;

    Instant createdAt;
}
