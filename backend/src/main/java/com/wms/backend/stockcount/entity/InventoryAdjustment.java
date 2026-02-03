package com.wms.backend.stockcount.entity;

import com.wms.backend.fefo.entity.Batch;
import com.wms.backend.masterdata.product.entity.Product;
import com.wms.backend.masterdata.warehouse.entity.StorageLocation;
import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import com.wms.backend.stockcount.enums.AdjustmentStatus;
import com.wms.backend.stockcount.enums.AdjustmentType;
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
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_adjustments")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryAdjustment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "adjustment_number", nullable = false, length = 50, unique = true)
    String adjustmentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_count_detail_id")
    StockCountDetail stockCountDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    StorageLocation location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    Batch batch;

    @Enumerated(EnumType.STRING)
    @Column(name = "adjustment_type", length = 30)
    AdjustmentType adjustmentType;

    @Column(name = "quantity_before", nullable = false, precision = 12, scale = 3)
    BigDecimal quantityBefore;

    @Column(name = "adjustment_quantity", nullable = false, precision = 12, scale = 3)
    BigDecimal adjustmentQuantity;

    @Column(name = "quantity_after", nullable = false, precision = 12, scale = 3)
    BigDecimal quantityAfter;

    @Column(name = "reason", length = 200)
    String reason;

    @Column(name = "supporting_documents", columnDefinition = "TEXT")
    String supportingDocuments;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    AdjustmentStatus status = AdjustmentStatus.PENDING;

    @Column(name = "created_by", length = 100)
    String createdBy;

    @Column(name = "approved_by", length = 100)
    String approvedBy;

    @Column(name = "approved_at")
    LocalDateTime approvedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    Instant createdAt;
}
