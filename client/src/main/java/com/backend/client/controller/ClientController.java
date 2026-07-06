package com.backend.client.controller;

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

import com.backend.client.model.dto.ClientDto;
import com.backend.client.model.dto.PartialClientDto;
import com.backend.client.service.ClientService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public Flux<ClientDto> getAll() {
        return clientService.getAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ClientDto>> getById(@PathVariable Long id) {
        return clientService.getById(id)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<ClientDto>> create(@RequestBody @Valid ClientDto clientDto) {
        return clientService.create(clientDto)
                .map(client -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(client));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ClientDto>> update(@PathVariable Long id, @RequestBody @Valid ClientDto clientDto) {

        clientDto.setId(id);
        
        return clientService.update(clientDto)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<ClientDto>> partialUpdate(
            @PathVariable Long id,
            @RequestBody PartialClientDto partialClientDto) {

        return clientService.partialUpdate(id, partialClientDto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return clientService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
