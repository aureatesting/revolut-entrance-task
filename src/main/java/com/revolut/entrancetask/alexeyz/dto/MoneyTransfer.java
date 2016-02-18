package com.revolut.entrancetask.alexeyz.dto;

import com.revolut.entrancetask.alexeyz.domain.Currency;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author alexey.zakharchenko@gmail.com
 */
public class MoneyTransfer {
    /** Id of account FROM which money should be transferred */
    @Min(1)
    private long fromAccountId;
    /** Id of account TO which money should be transferred */
    @Min(1)
    private long toAccountId;
    /** Amount of this transfer */
    @Min(0)
    private BigDecimal amount;
    // The only supported currency for v.0.1 is USD
    @NotNull
    private Currency currency = Currency.USD;

    public MoneyTransfer() {
    }

    public MoneyTransfer(long fromAccountId, long toAccountId, double amount) {
        this(fromAccountId, toAccountId, new BigDecimal(amount));
    }

    public MoneyTransfer(long fromAccountId, long toAccountId, BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }


    public long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return Currency of this transfer. In v.0.1, is always Currency.USD
     */
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "[" + fromAccountId + " -> " + toAccountId + ": " + amount + " " + currency + "]";
    }
}
