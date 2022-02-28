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
public class Monthly {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "monthly_Id")
    private int monthlyId;
    private String month;
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
