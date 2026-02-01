package com.wms.backend.masterdata.warehouse.mapper;

import com.wms.backend.masterdata.warehouse.dto.WarehouseDTO;
import com.wms.backend.masterdata.warehouse.entity.Warehouse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WarehouseMapper {

    Warehouse toEntity(WarehouseDTO dto);

    WarehouseDTO toDTO(Warehouse entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Warehouse entity, WarehouseDTO dto);
}
