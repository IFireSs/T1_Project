package com.account_processing.mapper;

import com.account_processing.dto.AccountDto;
import com.account_processing.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {
    AccountDto toDto(Account account);
    List<AccountDto> toDtoList(List<Account> clientProducts);
}
