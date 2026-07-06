package com.backend.account.model;

import java.time.LocalDateTime;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table("transactions")
@Data
public class Transaction extends Base {

	private LocalDateTime date;
	private String type;
	private double amount;
	private double balance;

	@Column("account_id")
	private Long accountId;
}
