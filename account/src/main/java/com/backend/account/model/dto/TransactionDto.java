package com.backend.account.model.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

	private Long id;
	private LocalDateTime date;
	@NotBlank
	private String type;
	@NotNull
	private Double amount;
	private Double balance;
	@NotNull
	private Long accountId;
}
