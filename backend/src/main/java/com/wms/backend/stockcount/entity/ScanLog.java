package com.wms.backend.stockcount.entity;

import com.wms.backend.fefo.entity.Batch;
import com.wms.backend.masterdata.product.entity.Product;
import com.wms.backend.masterdata.warehouse.entity.StorageLocation;
import com.wms.backend.stockcount.enums.ScanType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "scan_logs")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScanLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    StockCountSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "scan_type", length = 20)
    ScanType scanType;

    @Column(name = "scanned_code", nullable = false, length = 100)
    String scannedCode;

    @Column(name = "scan_time")
    LocalDateTime scanTime;

    @Column(name = "scanned_by", length = 100)
    String scannedBy;

    @Column(name = "device_id", length = 100)
    String deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    StorageLocation location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    Batch batch;

    @Column(name = "quantity", precision = 12, scale = 3)
    BigDecimal quantity;

    @Column(name = "scan_purpose", length = 30)
    String scanPurpose;

    @Column(name = "status", length = 20)
    String status = "SUCCESS";

    @Column(name = "error_message", columnDefinition = "TEXT")
    String errorMessage;
}
