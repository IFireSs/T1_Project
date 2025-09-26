package com.client_processing.mapper;

import com.client_processing.dto.ClientDto;
import com.client_processing.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClientMapper {
    ClientDto toDto(Client client);
    List<ClientDto> toDtoList(List<Client> clients);
}
