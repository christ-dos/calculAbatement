package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.NetBrutCoefficientNotNullException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Monthly;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaxableSalaryServiceImpl implements TaxableSalaryService {
    private ChildService childService;

    public TaxableSalaryServiceImpl(ChildService childService) {
        this.childService = childService;
    }

    @Override
    public double getSumTaxableSalaryByChildAndByYear(String year, int childId) {
        Child child = childService.getChildById(childId);
        double sumTaxableSalaryByChildAndByYear = child.getMonthlies().stream()
                .filter(monthly -> monthly.getYear().equals(year))
                .mapToDouble(Monthly::getTaxableSalary).sum();

        log.info("Service: Calculate taxable salary by child ID: " + child.getId() + " for year: " + year + ", Value: " + sumTaxableSalaryByChildAndByYear);
        return sumTaxableSalaryByChildAndByYear;
    }

    @Override
    public double calculateTaxableSalarySiblingByMonth(double netSalary, double netBrutCoefficient, double maintenanceCost) {
        double csgRdsCoefficient = 0.029;
        double brutSalary = netSalary / netBrutCoefficient;
        double basisCalculation = brutSalary * 0.9825;
        if (netBrutCoefficient == 0) {
            log.error("The coefficient of conversion net/brut of the salary can not be null");
            throw new NetBrutCoefficientNotNullException("Le coefficient de conversion de net en brut ne peut pas être equal à 0!");
        }
        double TaxableSalary = (basisCalculation * csgRdsCoefficient) + netSalary + maintenanceCost;
        return Precision.round(TaxableSalary, 2);
    }
}
