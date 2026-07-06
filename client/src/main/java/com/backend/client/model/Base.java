package com.backend.client.model;


import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class Base {

	@Id
	private Long id;
}
