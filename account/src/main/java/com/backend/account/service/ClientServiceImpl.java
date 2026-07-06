package com.backend.account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.backend.account.model.dto.ClientDto;

import reactor.core.publisher.Mono;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    WebClient webClient;

    @Override
    public Mono<ClientDto> getById(Long clientId) {
        return webClient.get()
                .uri("/{clientId}", clientId)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(ClientDto.class);
                    } else if (response.statusCode().value() == 404) {
                        return Mono.empty();
                    } else {
                        return response.createException()
                                .flatMap(Mono::error);
                    }
                });
    }

}
