package com.wms.backend.masterdata.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.backend.masterdata.warehouse.dto.StorageLocationDTO;
import com.wms.backend.masterdata.warehouse.entity.StorageLocation;
import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import com.wms.backend.masterdata.warehouse.entity.WarehouseZone;
import com.wms.backend.masterdata.warehouse.repository.StorageLocationRepository;
import com.wms.backend.masterdata.warehouse.repository.WarehouseRepository;
import com.wms.backend.masterdata.warehouse.repository.WarehouseZoneRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@org.springframework.boot.test.context.SpringBootTest
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
@org.springframework.transaction.annotation.Transactional
class StorageLocationControllerIT {

    @Autowired
    private org.springframework.test.web.servlet.MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseZoneRepository zoneRepository;

    @Autowired
    private StorageLocationRepository locationRepository;

    @Autowired
    private EntityManager entityManager;

    private Warehouse warehouse;
    private WarehouseZone zone;
    private StorageLocation location;

    @BeforeEach
    void setUp() {
        locationRepository.deleteAll();
        zoneRepository.deleteAll();
        warehouseRepository.deleteAll();

        warehouse = new Warehouse();
        warehouse.setWarehouseCode("WH_LOC_IT");
        warehouse.setWarehouseName("IT Warehouse Loc");
        warehouse = warehouseRepository.save(warehouse);

        zone = new WarehouseZone();
        zone.setZoneCode("Z_LOC_IT");
        zone.setZoneName("IT Zone Loc");
        zone.setWarehouse(warehouse);
        zone = zoneRepository.save(zone);

        location = new StorageLocation();
        location.setLocationCode("LOC001");
        location.setWarehouse(warehouse);
        location.setZone(zone);
        location.setMaxCapacity(new BigDecimal("100"));
        location.setCurrentCapacity(BigDecimal.ZERO);
        location = locationRepository.save(location);
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = { "LOCATION_CREATE", "ROLE_ADMIN" })
    void createLocation_Success() throws Exception {
        StorageLocationDTO dto = new StorageLocationDTO();
        dto.setWarehouseId(warehouse.getId());
        dto.setZoneId(zone.getId());
        dto.setLocationCode("LOC002");
        dto.setMaxCapacity(new BigDecimal("500"));

        mockMvc.perform(post("/api/v1/warehouse/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.locationCode").value("LOC002"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = { "LOCATION_CREATE", "ROLE_ADMIN" })
    void createLocation_Conflict() throws Exception {
        StorageLocationDTO dto = new StorageLocationDTO();
        dto.setWarehouseId(warehouse.getId());
        dto.setZoneId(zone.getId());
        dto.setLocationCode("LOC001"); // Exists

        mockMvc.perform(post("/api/v1/warehouse/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = { "LOCATION_READ", "ROLE_ADMIN" })
    void getLocation_AsAdmin_Success() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/locations/{id}", location.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.locationCode").value("LOC001"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = { "LOCATION_DELETE", "ROLE_ADMIN" })
    void deleteLocation_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/warehouse/locations/{id}", location.getId()))
                .andExpect(status().isNoContent());

        locationRepository.flush();
        entityManager.clear();
        assertFalse(locationRepository.existsById(location.getId()));
    }
}
