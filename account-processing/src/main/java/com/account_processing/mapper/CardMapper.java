package com.account_processing.mapper;

import com.account_processing.dto.CardDto;
import com.account_processing.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CardMapper {
    CardDto toDto(Card card);
    List<CardDto> toDtoList(List<Card> cards);
}
