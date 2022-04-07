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
    private int taste;
    @Column(name = "day_worked")
    private int dayWorked;
    @Column(name = "hours_worked")
    private double hoursWorked;
    @Column(name = "child_id")
    private int childId;

}
