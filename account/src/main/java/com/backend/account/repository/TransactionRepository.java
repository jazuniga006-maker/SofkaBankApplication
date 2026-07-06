package com.backend.account.repository;

import java.time.LocalDateTime;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.backend.account.model.Transaction;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, Long> {

    Mono<Transaction> findFirstByAccountIdOrderByDateDesc(Long accountId);

    Flux<Transaction> findByAccountIdAndDateBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
}
