package com.client_processing.mapper;

import com.client_processing.dto.ClientProductDto;
import com.client_processing.dto.kafka.ClientProductMessage;
import com.client_processing.dto.CreateCardDto;
import com.client_processing.dto.kafka.CreateCardRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = Instant.class)
public interface KafkaMapper {
    @Mapping(target = "op", source = "op")
    @Mapping(target = "clientId", source = "dto.clientId")
    @Mapping(target = "productId", source = "dto.productId")
    @Mapping(target = "openDate", source = "dto.openDate")
    @Mapping(target = "closeDate", source = "dto.closeDate")
    ClientProductMessage toClientProductMessage(ClientProductDto dto, ClientProductMessage.Op op);

    @Mapping(target = "clientId", source = "dto.clientId")
    @Mapping(target = "productId", source = "dto.productId")
    @Mapping(target = "paymentSystem", source = "dto.paymentSystem")
    @Mapping(target = "ts", expression = "java(Instant.now())")
    CreateCardRequest toCreateCardRequest(CreateCardDto dto);
}
