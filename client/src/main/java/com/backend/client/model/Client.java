package com.backend.client.model;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table("clients")
@Data
public class Client extends Person {
	private String password;
	private boolean isActive;
}
