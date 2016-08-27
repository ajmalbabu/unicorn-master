package com.unicorn.service.domain;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static com.unicorn.service.domain.BankTransaction.BankTransactionType.DEPOSIT;
import static com.unicorn.service.domain.BankTransaction.BankTransactionType.WITHDRAW;


public class BankTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    // A unique ID for every transaction.
    private UUID transactionId;
    private BankTransactionType bankTransactionType;
    private Double transactionAmount;

    private Map<String, Object> mdc;


    public BankTransaction() {
        transactionId = UUID.randomUUID();
    }

    public BankTransaction(BankTransactionType bankTransactionType, Double transactionAmount) {
        this();
        this.bankTransactionType = bankTransactionType;
        this.transactionAmount = transactionAmount;
    }

    public void setMdc(Map<String, Object> mdc) {
        this.mdc = mdc;
    }

    public Map<String, Object> getMdc() {
        return mdc;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BankTransactionType getBankTransactionType() {
        return bankTransactionType;
    }

    public void setBankTransactionType(BankTransactionType bankTransactionType) {
        this.bankTransactionType = bankTransactionType;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public boolean deposit() {
        return bankTransactionType == DEPOSIT;
    }

    public double getAdjustedTransactionAmount() {
        if (deposit()) {
            return getTransactionAmount();
        }
        return getTransactionAmount() * -1;
    }


    public boolean withdraw() {
        return bankTransactionType == WITHDRAW;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BankTransaction that = (BankTransaction) o;

        return transactionId != null ? transactionId.equals(that.transactionId) : that.transactionId == null;

    }

    @Override
    public int hashCode() {
        return transactionId != null ? transactionId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "BankTransaction{" +
                "transactionId=" + transactionId +
                ", bankTransactionType=" + bankTransactionType +
                ", transactionAmount=" + transactionAmount +
                '}';
    }

    public static enum BankTransactionType {
        DEPOSIT, WITHDRAW
    }
}


