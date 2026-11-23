package com.fincore.ledgerservice.service;

import com.fincore.ledgerservice.dto.AccountCreateDto;
import com.fincore.ledgerservice.dto.AccountResponseDto;

import java.util.List;

public interface AccountService {

    AccountResponseDto createAccount(AccountCreateDto accountCreateDto);

    List<AccountResponseDto> getAllAccountsByUser();

    AccountResponseDto getAccountById(String id);
}
