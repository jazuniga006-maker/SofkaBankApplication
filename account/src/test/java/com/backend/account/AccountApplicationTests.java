package com.backend.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.backend.account.controller.AccountController;
import com.backend.account.model.dto.AccountDto;
import com.backend.account.service.AccountService;

import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountApplicationTests {

	private AccountService accountService;
	private AccountController accountController;

	@BeforeEach
	void setUp() {
		accountService = mock(AccountService.class);
		accountController = new AccountController(accountService);
	}

	@Test
	void createAccount() {
		AccountDto request = new AccountDto(null, "123456", "AHO", 100.0, true, 1L);
		AccountDto response = new AccountDto(1L, "123456", "AHO", 100.0, true, 1L);

		when(accountService.create(any(AccountDto.class))).thenReturn(Mono.just(response));

		ResponseEntity<AccountDto> result = accountController.create(request).block();

		assertNotNull(result);
		assertEquals(HttpStatus.CREATED, result.getStatusCode());
		assertEquals(response, result.getBody());
	}

}
