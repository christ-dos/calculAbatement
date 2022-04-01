package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.Child;

public interface TaxableSalaryService {
    double getSumTaxableSalaryByChildAndByYear(Child child, String year);

    double calculateTaxableSalarySiblingByMonth(double netSalary, double netBrutCoefficient, double maintenanceCost);
}
