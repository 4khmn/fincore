package com.fincore.ledgerservice.mapper;

import com.fincore.ledgerservice.dto.AccountCreateDto;
import com.fincore.ledgerservice.dto.AccountResponseDto;
import com.fincore.ledgerservice.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "balance", source = "balance.balanceMinor", qualifiedByName = "minorToMajor")
    @Mapping(target = "accountStatus", source = "status")
    AccountResponseDto toDto(Account account);

    @Mapping(target = "currency", source = "accountCurrency")
    @Mapping(target = "accountType", constant = "USER")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "balance", ignore = true)
    Account toEntity(AccountCreateDto dto);

    @Named("minorToMajor")
    static BigDecimal minorToMajor(Long minor) {
        if (minor == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(minor, 2);
    }
}