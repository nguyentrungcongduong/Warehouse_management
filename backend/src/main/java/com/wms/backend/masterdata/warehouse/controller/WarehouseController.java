package com.wms.backend.masterdata.warehouse.controller;

import com.turkraft.springfilter.boot.Filter;
import com.wms.backend.masterdata.warehouse.dto.WarehouseDTO;
import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import com.wms.backend.masterdata.warehouse.service.WarehouseService;
import com.wms.backend.shared.util.anotation.ApiMessage;
import com.wms.backend.shared.dto.response.PagedResponse;
import com.wms.backend.shared.exception.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Warehouse", description = "Warehouse Management APIs")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping("/warehouse/warehouses")
    @PreAuthorize("hasAuthority('WAREHOUSE_CREATE')")
    @Operation(summary = "Create a new warehouse")
    @ApiMessage("Create warehouse successfully")
    public ResponseEntity<WarehouseDTO> createWarehouse(@Valid @RequestBody WarehouseDTO warehouseDTO) {
        if (warehouseDTO.getId() != null) {
            throw new BadRequestAlertException("A new warehouse cannot already have an ID", "Warehouse", "idexists");
        }
        WarehouseDTO result = warehouseService.createWarehouse(warehouseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/warehouse/warehouses/{id}")
    @PreAuthorize("hasAuthority('WAREHOUSE_READ')")
    @Operation(summary = "Get warehouse by ID")
    @ApiMessage("Get warehouse success")
    public ResponseEntity<WarehouseDTO> getWarehouse(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "Warehouse", "idinvalid");
        }
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @PutMapping("/warehouse/warehouses/{id}")
    @PreAuthorize("hasAuthority('WAREHOUSE_UPDATE')")
    @Operation(summary = "Update warehouse")
    @ApiMessage("Update warehouse success")
    public ResponseEntity<WarehouseDTO> updateWarehouse(@PathVariable("id") Long id,
            @Valid @RequestBody WarehouseDTO warehouseDTO) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "Warehouse", "idinvalid");
        }
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, warehouseDTO));
    }

    @DeleteMapping("/warehouse/warehouses/{id}")
    @PreAuthorize("hasAuthority('WAREHOUSE_DELETE')")
    @Operation(summary = "Delete warehouse")
    @ApiMessage("Delete warehouse success")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "Warehouse", "idinvalid");
        }
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/warehouse/warehouses")
    @PreAuthorize("hasAuthority('WAREHOUSE_READ')")
    @Operation(summary = "Get all warehouses with filter and pagination")
    @ApiMessage("Get all warehouses success")
    public ResponseEntity<PagedResponse<WarehouseDTO>> getAllWarehouses(
            @Filter Specification<Warehouse> spec,
            Pageable pageable) {
        return ResponseEntity.ok(warehouseService.getAllWarehouses(spec, pageable));
    }
}
