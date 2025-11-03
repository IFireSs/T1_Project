package com.client_processing.mapper;

import com.client_processing.dto.ProductDto;
import com.client_processing.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    ProductDto toDto(Product product);
    List<ProductDto> toDtoList(List<Product> products);
}
