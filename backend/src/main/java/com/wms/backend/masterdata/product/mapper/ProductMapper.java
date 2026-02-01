package com.wms.backend.masterdata.product.mapper;

import com.wms.backend.masterdata.product.dto.ProductDTO;
import com.wms.backend.masterdata.product.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(source = "categoryId", target = "category.id")
    Product toEntity(ProductDTO dto);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.categoryName", target = "categoryName")
    ProductDTO toDTO(Product entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "categoryId", target = "category.id")
    void partialUpdate(@MappingTarget Product entity, ProductDTO dto);
}
