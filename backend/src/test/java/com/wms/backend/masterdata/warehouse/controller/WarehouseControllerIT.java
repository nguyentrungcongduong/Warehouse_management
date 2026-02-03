package com.wms.backend.masterdata.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.backend.auth.entity.User;
import com.wms.backend.auth.repository.UserRepository;
import com.wms.backend.masterdata.warehouse.dto.WarehouseDTO;
import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import com.wms.backend.masterdata.warehouse.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class WarehouseControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private UserRepository userRepository;

    private Warehouse warehouse;
    private User adminUser;
    private User managerUser;

    @BeforeEach
    void setUp() {
        // Clear DB
        userRepository.deleteAll();
        warehouseRepository.deleteAll();

        // Create Warehouse
        warehouse = new Warehouse();
        warehouse.setWarehouseCode("WH001");
        warehouse.setWarehouseName("Test Warehouse");
        warehouse.setAddress("123 Test St");
        warehouse = warehouseRepository.save(warehouse);

        // Create Admin User
        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@test.com");
        adminUser.setActive(true);
        adminUser.setPassword("password");
        adminUser.setCreatedAt(java.time.Instant.now()); // Manual set
        adminUser.setUpdatedAt(java.time.Instant.now());
        adminUser.setFullName("Admin User");
        userRepository.save(adminUser);

        // Create Manager User
        managerUser = new User();
        managerUser.setUsername("manager");
        managerUser.setEmail("manager@test.com");
        managerUser.setActive(true);
        managerUser.setWarehouseId(warehouse.getId());
        managerUser.setCreatedAt(java.time.Instant.now());
        managerUser.setUpdatedAt(java.time.Instant.now());
        managerUser.setFullName("Manager User");
        managerUser.setPassword("password");
        userRepository.save(managerUser);
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = { "WAREHOUSE_CREATE", "ROLE_ADMIN" })
    void createWarehouse_AsAdmin_Success() throws Exception {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setWarehouseCode("WH002");
        dto.setWarehouseName("New Warehouse");
        dto.setAddress("456 New St");

        mockMvc.perform(post("/api/v1/warehouse/warehouses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.warehouseCode").value("WH002"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = { "WAREHOUSE_READ", "ROLE_ADMIN" })
    void getWarehouse_AsAdmin_Success() throws Exception {
        mockMvc.perform(get("/api/v1/warehouse/warehouses/{id}", warehouse.getId()))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.warehouseCode").value("WH001"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "WAREHOUSE_READ")
    void getWarehouse_AsManager_AssignedScope_Success() throws Exception {
        // Manager assigned to WH001
        mockMvc.perform(get("/api/v1/warehouse/warehouses/{id}", warehouse.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "WAREHOUSE_READ")
    void getWarehouse_AsManager_OutsideScope_NotFound() throws Exception {
        // Create another warehouse
        Warehouse other = new Warehouse();
        other.setWarehouseCode("WH003");
        other.setWarehouseName("Other");
        warehouseRepository.save(other);

        // Manager is assigned to WH001, so shouldn't see WH003
        mockMvc.perform(get("/api/v1/warehouse/warehouses/{id}", other.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = { "WAREHOUSE_UPDATE", "ROLE_ADMIN" })
    void updateWarehouse_AsAdmin_Success() throws Exception {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setWarehouseCode("WH001-UPDATED");
        dto.setWarehouseName("Updated Name");
        dto.setAddress("Updated Address");
        // We need to set ID or not depending on Mapper? Usually ID in path is the key.

        mockMvc.perform(put("/api/v1/warehouse/warehouses/{id}", warehouse.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.warehouseCode").value("WH001-UPDATED"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = { "WAREHOUSE_DELETE", "ROLE_ADMIN" })
    void deleteWarehouse_AsAdmin_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/warehouse/warehouses/{id}", warehouse.getId()))
                .andExpect(status().isNoContent());
    }
}
