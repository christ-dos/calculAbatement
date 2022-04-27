package com.myprojet.calculabatement.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Monthly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "monthly_Id")
    private int monthlyId;
    @Enumerated(EnumType.STRING)
    private Month month;
    private String year;
    @Column(name = "taxable_salary")
    private Double taxableSalary;
    private int lunch;
    private int snack;
    @Column(name = "day_worked")
    private int dayWorked;
    @Column(name = "hours_worked")
    private double hoursWorked;
    @Column(name = "child_id")
    private int childId;

//    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    @JoinColumn(name = "id")
//    private Child child;

//    public Monthly(int monthlyId, Month month, String year, Double taxableSalary, int lunch, int taste, int dayWorked, double hoursWorked, int childId) {
//        this.monthlyId = monthlyId;
//        this.month = month;
//        this.year = year;
//        this.taxableSalary = taxableSalary;
//        this.lunch = lunch;
//        this.taste = taste;
//        this.dayWorked = dayWorked;
//        this.hoursWorked = hoursWorked;
//        this.childId = childId;
//    } // todo clean code
}
