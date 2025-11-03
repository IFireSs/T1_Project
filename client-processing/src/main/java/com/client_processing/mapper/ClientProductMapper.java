package com.client_processing.mapper;

import com.client_processing.dto.ClientProductDto;
import com.client_processing.entity.ClientProduct;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientProductMapper {
    ClientProductDto toDto(ClientProduct clientProduct);
    List<ClientProductDto> toDtoList(List<ClientProduct> clientProducts);
}
