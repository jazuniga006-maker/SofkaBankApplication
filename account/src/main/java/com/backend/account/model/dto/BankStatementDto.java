package com.backend.account.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BankStatementDto {
    
	private LocalDateTime date;
	private String client;
	private String accountNumber;
	private String accountType;
	private double initialAmount;
    private boolean isActive;
	private String transactionType;
	private double amount;
	private double balance;
}
