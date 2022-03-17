package com.myprojet.calculabatement.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "child_lastname")
    private String lastname;
    @Column(name = "child_firstname")
    private String firstname;
    @Column(name = "birth_Date")
    private String birthDate;
    @Column(name = "begin_contract")
    private String beginContract;
    @Column(name = "user_email")
    private String userEmail;

}
