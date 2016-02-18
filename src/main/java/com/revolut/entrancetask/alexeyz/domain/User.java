package com.revolut.entrancetask.alexeyz.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * User in the system. Is optinal entity for the entrance task, but is present to keep things straightforward
 *
 * @author alexey.zakharchenko@gmail.com
 */
@Entity
public class User {
    @Id
    @GeneratedValue
    private long id;

    @Size(min = 3)
    private String name;
    @Size(min = 3)
    private String login;
    @Size(min = 32)
    private String pwdHash;

    @NotNull
    @OneToOne
    private Account account;

    public User() {
    }


    public User(String name, String login, String pwdHash) {
        this.name = name;
        this.login = login;
        this.pwdHash = pwdHash;
    }

}
