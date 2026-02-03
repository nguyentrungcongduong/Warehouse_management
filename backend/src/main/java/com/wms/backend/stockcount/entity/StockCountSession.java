package com.wms.backend.stockcount.entity;

import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import com.wms.backend.masterdata.warehouse.entity.WarehouseZone;
import com.wms.backend.stockcount.enums.StockCountStatus;
import com.wms.backend.stockcount.enums.StockCountType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_count_sessions")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockCountSession implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "session_number", nullable = false, length = 50, unique = true)
    String sessionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    WarehouseZone zone;

    @Enumerated(EnumType.STRING)
    @Column(name = "count_type", length = 30)
    StockCountType countType;

    @Column(name = "scheduled_date")
    LocalDate scheduledDate;

    @Column(name = "start_time")
    LocalDateTime startTime;

    @Column(name = "end_time")
    LocalDateTime endTime;

    @Column(name = "assigned_to", length = 100)
    String assignedTo;

    @Column(name = "approved_by", length = 100)
    String approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    StockCountStatus status = StockCountStatus.PLANNED;

    @Column(name = "total_locations")
    Integer totalLocations;

    @Column(name = "counted_locations")
    @ColumnDefault("0")
    Integer countedLocations = 0;

    @Column(name = "total_items")
    Integer totalItems;

    @Column(name = "counted_items")
    @ColumnDefault("0")
    Integer countedItems = 0;

    @Column(name = "discrepancy_count")
    @ColumnDefault("0")
    Integer discrepancyCount = 0;

    @Column(name = "accuracy_rate", precision = 5, scale = 2)
    BigDecimal accuracyRate;

    @Column(name = "notes", columnDefinition = "TEXT")
    String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    Instant updatedAt;
}
