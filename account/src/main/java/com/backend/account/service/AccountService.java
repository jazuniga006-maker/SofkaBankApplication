package com.backend.account.service;

import com.backend.account.model.dto.AccountDto;
import com.backend.account.model.dto.PartialAccountDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountService {

    public Flux<AccountDto> getAll();

    public Mono<AccountDto> getById(Long id);

    public Mono<AccountDto> create(AccountDto accountDto);

    public Mono<AccountDto> update(AccountDto accountDto);

    public Mono<AccountDto> partialUpdate(Long id, PartialAccountDto partialAccountDto);

    public Mono<Void> deleteById(Long id);
}
