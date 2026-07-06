package com.backend.account.service;

import com.backend.account.model.dto.ClientDto;

import reactor.core.publisher.Mono;

public interface ClientService {

    public Mono<ClientDto> getById(Long clientId);
}
