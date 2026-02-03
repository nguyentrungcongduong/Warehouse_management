package com.wms.backend.masterdata.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.backend.auth.entity.User;
import com.wms.backend.auth.repository.UserRepository;
import com.wms.backend.masterdata.warehouse.dto.WarehouseZoneDTO;
import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import com.wms.backend.masterdata.warehouse.entity.WarehouseZone;
import com.wms.backend.masterdata.warehouse.repository.StorageLocationRepository;
import com.wms.backend.masterdata.warehouse.repository.WarehouseRepository;
import com.wms.backend.masterdata.warehouse.repository.WarehouseZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WarehouseZoneControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseZoneRepository zoneRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StorageLocationRepository storageLocationRepository;

    @Autowired
    private EntityManager entityManager;

    private Warehouse warehouse;
    private WarehouseZone zone;
    private User adminUser;
    private User managerUser;

    @BeforeEach
    void setUp() {
        storageLocationRepository.deleteAll();
        zoneRepository.deleteAll();
        warehouseRepository.deleteAll();
        userRepository.deleteAll();

        // Create Warehouse
        warehouse = new Warehouse();
        warehouse.setWarehouseCode("WH_Z_TEST");
        warehouse.setWarehouseName("Test Warehouse Zone");
        warehouse = warehouseRepository.save(warehouse);

        // Create Zone
        zone = new WarehouseZone();
        zone.setZoneCode("Z01");
        zone.setZoneName("Zone 1");
        zone.setWarehouse(warehouse);
        zone.setZoneType("STORAGE");
        zone = zoneRepository.save(zone);

        // Create Users
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword("password");
        adminUser.setFullName("Admin User");
        adminUser.setCreatedAt(java.time.Instant.now());
        adminUser.setUpdatedAt(java.time.Instant.now());
        adminUser.setActive(true);
        userRepository.save(adminUser);

        managerUser = new User();
        managerUser.setUsername("manager");
        managerUser.setEmail("manager@test.com");
        managerUser.setPassword("password");
        managerUser.setFullName("Manager User");
        managerUser.setCreatedAt(java.time.Instant.now());
        managerUser.setUpdatedAt(java.time.Instant.now());
        managerUser.setActive(true);
        managerUser.setWarehouseId(warehouse.getId());
        userRepository.save(managerUser);
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = { "ZONE_CREATE", "ROLE_ADMIN" })
    void createZone_AsAdmin_Success() throws Exception {
        WarehouseZoneDTO dto = new WarehouseZoneDTO();
        dto.setWarehouseId(warehouse.getId());
        dto.setZoneCode("Z02");
        dto.setZoneName("Zone 2");
        dto.setZoneType("PICKING");

        mockMvc.perform(post("/api/v1/warehouse/zones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.zoneCode").value("Z02"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = { "ZONE_CREATE", "ROLE_ADMIN" })
    void createZone_Conflict() throws Exception {
        WarehouseZoneDTO dto = new WarehouseZoneDTO();
        dto.setWarehouseId(warehouse.getId());
        dto.setZoneCode("Z01"); // Exists
        dto.setZoneName("Zone 1 Duplicate");

        mockMvc.perform(post("/api/v1/warehouse/zones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = { "ZONE_READ", "ROLE_ADMIN" })
    void getZone_AsAdmin_Success() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/zones/{id}", zone.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.zoneCode").value("Z01"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ZONE_READ")
    void getZone_AsManager_Success() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/zones/{id}", zone.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.zoneCode").value("Z01"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ZONE_READ")
    void getZone_AsManager_OtherWarehouse_NotFound() throws Exception {
        // Create another warehouse and zone
        Warehouse otherWh = new Warehouse();
        otherWh.setWarehouseCode("WH_OTHER");
        otherWh.setWarehouseName("Other");
        otherWh = warehouseRepository.save(otherWh);

        WarehouseZone otherZone = new WarehouseZone();
        otherZone.setZoneCode("Z_OTHER");
        otherZone.setWarehouse(otherWh);
        otherZone = zoneRepository.save(otherZone);

        mockMvc.perform(get("/api/v1/warehouse/zones/{id}", otherZone.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = { "ZONE_UPDATE", "ROLE_ADMIN" })
    void updateZone_Success() throws Exception {
        WarehouseZoneDTO dto = new WarehouseZoneDTO();
        dto.setWarehouseId(warehouse.getId());
        dto.setZoneCode("Z01-UPD");
        dto.setZoneName("Zone 1 Updated");

        mockMvc.perform(put("/api/v1/warehouse/zones/{id}", zone.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.zoneCode").value("Z01-UPD"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = { "ZONE_DELETE", "ROLE_ADMIN" })
    void deleteZone_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/warehouse/zones/{id}", zone.getId()))
                .andExpect(status().isNoContent());

        zoneRepository.flush();
        entityManager.clear();
        assertFalse(zoneRepository.existsById(zone.getId()));
    }
}
