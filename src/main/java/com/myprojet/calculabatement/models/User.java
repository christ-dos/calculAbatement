package com.myprojet.calculabatement.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class User {
    @Id
    @NotNull(message = "l'Email de l'utilisateur ne peut pas être vide")
    private String email;
    private String password;
    private String lastname;
    private String firstname;
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "user_email")
    private List<Child> children = new ArrayList<>();

    public User(String email, String password, String lastname, String firstname) {
        this.email = email;
        this.password = password;
        this.lastname = lastname;
        this.firstname = firstname;
    }
}
