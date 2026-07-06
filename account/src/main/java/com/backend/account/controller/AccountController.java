package com.backend.account.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.account.model.dto.AccountDto;
import com.backend.account.model.dto.PartialAccountDto;
import com.backend.account.service.AccountService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public Flux<AccountDto> getAll() {
        return accountService.getAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AccountDto>> getById(@PathVariable Long id) {
        return accountService.getById(id)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<AccountDto>> create(@RequestBody @Valid AccountDto accountDto) {
        return accountService.create(accountDto)
                .map(account -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(account));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<AccountDto>> update(@PathVariable Long id, @RequestBody @Valid AccountDto accountDto) {
        accountDto.setId(id);

        return accountService.update(accountDto)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<AccountDto>> partialUpdate(
            @PathVariable Long id,
            @RequestBody PartialAccountDto partialAccountDto) {
        return accountService.partialUpdate(id, partialAccountDto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable Long id) {
        return accountService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
