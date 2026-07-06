package com.backend.account.service;

import java.time.LocalDateTime;

import org.springframework.data.repository.query.Param;

import com.backend.account.model.dto.BankStatementDto;
import com.backend.account.model.dto.TransactionDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {

    public Flux<TransactionDto> getAll();

    public Mono<TransactionDto> getById(Long id);

    public Mono<TransactionDto> create(TransactionDto transactionDto);

    public Flux<BankStatementDto> getAllByAccountClientIdAndDateBetween(Long clientId,
            @Param("dateTransactionStart") LocalDateTime dateTransactionStart,
            @Param("dateTransactionEnd") LocalDateTime dateTransactionEnd);

    public Mono<TransactionDto> getLastByAccountId(Long accountId);
}
