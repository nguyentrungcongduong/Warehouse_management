package com.wms.backend.masterdata.warehouse.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "warehouses")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Warehouse implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "warehouse_code", nullable = false, length = 50, unique = true)
    String warehouseCode;

    @Column(name = "warehouse_name", nullable = false, length = 200)
    String warehouseName;

    @Column(name = "address", columnDefinition = "TEXT")
    String address;

    @Column(name = "city", length = 100)
    String city;

    @Column(name = "country", length = 100)
    String country;

    @Column(name = "postal_code", length = 20)
    String postalCode;

    @Column(name = "phone", length = 20)
    String phone;

    @Column(name = "manager_name", length = 100)
    String managerName;

    @Column(name = "warehouse_type", length = 30)
    String warehouseType;

    @Column(name = "total_capacity", precision = 12, scale = 2)
    BigDecimal totalCapacity;

    @Column(name = "status", length = 20)
    String status = "ACTIVE";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    Instant updatedAt;
}
