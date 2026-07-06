package com.backend.client.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleResponseStatusException(ResponseStatusException ex) {
        return Mono.just(
                ResponseEntity.status(ex.getStatusCode())
                        .body(Map.of(
                                "status", ex.getStatusCode().value(),
                                "error", ex.getReason(),
                                "date", new Date().toString())));
    }
}
