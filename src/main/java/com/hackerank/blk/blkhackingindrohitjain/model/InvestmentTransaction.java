package com.hackerank.blk.blkhackingindrohitjain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;

import java.time.LocalDateTime;

@Log
@Getter
@Setter
@AllArgsConstructor
@ToString
@JsonPropertyOrder(value = { "date", "amount", "ceiling", "remanent" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvestmentTransaction extends Transaction {
    
    private double ceiling;
    private double remanent;

    @JsonIgnore
    private TransactionType type;

    private String message;
    
    public InvestmentTransaction(LocalDateTime transactionDate, Double amount) {
        super(transactionDate, amount);

        if (amount != null) {
            this.ceiling = getCeilOfHundred(amount);
            this.remanent = this.ceiling - amount;
        }
    }
    
    public InvestmentTransaction(Transaction transaction) {
        this(transaction.getDate(), transaction.getAmount());
    }

    private double getCeilOfHundred(double inputAmount) {
        return Math.ceil(inputAmount / 100) * 100;
    }

    public InvestmentTransaction validate(Double wage) {
        if (this.getAmount() == null || wage == null) {
            this.type = TransactionType.INVALID;
            this.message = "Amount and Wage cannot be null";
        } else if (this.getAmount() < 0) {
            this.type = TransactionType.INVALID;
            this.message = "Negative amounts are not allowed";
        } else if (this.getAmount() > wage) {
            this.type = TransactionType.INVALID;
            this.message = "Amount cannot exceed wage";
        }

        if (this.getType() != TransactionType.INVALID) {
            this.type = TransactionType.VALID;
        }

        return this;
    }
}
