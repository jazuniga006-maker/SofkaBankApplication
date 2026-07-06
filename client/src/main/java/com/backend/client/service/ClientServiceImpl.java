package com.backend.client.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.backend.client.model.Client;
import com.backend.client.model.dto.ClientDto;
import com.backend.client.model.dto.PartialClientDto;
import com.backend.client.repository.ClientRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientServiceImpl(ClientRepository clientRepository, PasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @CircuitBreaker(name = "clientService", fallbackMethod = "getEmptyList")
    @Retry(name = "clientService", fallbackMethod = "getEmptyList")
    public Flux<ClientDto> getAll() {
        return clientRepository.findAll()
                .map(this::mapToDto);
    }

    public Flux<ClientDto> getEmptyList(Throwable e) {
        log.error("CircuitBreaker", e.getMessage());
        return Flux.empty();
    }

    @Override
    public Mono<ClientDto> getById(Long id) {
        return clientRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found")))
                .map(this::mapToDto);

    }

    @Override
    public Mono<ClientDto> create(ClientDto clientDto) {

        return clientRepository.existsByDni(clientDto.getDni())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(
                                new ResponseStatusException(HttpStatus.CONFLICT, "DNI already exists"));
                    }

                    Client client = mapToEntity(clientDto);

                    client.setId(null);
                    client.setPassword(passwordEncoder.encode(client.getPassword()));

                    return clientRepository.save(client)
                            .map(this::mapToDto);
                });
    }

    @Override
    public Mono<ClientDto> update(ClientDto clientDto) {
        if (clientDto.getId() == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client id is required"));
        }

        return clientRepository.findById(clientDto.getId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found")))
                .flatMap(client -> {

                    client.setName(clientDto.getName());
                    client.setPassword(passwordEncoder.encode(clientDto.getPassword()));
                    client.setGender(clientDto.getGender());
                    client.setAge(clientDto.getAge());
                    client.setAddress(clientDto.getAddress());
                    client.setPhone(clientDto.getPhone());

                    return clientRepository.save(client);
                })
                .map(this::mapToDto);
    }

    @Override
    public Mono<ClientDto> partialUpdate(Long id, PartialClientDto partialClientDto) {
        return clientRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found")))
                .flatMap(client -> {
                    client.setActive(partialClientDto.isActive());
                    return clientRepository.save(client);
                })
                .map(this::mapToDto);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return clientRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found")))
                .flatMap(clientRepository::delete);
    }

    private ClientDto mapToDto(Client client) {
        return new ClientDto(
                client.getId(),
                client.getDni(),
                client.getName(),
                client.getPassword(),
                client.getGender(),
                client.getAge(),
                client.getAddress(),
                client.getPhone(),
                client.isActive());
    }

    private Client mapToEntity(ClientDto dto) {
        Client client = new Client();

        client.setId(dto.getId());
        client.setDni(dto.getDni());
        client.setName(dto.getName());
        client.setPassword(dto.getPassword());
        client.setGender(dto.getGender());
        client.setAge(dto.getAge());
        client.setAddress(dto.getAddress());
        client.setPhone(dto.getPhone());
        client.setActive(dto.isActive());

        return client;
    }
}
