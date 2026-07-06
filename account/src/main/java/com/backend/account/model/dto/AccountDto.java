package com.backend.account.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDto {

	private Long id;

	@NotBlank
	private String number;

	@NotBlank
	private String type;

	@Min(0)
	private double initialAmount;

	private boolean isActive;

	@NotNull
	private Long clientId;
}
