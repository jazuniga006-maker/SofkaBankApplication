package com.backend.client.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.backend.client.model.Client;

import reactor.core.publisher.Mono;

public interface ClientRepository extends ReactiveCrudRepository<Client, Long> {

    Mono<Boolean> existsByDni(String dni);
}
