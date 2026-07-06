package com.backend.account.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.backend.account.model.Account;
import com.backend.account.model.dto.AccountDto;
import com.backend.account.model.dto.PartialAccountDto;
import com.backend.account.model.dto.TransactionDto;
import com.backend.account.repository.AccountRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final ClientService clientService;

    public AccountServiceImpl(AccountRepository accountRepository, TransactionService transactionService,
            ClientService clientService) {
        this.accountRepository = accountRepository;
        this.transactionService = transactionService;
        this.clientService = clientService;
    }

    @Override
    public Flux<AccountDto> getAll() {
        return accountRepository.findAll()
                .map(this::mapToDto);
    }

    @Override
    public Mono<AccountDto> getById(Long id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")))
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public Mono<AccountDto> create(AccountDto accountDto) {

        return validateAccountNumber(accountDto.getNumber())
                .then(saveAccount(accountDto))
                .flatMap(this::createInitialTransaction);
    }

    @Override
    public Mono<AccountDto> update(AccountDto accountDto) {
        if (accountDto.getId() == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Account id is required"));
        }

        return accountRepository.findById(accountDto.getId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")))
                .flatMap(account -> accountRepository.existsByNumberAndIdNot(accountDto.getNumber(), accountDto.getId())
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
                                        "Account number already exists"));
                            }

                            account.setNumber(accountDto.getNumber());
                            account.setType(accountDto.getType());
                            account.setActive(accountDto.isActive());
                            account.setClientId(accountDto.getClientId());

                            return accountRepository.save(account);
                        }))
                .map(this::mapToDto);
    }

    @Override
    public Mono<AccountDto> partialUpdate(Long id, PartialAccountDto partialAccountDto) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")))
                .flatMap(account -> {
                    account.setActive(partialAccountDto.isActive());
                    return accountRepository.save(account);
                })
                .map(this::mapToDto);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")))
                .flatMap(accountRepository::delete);
    }

    private Mono<Void> validateAccountNumber(String number) {
        return accountRepository.existsByNumber(number)
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
                                "Account number already exists"));
                    }

                    return Mono.empty();
                });
    }

    private Mono<Account> saveAccount(AccountDto accountDto) {
        Account account = mapToEntity(accountDto);
        account.setId(null);

        return accountRepository.save(account);
    }

    private Mono<AccountDto> createInitialTransaction(Account account) {
        return transactionService.create(buildInitialTransaction(account))
                .thenReturn(mapToDto(account));
    }

    private TransactionDto buildInitialTransaction(Account account) {
        TransactionDto transaction = new TransactionDto();

        transaction.setAccountId(account.getId());
        transaction.setAmount(account.getInitialAmount());
        transaction.setBalance(account.getInitialAmount());
        transaction.setType("ING");
        transaction.setDate(LocalDateTime.now());

        return transaction;
    }

    private AccountDto mapToDto(Account account) {
        return new AccountDto(
                account.getId(),
                account.getNumber(),
                account.getType(),
                account.getInitialAmount(),
                account.isActive(),
                account.getClientId());
    }

    private Account mapToEntity(AccountDto dto) {
        Account account = new Account();

        account.setId(dto.getId());
        account.setNumber(dto.getNumber());
        account.setType(dto.getType());
        account.setInitialAmount(dto.getInitialAmount());
        account.setActive(dto.isActive());
        account.setClientId(dto.getClientId());

        return account;
    }

}
