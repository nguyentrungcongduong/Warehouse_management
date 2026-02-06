package com.wms.backend.masterdata.warehouse.service.impl;

import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.auth.entity.User;
import com.wms.backend.auth.repository.UserRepository;
import com.wms.backend.masterdata.warehouse.dto.WarehouseDTO;
import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import com.wms.backend.masterdata.warehouse.mapper.WarehouseMapper;
import com.wms.backend.masterdata.warehouse.repository.WarehouseRepository;
import com.wms.backend.shared.exception.ConflictException;
import com.wms.backend.shared.exception.EntityNotFoundException;
import com.wms.backend.shared.util.SecurityUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceImplTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private WarehouseMapper warehouseMapper;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WarehouseServiceImpl warehouseService;

    private MockedStatic<SecurityUtil> securityUtilMockedStatic;

    @BeforeEach
    void setUp() {
        securityUtilMockedStatic = Mockito.mockStatic(SecurityUtil.class);
    }

    @AfterEach
    void tearDown() {
        securityUtilMockedStatic.close();
    }

    @Test
    void createWarehouse_Success() {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setWarehouseCode("WH_NEW");
        dto.setWarehouseName("New Warehouse");

        when(warehouseRepository.existsByWarehouseCode("WH_NEW")).thenReturn(false);
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        when(warehouseMapper.toEntity(dto)).thenReturn(warehouse);
        when(warehouseRepository.save(warehouse)).thenReturn(warehouse);
        when(warehouseMapper.toDTO(warehouse)).thenReturn(dto);

        WarehouseDTO result = warehouseService.createWarehouse(dto);

        assertNotNull(result);
        assertEquals("WH_NEW", result.getWarehouseCode());
        verify(warehouseRepository).save(warehouse);
        verify(auditLogService).log(eq("CREATE"), eq("WAREHOUSE"), eq(1L), any(), eq(dto));
    }

    @Test
    void createWarehouse_Conflict() {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setWarehouseCode("WH_EXIST");

        when(warehouseRepository.existsByWarehouseCode("WH_EXIST")).thenReturn(true);

        assertThrows(ConflictException.class, () -> warehouseService.createWarehouse(dto));
        verify(warehouseRepository, never()).save(any());
    }

    @Test
    void getWarehouseById_Admin_Success() {
        Long id = 1L;
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        WarehouseDTO dto = new WarehouseDTO();

        when(warehouseRepository.findById(id)).thenReturn(Optional.of(warehouse));
        securityUtilMockedStatic.when(() -> SecurityUtil.hasCurrentUserThisAuthority("ROLE_ADMIN")).thenReturn(true);
        when(warehouseMapper.toDTO(warehouse)).thenReturn(dto);

        WarehouseDTO result = warehouseService.getWarehouseById(id);
        assertNotNull(result);
    }

    @Test
    void getWarehouseById_Manager_Success() {
        Long id = 1L;
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        WarehouseDTO dto = new WarehouseDTO();

        when(warehouseRepository.findById(id)).thenReturn(Optional.of(warehouse));
        securityUtilMockedStatic.when(() -> SecurityUtil.hasCurrentUserThisAuthority("ROLE_ADMIN")).thenReturn(false);
        securityUtilMockedStatic.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("manager"));

        User manager = new User();
        manager.setWarehouseId(id);
        when(userRepository.findOneByEmail("manager")).thenReturn(Optional.of(manager));
        when(warehouseMapper.toDTO(warehouse)).thenReturn(dto);

        WarehouseDTO result = warehouseService.getWarehouseById(id);
        assertNotNull(result);
    }

    @Test
    void getWarehouseById_Manager_Forbidden_DifferentWarehouse() {
        Long id = 1L;
        // Warehouse warehouse = new Warehouse(); // Not needed
        // warehouse.setId(id);

        // when(warehouseRepository.findById(id)).thenReturn(Optional.of(warehouse)); //
        // Removed internal call stub

        securityUtilMockedStatic.when(() -> SecurityUtil.hasCurrentUserThisAuthority("ROLE_ADMIN")).thenReturn(false);
        securityUtilMockedStatic.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("manager"));

        User manager = new User();
        manager.setWarehouseId(2L); // Different ID
        when(userRepository.findOneByEmail("manager")).thenReturn(Optional.of(manager));

        assertThrows(EntityNotFoundException.class, () -> warehouseService.getWarehouseById(id));
    }
}
