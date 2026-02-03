package com.wms.backend.masterdata.product.mapper;

import com.wms.backend.masterdata.product.dto.CategoryDTO;
import com.wms.backend.masterdata.product.entity.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(source = "parentId", target = "parent.id")
    Category toEntity(CategoryDTO dto);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "parent.categoryName", target = "parentName")
    CategoryDTO toDTO(Category entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "parentId", target = "parent.id")
    void partialUpdate(@MappingTarget Category entity, CategoryDTO dto);
}
