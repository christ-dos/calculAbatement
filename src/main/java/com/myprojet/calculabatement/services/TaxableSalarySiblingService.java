package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.repositories.MonthlyRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaxableSalarySiblingService {

    public double calculateTaxableSalarySibling(double netSalary, double netBrutCoefficient, double maintenanceCost){
        double csgRdsCoefficient = 0.029;
        double brutSalary = netSalary  / netBrutCoefficient;
        double basisCalculation = brutSalary * 0.9825;

        double TaxableSalary = (basisCalculation * csgRdsCoefficient) + netSalary + maintenanceCost;
        return Precision.round(TaxableSalary, 2);
    }
}
