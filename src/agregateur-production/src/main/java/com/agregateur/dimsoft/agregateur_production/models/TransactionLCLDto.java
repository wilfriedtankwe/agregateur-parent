package com.agregateur.dimsoft.agregateur_production.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TransactionLCLDto {
    private Date transactionLCLDate;

    private String transactionLCLLibelle;

    private Double transactionLCLMontant;

    private Long transactionChequeNumber;

    private String transactionNotes;

}
