package com.fincore.ledgerservice.controller;

import com.fincore.ledgerservice.controller.api.AccountController;
import com.fincore.ledgerservice.dto.AccountCreateDto;
import com.fincore.ledgerservice.dto.AccountResponseDto;
import com.fincore.ledgerservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountControllerImpl implements AccountController {

    private final AccountService accountService;


    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody AccountCreateDto dto,
                                                            @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        log.info("POST /api/v1/accounts - Request to create account for user with id: {}", ownerId);
        AccountResponseDto account = accountService.createAccount(dto, ownerId);
        log.info("POST /api/v1/accounts - Bank account: {} was successfully created", account);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getAllAccountsByUser(@AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        log.info("GET /api/v1/accounts - Fetching all accounts for user: {}", ownerId);
        List<AccountResponseDto> allAccountsByUser = accountService.getAllAccountsByUser(ownerId);
        log.info("GET /api/v1/accounts - Accounts for user: {} were successfully received", ownerId);
        return ResponseEntity.ok(allAccountsByUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAccountById(@PathVariable(name = "id") UUID id,
                                                             @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        log.info("GET /api/v1/accounts - getting account by id: {}", id);
        AccountResponseDto account = accountService.getAccountById(id, ownerId);
        log.info("GET /api/v1/accounts - Account by id: {} was successfully received for user: {}", id, ownerId);
        return ResponseEntity.ok(account);
    }

}
