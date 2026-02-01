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
@Table(name = "warehouse_zones", uniqueConstraints = {
        @UniqueConstraint(name = "uk_zone", columnNames = { "warehouse_id", "zone_code" })
})
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WarehouseZone implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    Warehouse warehouse;

    @Column(name = "zone_code", nullable = false, length = 50)
    String zoneCode;

    @Column(name = "zone_name", length = 100)
    String zoneName;

    @Column(name = "zone_type", length = 30)
    String zoneType;

    @Column(name = "temperature_controlled")
    Boolean temperatureControlled = false;

    @Column(name = "temperature_min", precision = 5, scale = 2)
    BigDecimal temperatureMin;

    @Column(name = "temperature_max", precision = 5, scale = 2)
    BigDecimal temperatureMax;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    Instant createdAt;
}
