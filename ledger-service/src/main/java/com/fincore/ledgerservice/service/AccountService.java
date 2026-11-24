package com.fincore.ledgerservice.service;

import com.fincore.ledgerservice.dto.AccountCreateDto;
import com.fincore.ledgerservice.dto.AccountResponseDto;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountResponseDto createAccount(AccountCreateDto accountCreateDto, UUID ownerId);

    List<AccountResponseDto> getAllAccountsByUser(UUID ownerId);

    AccountResponseDto getAccountById(UUID id, UUID ownerId);
}
