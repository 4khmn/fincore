package com.fincore.ledgerservice.controller;

import com.fincore.ledgerservice.dto.AccountCreateDto;
import com.fincore.ledgerservice.dto.AccountResponseDto;
import com.fincore.ledgerservice.service.AccountService;
import com.fincore.ledgerservice.service.AccountServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;


    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody AccountCreateDto dto,
                                                            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        log.info("ownerId={}", ownerId);
        log.info("POST /api/v1/accounts - Request to create account for user with id: {}", ownerId);
        AccountResponseDto account = accountService.createAccount(dto);
        log.info("POST /api/v1/accounts - Bank account: {} was successfully created", account);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getAllAccountsByUser() {
        log.info("GET /api/v1/accounts - Fetching all accounts for user: {}", 1);
        List<AccountResponseDto> allAccountsByUser = accountService.getAllAccountsByUser();
        log.info("GET /api/v1/accounts - Accounts for user: {} were successfully received", allAccountsByUser);
        return ResponseEntity.ok(allAccountsByUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAccountById(@PathVariable String id) {
        log.info("GET /api/v1/accounts - getting account by id: {}", id);
        AccountResponseDto account = accountService.getAccountById(id);
        log.info("GET /api/v1/accounts - Account by id: {} was successfully received: {}", id, account);
        return ResponseEntity.ok(account);
    }


}
