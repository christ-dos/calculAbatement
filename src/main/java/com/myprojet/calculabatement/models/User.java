package com.myprojet.calculabatement.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "utilisateur")
public class User {
    @Id
    @NotNull(message = "l'Email de l'utilisateur ne peut pas être vide")
    private String email;
    private String password;
    private String nom;
    private String prenom;
    //private List<Enfant> enfants;

    @Override
    public String toString() {
        return "Utilisateur{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                '}';
    }
}
