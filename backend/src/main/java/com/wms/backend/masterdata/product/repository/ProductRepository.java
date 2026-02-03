package com.wms.backend.masterdata.product.repository;

import com.wms.backend.masterdata.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    boolean existsByProductCode(String productCode);

    boolean existsByBarcode(String barcode);

    boolean existsByRfidTag(String rfidTag);
}
