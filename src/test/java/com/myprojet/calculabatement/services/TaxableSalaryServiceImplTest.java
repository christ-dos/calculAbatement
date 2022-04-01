package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.NetBrutCoefficientNotNullException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Monthly;
import org.apache.commons.math3.util.Precision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Month;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaxableSalaryServiceImplTest {

    private TaxableSalaryServiceImpl taxableSalaryServiceImplTest;
    @Mock
    private ChildServiceImpl childServiceImplMock;

    @BeforeEach
    public void setPerTest() {
        taxableSalaryServiceImplTest = new TaxableSalaryServiceImpl(childServiceImplMock);
    }

    @Test
    public void getSumTaxableSalaryByChildAndByYear_whenChildHasTreeMonthlyDate2022_thenReturnSumEquals1500() {
        //GIVEN
        Child child =
                new Child(1, "Riboulet", "Romy", "12/01/2019", "02/05/2021", "https://www.hdwallpaper.nu.jpg", "christine@email.fr",
                        Arrays.asList(
                                new Monthly(1, Month.JANUARY, "2022", 500D, 10, 10, 20, 0.0, 1),
                                new Monthly(2, Month.FEBRUARY, "2022", 500D, 10, 10, 20, 0.0, 1),
                                new Monthly(3, Month.MARCH, "2022", 500D, 10, 10, 20, 0.0, 2)
                        )
                );
        //WHEN
        when(childServiceImplMock.getChildById(anyInt())).thenReturn(child);
        double sumTaxableSalaryResult = taxableSalaryServiceImplTest.getSumTaxableSalaryByChildAndByYear("2022", 1);
        //THEN
        assertEquals(1500D, sumTaxableSalaryResult);
    }

    @Test
    public void getSumTaxableSalaryByChildAndByYear_whenJanuaryDate2021_thenReturnSumEquals1000() {
        //GIVEN
        Child child =
                new Child(1, "Riboulet", "Romy", "12/01/2019", "02/05/2021", "https://www.hdwallpaper.nu.jpg", "christine@email.fr",
                        Arrays.asList(
                                new Monthly(1, Month.JANUARY, "2021", 500D, 10, 10, 20, 0.0, 1),
                                new Monthly(2, Month.FEBRUARY, "2022", 500D, 10, 10, 20, 0.0, 1),
                                new Monthly(3, Month.MARCH, "2022", 500D, 10, 10, 20, 0.0, 2)
                        )
                );
        //WHEN
        when(childServiceImplMock.getChildById(anyInt())).thenReturn(child);
        double sumTaxableSalaryResult = taxableSalaryServiceImplTest.getSumTaxableSalaryByChildAndByYear("2022",1 );
        //THEN
        assertEquals(1000D, sumTaxableSalaryResult);
    }

    @Test
    public void getSumTaxableSalaryByChildAndByYear_whenNoMonthlyDate2022_thenReturnSumEqualsZero() {
        //GIVEN
        Child child =
                new Child(1, "Riboulet", "Romy", "12/01/2019", "02/05/2021", "https://www.hdwallpaper.nu.jpg", "christine@email.fr",
                        Arrays.asList(
                                new Monthly(1, Month.JANUARY, "2021", 500D, 10, 10, 20, 0.0, 1),
                                new Monthly(2, Month.FEBRUARY, "2021", 500D, 10, 10, 20, 0.0, 1),
                                new Monthly(3, Month.MARCH, "2021", 500D, 10, 10, 20, 0.0, 2)
                        )
                );
        //WHEN
        when(childServiceImplMock.getChildById(anyInt())).thenReturn(child);
        double sumTaxableSalaryResult = taxableSalaryServiceImplTest.getSumTaxableSalaryByChildAndByYear("2022", 1);
        //THEN
        assertEquals(0D, sumTaxableSalaryResult);
    }

    @Test
    public void getSumTaxableSalaryByChildAndByYear_whenChildHasNoMonthly_thenReturnZero() {
        //GIVEN
        Child child =
                new Child(1, "Riboulet", "Romy", "12/01/2019", "02/05/2021", "https://www.hdwallpaper.nu.jpg", "christine@email.fr",
                        Arrays.asList()
                );
        //WHEN
        when(childServiceImplMock.getChildById(anyInt())).thenReturn(child);
        double sumTaxableSalaryResult = taxableSalaryServiceImplTest.getSumTaxableSalaryByChildAndByYear("2022", 1);
        //THEN
        assertEquals(0D, sumTaxableSalaryResult);
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
        double taxableSalaryResult = taxableSalaryServiceImplTest.calculateTaxableSalarySiblingByMonth(netSalary, netBrutCoefficient, maintenanceCost);
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
        double taxableSalaryResult = taxableSalaryServiceImplTest.calculateTaxableSalarySiblingByMonth(netSalary, netBrutCoefficient, maintenanceCost);
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
                taxableSalaryServiceImplTest.calculateTaxableSalarySiblingByMonth(netSalary, netBrutCoefficient, maintenanceCost));
    }
}
