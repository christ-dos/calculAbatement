package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.NetBrutCoefficientNotNullException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TaxableSalarySiblingService {

    public double calculateTaxableSalarySibling(double netSalary, double netBrutCoefficient, double maintenanceCost) {
        double csgRdsCoefficient = 0.029;
        double brutSalary = netSalary / netBrutCoefficient;
        double basisCalculation = brutSalary * 0.9825;
        if (netBrutCoefficient == 0) {
            throw new NetBrutCoefficientNotNullException("Le coefficient de conversion de net en brut ne peut pas être equal à 0!");
        }
        double TaxableSalary = (basisCalculation * csgRdsCoefficient) + netSalary + maintenanceCost;
        return Precision.round(TaxableSalary, 2);
    }
}