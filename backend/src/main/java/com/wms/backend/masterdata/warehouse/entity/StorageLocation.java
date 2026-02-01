package com.wms.backend.masterdata.warehouse.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "storage_locations", uniqueConstraints = {
        @UniqueConstraint(name = "uk_location", columnNames = { "warehouse_id", "location_code" })
})
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StorageLocation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    WarehouseZone zone;

    @Column(name = "location_code", nullable = false, length = 50)
    String locationCode;

    @Column(name = "location_type", length = 30)
    String locationType;

    @Column(name = "aisle", length = 10)
    String aisle;

    @Column(name = "rack", length = 10)
    String rack;

    @Column(name = "shelf", length = 10)
    String shelf;

    @Column(name = "bin", length = 10)
    String bin;

    @Column(name = "max_capacity", precision = 10, scale = 2)
    BigDecimal maxCapacity;

    @Column(name = "current_capacity", precision = 10, scale = 2)
    BigDecimal currentCapacity = BigDecimal.ZERO;

    @Column(name = "is_available")
    Boolean isAvailable = true;

    @Column(name = "barcode", length = 100, unique = true)
    String barcode;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    Instant createdAt;
}
