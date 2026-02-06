package com.wms.backend.masterdata.warehouse.service.impl;

import com.wms.backend.audit.service.AuditLogService;
import com.wms.backend.auth.entity.User;
import com.wms.backend.auth.repository.UserRepository;
import com.wms.backend.masterdata.warehouse.dto.StorageLocationDTO;
import com.wms.backend.masterdata.warehouse.entity.StorageLocation;
import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import com.wms.backend.masterdata.warehouse.mapper.StorageLocationMapper;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageLocationServiceImplTest {

    @Mock
    private StorageLocationRepository locationRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private WarehouseZoneRepository zoneRepository;

    @Mock
    private StorageLocationMapper locationMapper;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StorageLocationServiceImpl locationService;

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
    void createLocation_Success() {
        StorageLocationDTO dto = new StorageLocationDTO();
        dto.setWarehouseId(1L);
        dto.setZoneId(2L);
        dto.setLocationCode("LOC001");

        when(warehouseRepository.existsById(1L)).thenReturn(true);
        when(zoneRepository.existsById(2L)).thenReturn(true);
        when(locationRepository.existsByWarehouseIdAndLocationCode(1L, "LOC001")).thenReturn(false);

        StorageLocation location = new StorageLocation();
        location.setId(100L);
        when(locationMapper.toEntity(dto)).thenReturn(location);
        when(locationRepository.save(location)).thenReturn(location);
        when(locationMapper.toDTO(location)).thenReturn(dto);

        StorageLocationDTO result = locationService.createLocation(dto);

        assertNotNull(result);
        verify(auditLogService).log(eq("CREATE"), eq("STORAGE_LOCATION"), eq(100L), any(), eq(dto));
    }

    @Test
    void deleteLocation_NotEmpty_Conflict() {
        Long id = 100L;
        StorageLocation location = new StorageLocation();
        location.setId(id);
        location.setCurrentCapacity(BigDecimal.TEN); // Not empty

        when(locationRepository.findById(id)).thenReturn(Optional.of(location));

        assertThrows(ConflictException.class, () -> locationService.deleteLocation(id));
        verify(locationRepository, never()).deleteById(any());
    }

    @Test
    void getLocationById_Manager_Success() {
        Long id = 100L;
        StorageLocation location = new StorageLocation();
        location.setId(id);
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        location.setWarehouse(warehouse);

        when(locationRepository.findById(id)).thenReturn(Optional.of(location));
        securityUtilMockedStatic.when(() -> SecurityUtil.hasCurrentUserThisAuthority("ROLE_ADMIN")).thenReturn(false);
        securityUtilMockedStatic.when(SecurityUtil::getCurrentUserLogin).thenReturn(Optional.of("manager"));

        User manager = new User();
        manager.setWarehouseId(1L);
        when(userRepository.findOneByEmail("manager")).thenReturn(Optional.of(manager));
        StorageLocationDTO dto = new StorageLocationDTO();
        when(locationMapper.toDTO(location)).thenReturn(dto);

        StorageLocationDTO result = locationService.getLocationById(id);
        assertNotNull(result);
    }
}
