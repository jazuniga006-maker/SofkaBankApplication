package com.backend.client.model;

import lombok.Data;

@Data
public class Person extends Base {
	private String name;
	private String dni;
	private String gender;
	private int age;
	private String address;
	private String phone;
}
