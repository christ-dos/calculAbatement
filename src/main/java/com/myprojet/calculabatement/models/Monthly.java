package com.myprojet.calculabatement.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Override
    public String toString() {
        return "Monthly{" +
                "monthlyId=" + monthlyId +
                ", month=" + month +
                ", year='" + year + '\'' +
                ", taxableSalary=" + taxableSalary +
                ", lunch=" + lunch +
                ", taste=" + taste +
                ", dayWorked=" + dayWorked +
                ", hoursWorked=" + hoursWorked +
                ", childId=" + childId +
                '}';
    }
}
