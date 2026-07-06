package com.backend.account.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.backend.account.model.Account;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {

    Mono<Boolean> existsByNumber(String number);

    Mono<Boolean> existsByNumberAndIdNot(String number, Long id);

    @Query("SELECT * FROM accounts WHERE id = :id FOR UPDATE")
    Mono<Account> findByIdForUpdate(@Param("id") Long id);

    Flux<Account> findByClientId(Long clientId);
}
