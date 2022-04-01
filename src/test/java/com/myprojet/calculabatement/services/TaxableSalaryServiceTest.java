package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.NetBrutCoefficientNotNullException;
import org.apache.commons.math3.util.Precision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class TaxableSalaryServiceTest {

    private TaxableSalaryService taxableSalaryServiceTest;

    @BeforeEach
    public void setPerTest() {
        taxableSalaryServiceTest = new TaxableSalaryService();
    }

    @Test
    public void calculateTaxableSalarySibling_whenNetSalaryAndNetBrutCoefficientIsSuperiorAtZero_thenReturnTaxableSalary() {
        //GIVEN
        double netBrutCoefficient = 0.7801;
        double netSalary = 600;
        double brutSalary = netSalary / netBrutCoefficient;
        double maintenanceCost = 60;
        double basisCalculation = brutSalary * 0.9825;
        double taxableSalaryExpected = Precision.round(basisCalculation * 0.029 + netSalary + maintenanceCost, 2);//681.91
        //WHEN
        double taxableSalaryResult = taxableSalaryServiceTest.calculateTaxableSalarySiblingByMonth(netSalary, netBrutCoefficient, maintenanceCost);
        //THEN
        assertEquals(taxableSalaryExpected, taxableSalaryResult);
    }

    @Test
    public void calculateTaxableSalarySibling_whenNetSalaryAndMaintenanceCostIsEqualToZero_thenReturnTaxableSalaryEqualZero() {
        //GIVEN
        double netBrutCoefficient = 0.7801;
        double netSalary = 0;
        double brutSalary = netSalary / netBrutCoefficient;
        double maintenanceCost = 0;
        double basisCalculation = brutSalary * 0.9825;
        double taxableSalaryExpected = Precision.round(basisCalculation * 0.029 + netSalary + maintenanceCost, 2);//0.00
        //WHEN
        double taxableSalaryResult = taxableSalaryServiceTest.calculateTaxableSalarySiblingByMonth(netSalary, netBrutCoefficient, maintenanceCost);
        //THEN
        assertEquals(taxableSalaryExpected, taxableSalaryResult);
    }

    @Test
    public void calculateTaxableSalarySibling_whenNetBrutCoefficientIsEqualToZero_thenThrowNetBrutCoefficientNotNullException() {
        //GIVEN
        double netBrutCoefficient = 0;
        double netSalary = 600;
        double maintenanceCost = 60;
        //WHEN
        //THEN
        assertThrows(NetBrutCoefficientNotNullException.class, () ->
                taxableSalaryServiceTest.calculateTaxableSalarySiblingByMonth(netSalary, netBrutCoefficient, maintenanceCost));
    }
}
