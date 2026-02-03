package com.wms.backend.masterdata.warehouse.mapper;

import com.wms.backend.masterdata.warehouse.dto.WarehouseZoneDTO;
import com.wms.backend.masterdata.warehouse.entity.WarehouseZone;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WarehouseZoneMapper {

    @Mapping(source = "warehouseId", target = "warehouse.id")
    WarehouseZone toEntity(WarehouseZoneDTO dto);

    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.warehouseName", target = "warehouseName")
    WarehouseZoneDTO toDTO(WarehouseZone entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "warehouseId", target = "warehouse.id")
    void partialUpdate(@MappingTarget WarehouseZone entity, WarehouseZoneDTO dto);
}
