package com.backend.account.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.account.model.dto.BankStatementDto;
import com.backend.account.model.dto.TransactionDto;
import com.backend.account.service.TransactionService;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/movements")
public class MovementController {

    private final TransactionService transactionService;

    public MovementController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

    @GetMapping
    public Flux<TransactionDto> getAll() {
        return transactionService.getAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TransactionDto>> get(@PathVariable Long id) {
        return transactionService.getById(id)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<TransactionDto>> create(@Valid @RequestBody TransactionDto transactionDto) {
        return transactionService.create(transactionDto)
                .map(created -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(created));
    }

    @GetMapping("clients/{clientId}/report")
    public Flux<BankStatementDto> report(
            @PathVariable Long clientId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTransactionStart,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dateTransactionEnd) {
        return transactionService.getAllByAccountClientIdAndDateBetween(
                clientId,
                dateTransactionStart.atStartOfDay(),
                dateTransactionEnd.atTime(LocalTime.MAX));
    }
}
