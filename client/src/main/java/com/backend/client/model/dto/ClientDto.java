package com.backend.client.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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

	@JsonProperty("active")
	@JsonAlias({"isActive", "active"})
	private boolean active;
}
