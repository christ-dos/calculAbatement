package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import org.apache.commons.math3.util.Precision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class CalculateTaxReliefServiceTest {
    private CalculateTaxReliefService calculateTaxReliefServiceTest;
    @Mock
    private MonthlyRepository monthlyRepositoryMock;

    @BeforeEach
    public void setPerTest() {
        calculateTaxReliefServiceTest = new CalculateTaxReliefService(monthlyRepositoryMock);
    }

    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareOneTimeAndRateSmic2IsEqualZeroAndWithoutHoursWorked_thenReturnTaxReliefForCompleteYear() {
        //GIVEN
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 20, 20, 20, 0D, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 20, 20, 0D, 1),
                new Monthly(3, Month.MARCH, "2022", 650D, 20, 20, 20, 0D, 1)
        );
        double calculateTaxRelief = Precision.round(20 * 3 * (10D * 3), 2); //1800.0
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        Double taxReliefResult = calculateTaxReliefServiceTest.calculateTaxReliefByChild(10D, 0.00, Month.FEBRUARY, "2022", 1);
        //THEN
        assertEquals(calculateTaxRelief, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareOneTimeAndRateSmic2IsEqualZeroWithoutDaysWorked_thenReturnTaxReliefForCompleteYear() {
        //GIVEN
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 20, 20, 0, 50.0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 20, 0, 50.0, 1),
                new Monthly(3, Month.MARCH, "2022", 650D, 20, 20, 0, 50.0, 1)
        );
        double calculateTaxRelief = Precision.round((0 + (Math.ceil(50D * 3 / 8))) * (10D * 3), 2);//570.0
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        Double taxReliefResult = calculateTaxReliefServiceTest.calculateTaxReliefByChild(10D, 0.00, Month.FEBRUARY, "2022", 1);
        //THEN
        assertEquals(calculateTaxRelief, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareOneTimeAndRateSmic2IsEqualZero_thenReturnTaxReliefForCompleteYear() {
        //GIVEN
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(3, Month.MARCH, "2022", 650D, 20, 20, 20, 10.0, 1)
        );
        double calculateTaxRelief = Precision.round((20 * 3 + (Math.ceil(10D * 3 / 8))) * (10D * 3), 2);//1920.0
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        Double taxReliefResult = calculateTaxReliefServiceTest.calculateTaxReliefByChild(10D, 0.00, Month.FEBRUARY, "2022", 1);
        //THEN
        assertEquals(calculateTaxRelief, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareTwoTimesAndWithoutHoursWorked_thenReturnTaxReliefCalculatedForTwoDifferentPeriods() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 20, 20, 20, 0D, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 20, 20, 0D, 1),
                new Monthly(3, Month.JUNE, "2022", 650D, 20, 20, 20, 0D, 1)
        );
        double calculateTaxReliefForTwoPeriod = Precision.round(20 * 2 * (10D * 3) + (20 * (10.10 * 3)), 2);//1806
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        Double taxReliefResult = calculateTaxReliefServiceTest.calculateTaxReliefByChild(10D, 10.10, Month.JUNE, "2022", 1);
        //THEN
        assertEquals(calculateTaxReliefForTwoPeriod, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareTwoTimesAndWithDaysWorked_thenReturnTaxReliefCalculatedForTwoDifferentPeriods() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 20, 20, 0, 50.0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 20, 0, 50.0, 1),
                new Monthly(3, Month.JUNE, "2022", 650D, 20, 20, 0, 50.0, 1)
        );
        double calculateTaxReliefForTwoPeriod = Precision.round((0 * 2 + (Math.ceil(50D * 2 / 8))) * (10D * 3) + (0 + (Math.ceil(50D / 8))) * (10.10 * 3), 2);//602.10
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        Double taxReliefResult = calculateTaxReliefServiceTest.calculateTaxReliefByChild(10D, 10.10, Month.JUNE, "2022", 1);
        //THEN
        assertEquals(calculateTaxReliefForTwoPeriod, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }


    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareTwoTimesAndWithHoursWorked_thenReturnTaxReliefCalculatedForTwoDifferentPeriods() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(3, Month.JUNE, "2022", 650D, 20, 20, 20, 10.0, 1)
        );
        double calculateTaxReliefForTwoPeriod = Precision.round((20 * 2 + (Math.ceil(10D * 2 / 8))) * (10D * 3) + (20 + (Math.ceil(10D / 8))) * (10.10 * 3), 2);//1956.60
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        Double taxReliefResult = calculateTaxReliefServiceTest.calculateTaxReliefByChild(10D, 10.10, Month.JUNE, "2022", 1);
        //THEN
        assertEquals(calculateTaxReliefForTwoPeriod, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareTwoTimesAndWithHoursWorkedAndUpWareInJanuary_thenReturnTaxReliefCalculatedForTwoDifferentPeriods() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBER, "2022", 650D, 20, 20, 20, 10.0, 1)
        );
        //  0 car il n'y a rien avant Janvier
        double calculateTaxReliefForTwoPeriod = Precision.round(0 + (20 * 3 + (Math.ceil(10D * 3 / 8))) * (10.10 * 3), 2);//1939.200
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        Double taxReliefResult = calculateTaxReliefServiceTest.calculateTaxReliefByChild(10D, 10.10, Month.JANUARY, "2022", 1);
        //THEN
        assertEquals(calculateTaxReliefForTwoPeriod, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareTwoTimesAndWithHoursWorkedAndUpWareInDecember_thenReturnTaxReliefCalculatedForTwoDifferentPeriods() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBER, "2022", 650D, 20, 20, 20, 10.0, 1)
        );
        //  0 car il n'y a rien avant Janvier
        double calculateTaxReliefForTwoPeriod = Precision.round((20 * 2 + (Math.ceil(10D * 2 / 8))) * (10D * 3) + (20 + (Math.ceil(10D / 8))) * (10.10 * 3), 2);//1956.6
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        Double taxReliefResult = calculateTaxReliefServiceTest.calculateTaxReliefByChild(10D, 10.10, Month.DECEMBER, "2022", 1);
        //THEN
        assertEquals(calculateTaxReliefForTwoPeriod, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }


    @Test
    public void calculateTaxReliefByChildTest_whenNoEntryWasFoundForYear2022_thenThrowMonthlyNotFoundException() {
        //GIVEN
        List<Monthly> monthliesByYearEmpty = new ArrayList<>();
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYearEmpty);
        //THEN
        assertThrows(MonthlyNotFoundException.class, () -> calculateTaxReliefServiceTest.calculateTaxReliefByChild(10.15, 0.00, Month.FEBRUARY, "2026", 1));
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }


}
