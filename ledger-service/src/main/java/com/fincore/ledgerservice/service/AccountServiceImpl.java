package com.fincore.ledgerservice.service;

import com.fincore.ledgerservice.dto.AccountCreateDto;
import com.fincore.ledgerservice.dto.AccountResponseDto;
import com.fincore.ledgerservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;


    @Override
    public AccountResponseDto createAccount(AccountCreateDto accountCreateDto) {
        return null;
    }

    @Override
    public List<AccountResponseDto> getAllAccountsByUser() {
        return List.of();
    }

    @Override
    public AccountResponseDto getAccountById(String id) {
        return null;
    }
}
