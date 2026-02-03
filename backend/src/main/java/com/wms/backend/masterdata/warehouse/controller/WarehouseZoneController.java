package com.wms.backend.masterdata.warehouse.controller;

import com.turkraft.springfilter.boot.Filter;
import com.wms.backend.masterdata.warehouse.dto.WarehouseZoneDTO;
import com.wms.backend.masterdata.warehouse.entity.WarehouseZone;
import com.wms.backend.masterdata.warehouse.service.WarehouseZoneService;
import com.wms.backend.shared.util.anotation.ApiMessage;
import com.wms.backend.shared.dto.response.PagedResponse;
import com.wms.backend.shared.exception.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Warehouse Zone", description = "Warehouse Zone Management APIs")
public class WarehouseZoneController {

    private final WarehouseZoneService zoneService;

    @PostMapping("/warehouse/zones")
    @PreAuthorize("hasAuthority('ZONE_CREATE')")
    @Operation(summary = "Create a new zone")
    @ApiMessage("Create zone successfully")
    @ApiResponse(responseCode = "201", description = "Create zone successfully")
    public ResponseEntity<WarehouseZoneDTO> createZone(@Valid @RequestBody WarehouseZoneDTO zoneDTO) {
        if (zoneDTO.getId() != null) {
            throw new BadRequestAlertException("A new zone cannot already have an ID", "WarehouseZone", "idexists");
        }
        WarehouseZoneDTO result = zoneService.createZone(zoneDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/warehouse/zones/{id}")
    @PreAuthorize("hasAuthority('ZONE_READ')")
    @Operation(summary = "Get zone by ID")
    @ApiMessage("Get zone success")
    @ApiResponse(responseCode = "200", description = "Get zone success")
    public ResponseEntity<WarehouseZoneDTO> getZone(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "WarehouseZone", "idinvalid");
        }
        return ResponseEntity.ok(zoneService.getZoneById(id));
    }

    @PutMapping("/warehouse/zones/{id}")
    @PreAuthorize("hasAuthority('ZONE_UPDATE')")
    @Operation(summary = "Update zone")
    @ApiMessage("Update zone success")
    @ApiResponse(responseCode = "200", description = "Update zone success")
    public ResponseEntity<WarehouseZoneDTO> updateZone(@PathVariable("id") Long id,
            @Valid @RequestBody WarehouseZoneDTO zoneDTO) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "WarehouseZone", "idinvalid");
        }
        return ResponseEntity.ok(zoneService.updateZone(id, zoneDTO));
    }

    @DeleteMapping("/warehouse/zones/{id}")
    @PreAuthorize("hasAuthority('ZONE_DELETE')")
    @Operation(summary = "Delete zone")
    @ApiMessage("Delete zone success")
    @ApiResponse(responseCode = "204", description = "Delete zone success")
    public ResponseEntity<Void> deleteZone(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "WarehouseZone", "idinvalid");
        }
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/warehouse/zones")
    @PreAuthorize("hasAuthority('ZONE_READ')")
    @Operation(summary = "Get all zones with filter and pagination")
    @ApiMessage("Get all zones success")
    @ApiResponse(responseCode = "200", description = "Get all zones success")
    public ResponseEntity<PagedResponse<WarehouseZoneDTO>> getAllZones(
            @Filter Specification<WarehouseZone> spec,
            Pageable pageable) {
        return ResponseEntity.ok(zoneService.getAllZones(spec, pageable));
    }
}
