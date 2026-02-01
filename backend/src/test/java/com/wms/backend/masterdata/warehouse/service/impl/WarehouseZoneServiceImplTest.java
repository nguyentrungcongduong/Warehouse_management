package com.wms.backend.masterdata.warehouse.service.impl;

import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.auth.entity.User;
import com.wms.backend.auth.repository.UserRepository;
import com.wms.backend.masterdata.warehouse.dto.WarehouseZoneDTO;
import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import com.wms.backend.masterdata.warehouse.entity.WarehouseZone;
import com.wms.backend.masterdata.warehouse.mapper.WarehouseZoneMapper;
import com.wms.backend.masterdata.warehouse.repository.StorageLocationRepository;
import com.wms.backend.masterdata.warehouse.repository.WarehouseRepository;
import com.wms.backend.masterdata.warehouse.repository.WarehouseZoneRepository;
import com.wms.backend.shared.exception.ConflictException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseZoneServiceImplTest {

    @Mock
    private WarehouseZoneRepository zoneRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private WarehouseZoneMapper zoneMapper;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StorageLocationRepository storageLocationRepository;

    @InjectMocks
    private WarehouseZoneServiceImpl zoneService;

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
    void createZone_Success() {
        WarehouseZoneDTO dto = new WarehouseZoneDTO();
        dto.setWarehouseId(1L);
        dto.setZoneCode("Z01");

        when(warehouseRepository.existsById(1L)).thenReturn(true);
        when(zoneRepository.existsByWarehouseIdAndZoneCode(1L, "Z01")).thenReturn(false);

        WarehouseZone zone = new WarehouseZone();
        zone.setId(10L);
        when(zoneMapper.toEntity(dto)).thenReturn(zone);
        when(zoneRepository.save(zone)).thenReturn(zone);
        when(zoneMapper.toDTO(zone)).thenReturn(dto);

        WarehouseZoneDTO result = zoneService.createZone(dto);

        assertNotNull(result);
        verify(zoneRepository).save(zone);
        verify(auditLogService).log(eq("CREATE"), eq("WAREHOUSE_ZONE"), eq(10L), any(), eq(dto));
    }

    @Test
    void getZoneById_Admin_Success() {
        Long id = 10L;
        WarehouseZone zone = new WarehouseZone();
        zone.setId(id);
        // WarehouseZoneDTO dto = new WarehouseZoneDTO(); // Not strictly needed
        // variable, can inline

        // Note: Logic in service: 1. findById, 2. Check Security
        when(zoneRepository.findById(id)).thenReturn(Optional.of(zone));
        securityUtilMockedStatic.when(() -> SecurityUtil.hasCurrentUserThisAuthority("ROLE_ADMIN")).thenReturn(true);
        WarehouseZoneDTO zoneDTO = new WarehouseZoneDTO();
        when(zoneMapper.toDTO(zone)).thenReturn(zoneDTO);

        WarehouseZoneDTO result = zoneService.getZoneById(id);
        assertNotNull(result);
    }

    @Test
    void getZoneById_Manager_Success() {
        Long id = 10L;
        WarehouseZone zone = new WarehouseZone();
        zone.setId(id);
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        zone.setWarehouse(warehouse);

        when(zoneRepository.findById(id)).thenReturn(Optional.of(zone));
        securityUtilMockedStatic.when(() -> SecurityUtil.hasCurrentUserThisAuthority("ROLE_ADMIN")).thenReturn(false);
        securityUtilMockedStatic.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("manager"));

        User manager = new User();
        manager.setWarehouseId(1L);
        when(userRepository.findByEmail("manager")).thenReturn(Optional.of(manager));
        WarehouseZoneDTO zoneDTO = new WarehouseZoneDTO();
        when(zoneMapper.toDTO(zone)).thenReturn(zoneDTO);

        WarehouseZoneDTO result = zoneService.getZoneById(id);
        assertNotNull(result);
    }

    @Test
    void deleteZone_HasLocations_Conflict() {
        Long id = 10L;
        when(storageLocationRepository.existsByZoneId(id)).thenReturn(true);

        assertThrows(ConflictException.class, () -> zoneService.deleteZone(id));
        verify(zoneRepository, never()).deleteById(any());
    }
}
