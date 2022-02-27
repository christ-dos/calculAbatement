package com.myprojet.calculabatement.models;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {
    @Id
    @NotNull(message = "l'Email de l'utilisateur ne peut pas être vide")
    private String email;
    private String password;
    private String nom;
    private String prenom;
    //private List<Enfant> enfants;
}
