package com.wms.backend.masterdata.product.entity;

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
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "product_code", nullable = false, length = 50, unique = true)
    String productCode;

    @Column(name = "product_name", nullable = false, length = 200)
    String productName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "unit_of_measure", length = 20)
    String unitOfMeasure;

    @Column(name = "weight", precision = 10, scale = 3)
    BigDecimal weight;

    @Column(name = "length", precision = 10, scale = 2)
    BigDecimal length;

    @Column(name = "width", precision = 10, scale = 2)
    BigDecimal width;

    @Column(name = "height", precision = 10, scale = 2)
    BigDecimal height;

    @Column(name = "volume", precision = 10, scale = 3)
    BigDecimal volume;

    @Column(name = "requires_expiry_tracking")
    Boolean requiresExpiryTracking = false;

    @Column(name = "is_temperature_sensitive")
    Boolean isTemperatureSensitive = false;

    @Column(name = "storage_temperature_min", precision = 5, scale = 2)
    BigDecimal storageTemperatureMin;

    @Column(name = "storage_temperature_max", precision = 5, scale = 2)
    BigDecimal storageTemperatureMax;

    @Column(name = "barcode", length = 100, unique = true)
    String barcode;

    @Column(name = "rfid_tag", length = 100, unique = true)
    String rfidTag;

    @Column(name = "status", length = 20)
    String status = "ACTIVE";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    Instant updatedAt;

    @Column(name = "created_by", length = 100)
    String createdBy;

    @Column(name = "updated_by", length = 100)
    String updatedBy;
}
