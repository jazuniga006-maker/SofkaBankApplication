package com.backend.client.service;

import com.backend.client.model.dto.ClientDto;
import com.backend.client.model.dto.PartialClientDto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClientService {

    public Flux<ClientDto> getAll();
	public Mono<ClientDto> getById(Long id);
	public Mono<ClientDto> create(ClientDto clientDto);
	public Mono<ClientDto> update(ClientDto clientDto);
	public Mono<ClientDto> partialUpdate(Long id, PartialClientDto partialClientDto);
	public Mono<Void> deleteById(Long id);
}
