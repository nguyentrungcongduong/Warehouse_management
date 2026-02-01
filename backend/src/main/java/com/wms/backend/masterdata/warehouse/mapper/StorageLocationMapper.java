package com.wms.backend.masterdata.warehouse.mapper;

import com.wms.backend.masterdata.warehouse.dto.StorageLocationDTO;
import com.wms.backend.masterdata.warehouse.entity.StorageLocation;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StorageLocationMapper {

    @Mapping(source = "warehouseId", target = "warehouse.id")
    @Mapping(source = "zoneId", target = "zone.id")
    StorageLocation toEntity(StorageLocationDTO dto);

    @Mapping(source = "warehouse.id", target = "warehouseId")
    @Mapping(source = "warehouse.warehouseName", target = "warehouseName")
    @Mapping(source = "zone.id", target = "zoneId")
    @Mapping(source = "zone.zoneName", target = "zoneName")
    StorageLocationDTO toDTO(StorageLocation entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "warehouseId", target = "warehouse.id")
    @Mapping(source = "zoneId", target = "zone.id")
    void partialUpdate(@MappingTarget StorageLocation entity, StorageLocationDTO dto);
}
