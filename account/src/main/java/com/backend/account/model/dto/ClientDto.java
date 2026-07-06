package com.backend.account.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientDto {

	private Long id;

	@NotBlank
	private String dni;

	@NotBlank
	private String name;

	@NotBlank
	private String password;

	@NotBlank
	private String gender;

	@Min(0)
	private int age;

	@NotBlank
	private String address;

	@NotBlank
	private String phone;

	private boolean active;
}
