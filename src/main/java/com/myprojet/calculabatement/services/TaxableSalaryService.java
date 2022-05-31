package com.myprojet.calculabatement.services;

public interface TaxableSalaryService {
    double getSumTaxableSalaryByChildAndByYear(String year, int childId);

    double calculateTaxableSalarySiblingByMonth(double netSalary, double netBrutCoefficient, double maintenanceCost);
}
