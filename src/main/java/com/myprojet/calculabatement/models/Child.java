package com.myprojet.calculabatement.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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

    @Size(min = 3, max = 20, message = "Last name must be between 3 and 20 characters")
    @Column(name = "child_lastname")
    private String lastname;

    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 characters")
    @Column(name = "child_firstname")
    private String firstname;

    @NotBlank(message = "The field Birthdate cannot be blank")
    @Column(name = "birth_Date")
    private String birthDate;

    @NotBlank(message = "The Field Begin contract cannot be blank")
    @Column(name = "begin_contract")
    private String beginContract;

    @Column(name = "end_contract")
    private String endContract;

    @Column(name = "fees_lunch")
    private double feesLunch;

    @Column(name = "fees_snack")
    private double feesSnack;

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

    public Child(int id, String lastname, String firstname, String birthDate, String beginContract, String endContract, double feesLunch, double feesSnack, LocalDateTime dateAdded, String imageUrl, String userEmail) {
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.birthDate = birthDate;
        this.beginContract = beginContract;
        this.endContract = endContract;
        this.feesLunch = feesLunch;
        this.feesSnack = feesSnack;
        this.dateAdded = dateAdded;
        this.imageUrl = imageUrl;
        this.userEmail = userEmail;
    }
}
