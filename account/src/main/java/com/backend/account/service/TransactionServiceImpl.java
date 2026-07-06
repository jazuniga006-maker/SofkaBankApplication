package com.backend.account.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.backend.account.model.Account;
import com.backend.account.model.Transaction;
import com.backend.account.model.dto.BankStatementDto;
import com.backend.account.model.dto.TransactionDto;
import com.backend.account.repository.AccountRepository;
import com.backend.account.repository.TransactionRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ClientService clientService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository,
            ClientService clientService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.clientService = clientService;
    }

    @Override
    public Flux<TransactionDto> getAll() {
        return transactionRepository.findAll()
                .map(this::mapToDto);
    }

    @Override
    public Mono<TransactionDto> getById(Long id) {
        return transactionRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found")))
                .map(this::mapToDto);
    }

    @Override
    public Mono<TransactionDto> create(TransactionDto transactionDto) {
        return accountRepository.findByIdForUpdate(transactionDto.getAccountId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")))
                .flatMap(account -> validateAccount(account)
                        .then(validateClient(account))
                        .then(createTransaction(account, transactionDto)));
    }

    @Override
    public Flux<BankStatementDto> getAllByAccountClientIdAndDateBetween(Long clientId,
            LocalDateTime dateTransactionStart, LocalDateTime dateTransactionEnd) {
        return clientService.getById(clientId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found")))
                .flatMapMany(client -> accountRepository.findByClientId(clientId)
                        .flatMap(account -> transactionRepository
                                .findByAccountIdAndDateBetween(account.getId(), dateTransactionStart,
                                        dateTransactionEnd)
                                .map(transaction -> new BankStatementDto(
                                        transaction.getDate(),
                                        client.getName(),
                                        account.getNumber(),
                                        account.getType(),
                                        account.getInitialAmount(),
                                        account.isActive(),
                                        transaction.getType(),
                                        transaction.getAmount(),
                                        transaction.getBalance()))
                                .switchIfEmpty(Mono.just(new BankStatementDto(
                                        LocalDateTime.now(),
                                        client.getName(),
                                        account.getNumber(),
                                        account.getType(),
                                        account.getInitialAmount(),
                                        account.isActive(),
                                        "SIN MOVIMIENTOS",
                                        0.0,
                                        account.getInitialAmount())))));
    }

    @Override
    public Mono<TransactionDto> getLastByAccountId(Long accountId) {
        return transactionRepository.findFirstByAccountIdOrderByDateDesc(accountId)
                .map(this::mapToDto);
    }

    private Mono<Void> validateAccount(Account account) {
        if (!account.isActive()) {
            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Inactive account"));
        }

        return Mono.empty();
    }

    private Mono<Void> validateClient(Account account) {
        return clientService.getById(account.getClientId())
                .flatMap(client -> {
                    if (!client.isActive()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Inactive Cliente"));
                    }

                    return Mono.empty();
                });
    }

    private Mono<TransactionDto> createTransaction(Account account, TransactionDto transactionDto) {
        return getLastByAccountId(account.getId())
                .defaultIfEmpty(new TransactionDto(null, null, null, 0.0, 0.0, account.getId()))
                .flatMap(lastTransaction -> saveTransaction(account, transactionDto, lastTransaction.getBalance()));
    }

    private Mono<TransactionDto> saveTransaction(Account account, TransactionDto transactionDto,
            double currentBalance) {
                
        double newBalance = currentBalance + transactionDto.getAmount();

        if (transactionDto.getAmount() < 0 && newBalance < 0) {
            return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Balance not available"));
        }

        Transaction transaction = buildTransaction(account, transactionDto, newBalance);

        return transactionRepository.save(transaction)
                .flatMap(saved -> updateAccountBalance(account, newBalance)
                        .thenReturn(mapToDto(saved)));
    }

    private Transaction buildTransaction(Account account, TransactionDto transactionDto, double newBalance) {
        Transaction transaction = new Transaction();

        transaction.setDate(LocalDateTime.now());
        transaction.setType(transactionDto.getType());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setBalance(newBalance);
        transaction.setAccountId(account.getId());

        return transaction;
    }

    private Mono<Account> updateAccountBalance(Account account, double newBalance) {
        account.setInitialAmount(newBalance);
        return accountRepository.save(account);
    }

    private TransactionDto mapToDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getDate(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalance(),
                transaction.getAccountId());
    }
}
