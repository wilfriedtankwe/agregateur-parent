package com.agregateur.dimsoft.agregateur_production.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TransactionCADto {
    private Date transactionCADate;

    private String transactionCALibelle;

    private Double transactionCADebit;

    private Double transactionCACredit;
}
