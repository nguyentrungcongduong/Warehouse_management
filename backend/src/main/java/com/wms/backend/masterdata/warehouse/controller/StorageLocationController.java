package com.wms.backend.masterdata.warehouse.controller;

import com.turkraft.springfilter.boot.Filter;
import com.wms.backend.masterdata.warehouse.dto.StorageLocationDTO;
import com.wms.backend.masterdata.warehouse.entity.StorageLocation;
import com.wms.backend.masterdata.warehouse.service.StorageLocationService;
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
@Tag(name = "Storage Location", description = "Storage Location Management APIs")
public class StorageLocationController {

    private final StorageLocationService locationService;

    @PostMapping("/warehouse/locations")
    @PreAuthorize("hasAuthority('LOCATION_CREATE')")
    @Operation(summary = "Create a new location")
    @ApiMessage("Create location successfully")
    public ResponseEntity<StorageLocationDTO> createLocation(@Valid @RequestBody StorageLocationDTO locationDTO) {
        if (locationDTO.getId() != null) {
            throw new BadRequestAlertException("A new location cannot already have an ID", "StorageLocation",
                    "idexists");
        }
        StorageLocationDTO result = locationService.createLocation(locationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/warehouse/locations/{id}")
    @PreAuthorize("hasAuthority('LOCATION_READ')")
    @Operation(summary = "Get location by ID")
    @ApiMessage("Get location success")
    public ResponseEntity<StorageLocationDTO> getLocation(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "StorageLocation", "idinvalid");
        }
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @PutMapping("/warehouse/locations/{id}")
    @PreAuthorize("hasAuthority('LOCATION_UPDATE')")
    @Operation(summary = "Update location")
    @ApiMessage("Update location success")
    public ResponseEntity<StorageLocationDTO> updateLocation(@PathVariable("id") Long id,
            @Valid @RequestBody StorageLocationDTO locationDTO) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "StorageLocation", "idinvalid");
        }
        return ResponseEntity.ok(locationService.updateLocation(id, locationDTO));
    }

    @DeleteMapping("/warehouse/locations/{id}")
    @PreAuthorize("hasAuthority('LOCATION_DELETE')")
    @Operation(summary = "Delete location")
    @ApiMessage("Delete location success")
    public ResponseEntity<Void> deleteLocation(@PathVariable("id") Long id) {
        if (id <= 0) {
            throw new BadRequestAlertException("Invalid ID", "StorageLocation", "idinvalid");
        }
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/warehouse/locations")
    @PreAuthorize("hasAuthority('LOCATION_READ')")
    @Operation(summary = "Get all locations with filter and pagination")
    @ApiMessage("Get all locations success")
    public ResponseEntity<PagedResponse<StorageLocationDTO>> getAllLocations(
            @Filter Specification<StorageLocation> spec,
            Pageable pageable) {
        return ResponseEntity.ok(locationService.getAllLocations(spec, pageable));
    }
}
