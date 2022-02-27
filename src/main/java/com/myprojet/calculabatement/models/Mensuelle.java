package com.myprojet.calculabatement.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mensuelle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mensuelleId;
    private String mois;
    private String annee;
    @Column(name = "salaire_imposable")
    private Double salaireImposable;
    private int repas;
    private int gouter;
    @Column(name = "jour_travaille")
    private int jourTravaille ;
    @Column(name = "heure_travaille")
    private double heureTravaille;
    @Column(name = "enfant_id")
    private int enfantId;

}
