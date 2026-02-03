package com.wms.backend.stockcount.entity;

import com.wms.backend.fefo.entity.Batch;
import com.wms.backend.masterdata.product.entity.Product;
import com.wms.backend.masterdata.warehouse.entity.StorageLocation;
import com.wms.backend.stockcount.enums.CountMethod;
import com.wms.backend.stockcount.enums.StockCountDetailStatus;
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
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_count_details")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockCountDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    StockCountSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    StorageLocation location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    Batch batch;

    @Column(name = "system_quantity", nullable = false, precision = 12, scale = 3)
    BigDecimal systemQuantity;

    @Column(name = "counted_quantity", precision = 12, scale = 3)
    BigDecimal countedQuantity;

    @Column(name = "variance_quantity", insertable = false, updatable = false)
    BigDecimal varianceQuantity;

    @Column(name = "variance_percentage", precision = 5, scale = 2)
    BigDecimal variancePercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "count_method", length = 20)
    CountMethod countMethod;

    @Column(name = "scanned_at")
    LocalDateTime scannedAt;

    @Column(name = "scanned_by", length = 100)
    String scannedBy;

    @Column(name = "device_id", length = 100)
    String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    StockCountDetailStatus status = StockCountDetailStatus.PENDING;

    @Column(name = "is_discrepancy", insertable = false, updatable = false)
    Boolean isDiscrepancy;

    @Column(name = "discrepancy_reason", length = 200)
    String discrepancyReason;

    @Column(name = "adjustment_approved")
    @ColumnDefault("false")
    Boolean adjustmentApproved = false;

    @Column(name = "adjustment_approved_by", length = 100)
    String adjustmentApprovedBy;

    @Column(name = "adjustment_approved_at")
    LocalDateTime adjustmentApprovedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    Instant updatedAt;
}
