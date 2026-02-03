package com.wms.backend.masterdata.warehouse.dto;

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
public class WarehouseDTO implements Serializable {

    Long id;

    @NotBlank(message = "{warehouse.code.required}")
    @Size(max = 50, message = "{warehouse.code.max}")
    String warehouseCode;

    @NotBlank(message = "{warehouse.name.required}")
    @Size(max = 200, message = "{warehouse.name.max}")
    String warehouseName;

    String address;

    @Size(max = 100)
    String city;

    @Size(max = 100)
    String country;

    @Size(max = 20)
    String postalCode;

    @Size(max = 20)
    String phone;

    @Size(max = 100)
    String managerName;

    @Size(max = 30)
    String warehouseType;

    BigDecimal totalCapacity;

    @Size(max = 20)
    String status;

    Instant createdAt;
    Instant updatedAt;
}
