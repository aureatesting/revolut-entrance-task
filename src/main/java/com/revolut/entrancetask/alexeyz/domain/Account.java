package com.revolut.entrancetask.alexeyz.domain;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Account of a User
 *
 * @author alexey.zakharchenko@gmail.com
 */
@Entity
public class Account {
    @Id
    @GeneratedValue
    private long id;

    @NotNull
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private User user;

    @Min(0)
    private BigDecimal amount;

    // For the entrance project simplicity, currency is always USD
    private Currency currency = Currency.USD;

    public long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void add(BigDecimal num) {
        this.amount = this.amount.add(num);
    }

    public Currency getCurrency() {
        return currency;
    }
}
