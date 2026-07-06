package com.backend.account.model;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table("accounts")
@Data
public class Account extends Base {
    private String number;
    private String type;
    private double initialAmount;
    private boolean isActive;

    @Column("client_id")
    private Long clientId;
}
