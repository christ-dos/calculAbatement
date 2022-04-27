package com.myprojet.calculabatement.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
    @Column(name = "end_contract")
    private String endContract;
    @Column(name = "fees_lunch")
    private double feesLunch;
    @Column(name = "fees_snack")
    private double feesSnacks;
    @Column(name = "date_added")
    private LocalDateTime dateAdded;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "user_email")
    private String userEmail;
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "child_id")
    private List<Monthly> monthlies = new ArrayList<>();

    public Child(int id, String lastname, String firstname, String birthDate, String beginContract, LocalDateTime dateAdded, String imageUrl, String userEmail) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.birthDate = birthDate;
        this.beginContract = beginContract;
        this.dateAdded = dateAdded;
        this.imageUrl = imageUrl;
        this.userEmail = userEmail;
    }

    public Child(int id, String lastname, String firstname, String birthDate, String beginContract, String imageUrl, String userEmail) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.birthDate = birthDate;
        this.beginContract = beginContract;
        this.imageUrl = imageUrl;
        this.userEmail = userEmail;
    }

    public Child(int id, String lastname, String firstname, String birthDate, String beginContract, String imageUrl, String userEmail, List<Monthly> monthlies) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.birthDate = birthDate;
        this.beginContract = beginContract;
        this.imageUrl = imageUrl;
        this.userEmail = userEmail;
        this.monthlies = monthlies;
    }

    public Child(int id, String lastname, String firstname, String birthDate, String beginContract, String endContract, double feesLunch, double feesSnacks, LocalDateTime dateAdded, String imageUrl, String userEmail) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.birthDate = birthDate;
        this.beginContract = beginContract;
        this.endContract = endContract;
        this.feesLunch = feesLunch;
        this.feesSnacks = feesSnacks;
        this.dateAdded = dateAdded;
        this.imageUrl = imageUrl;
        this.userEmail = userEmail;
    }
}
