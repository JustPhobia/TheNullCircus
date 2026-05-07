package com.thenullcircus.model;

import lombok.Data;
import java.util.UUID;

@Data
public class User {

    private UUID userId;
    private String name;
    private String surname;
    private String email;
    private Gender gender;
    private String username;
    private String password;
    private Boolean clown;
    private Boolean ringleader;

    public User(){}

    public User(UUID userId,String name, String surname, String email, Gender gender, String username, String password, Boolean clown, Boolean ringleader) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.gender = gender;
        this.username = username;
        this.password = password;
        this.clown = clown;
        this.ringleader = ringleader;
    }

    public User(String name, String surname, String email, Gender gender, String username, String password, Boolean clown, Boolean ringleader) {
        this.userId = UUID.randomUUID();
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.gender = gender;
        this.username = username;
        this.password = password;
        this.clown = clown;
        this.ringleader = ringleader;
    }
}
