package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.models.RateSmicApi;
import com.myprojet.calculabatement.proxies.RateSmicProxy;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import org.apache.commons.math3.util.Precision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


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
public class CalculateTaxReliefServiceImplTest {
    private CalculateTaxReliefServiceImpl calculateTaxReliefServiceImplTest;
    @Mock
    private MonthlyRepository monthlyRepositoryMock;

    @Mock
    private RateSmicProxy rateSmicProxyMock;

    @BeforeEach
    public void setPerTest() {
        calculateTaxReliefServiceImplTest = new CalculateTaxReliefServiceImpl(monthlyRepositoryMock, rateSmicProxyMock);
    }

    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareOneTimeAndRateSmic2IsEqualZeroAndWithoutHoursWorked_thenReturnTaxReliefForCompleteYear() {
        //GIVEN
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2021", 650D, 20, 20, 20, 0D, 1),
                new Monthly(2, Month.FEVRIER, "2021", 650D, 20, 20, 20, 0D, 1),
                new Monthly(3, Month.MARS, "2021", 650D, 20, 20, 20, 0D, 1)
        );
        List<RateSmicApi> rateSmicApiList = Arrays.asList(
                new RateSmicApi("2021-07", "10.00"),
                new RateSmicApi("2021-06", "10.00"),
                new RateSmicApi("2021-02", "10.00"),
                new RateSmicApi("2021-01", "10.00")
        );
        double calculateTaxRelief = Precision.round(20 * 3 * (10D * 3), 2); //1800.0
        //WHEN
        when(rateSmicProxyMock.getRateSmicByInseeApi(anyString(), anyString())).thenReturn(rateSmicApiList);
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        Double taxReliefResult = calculateTaxReliefServiceImplTest.calculateTaxReliefByChild("2021", 1);
        //THEN
        assertEquals(calculateTaxRelief, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareOneTimeAndRateSmic2IsEqualZeroWithoutDaysWorked_thenReturnTaxReliefForCompleteYear() {
        //GIVEN
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2021", 650D, 20, 20, 0, 50.0, 1),
                new Monthly(2, Month.FEVRIER, "2021", 650D, 20, 20, 0, 50.0, 1),
                new Monthly(3, Month.MARS, "2021", 650D, 20, 20, 0, 50.0, 1)
        );
        List<RateSmicApi> rateSmicApiList = Arrays.asList(
                new RateSmicApi("2021-07", "10.00"),
                new RateSmicApi("2021-06", "10.00"),
                new RateSmicApi("2021-02", "10.00"),
                new RateSmicApi("2021-01", "10.00")
        );
        double calculateTaxRelief = 0 + (50D * 3 / 8) * (10D * 3);//562.5
        //WHEN
        when(rateSmicProxyMock.getRateSmicByInseeApi(anyString(), anyString())).thenReturn(rateSmicApiList);
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        Double taxReliefResult = calculateTaxReliefServiceImplTest.calculateTaxReliefByChild("2021", 1);
        //THEN
        assertEquals(calculateTaxRelief, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareOneTimeAndRateSmic2IsEqualZero_thenReturnTaxReliefForCompleteYear() {
        //GIVEN
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2021", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2021", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(3, Month.MARS, "2021", 650D, 20, 20, 20, 10.0, 1)
        );
        List<RateSmicApi> rateSmicApiList = Arrays.asList(
                new RateSmicApi("2021-07", "10.00"),
                new RateSmicApi("2021-06", "10.00"),
                new RateSmicApi("2021-02", "10.00"),
                new RateSmicApi("2021-01", "10.00")
        );
        double calculateTaxRelief = (20 * 3 + 10D * 3 / 8) * (10D * 3);//1912.50
        //WHEN
        when(rateSmicProxyMock.getRateSmicByInseeApi(anyString(), anyString())).thenReturn(rateSmicApiList);
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        Double taxReliefResult = calculateTaxReliefServiceImplTest.calculateTaxReliefByChild("2021", 1);
        //THEN
        assertEquals(calculateTaxRelief, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareTwoTimesAndWithoutHoursWorked_thenReturnTaxReliefCalculatedForTwoDifferentPeriods() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2021", 650D, 20, 20, 20, 0D, 1),
                new Monthly(2, Month.FEVRIER, "2021", 650D, 20, 20, 20, 0D, 1),
                new Monthly(3, Month.JUIN, "2021", 650D, 20, 20, 20, 0D, 1)
        );
        List<RateSmicApi> rateSmicApiList = Arrays.asList(
                new RateSmicApi("2021-07", "10.48"),
                new RateSmicApi("2021-06", "10.48"),
                new RateSmicApi("2021-02", "10.25")
        );
        double calculateTaxReliefForTwoPeriod = 20 * 2 * (10.25 * 3) + (20 * (10.48 * 3));//1845
        //WHEN
        when(rateSmicProxyMock.getRateSmicByInseeApi(anyString(), anyString())).thenReturn(rateSmicApiList);
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        Double taxReliefResult = calculateTaxReliefServiceImplTest.calculateTaxReliefByChild("2021", 1);
        //THEN
        assertEquals(calculateTaxReliefForTwoPeriod, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareTwoTimesAndWithDaysWorked_thenReturnTaxReliefCalculatedForTwoDifferentPeriods() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2021", 650D, 20, 20, 0, 50.0, 1),
                new Monthly(2, Month.FEVRIER, "2021", 650D, 20, 20, 0, 50.0, 1),
                new Monthly(3, Month.JUIN, "2021", 650D, 20, 20, 0, 50.0, 1)
        );
        List<RateSmicApi> rateSmicApiList = Arrays.asList(
                new RateSmicApi("2021-06", "10.48"),
                new RateSmicApi("2021-01", "10.25")
        );
        double calculateTaxReliefForTwoPeriod = (0 * 2 + 50D * 2 / 8) * (10.25 * 3) + (0 + 50D / 8) * (10.48 * 3);//580.875
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        when(rateSmicProxyMock.getRateSmicByInseeApi(anyString(), anyString())).thenReturn(rateSmicApiList);
        Double taxReliefResult = calculateTaxReliefServiceImplTest.calculateTaxReliefByChild("2021", 1);
        //THEN
        assertEquals(calculateTaxReliefForTwoPeriod, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }


    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareTwoTimesAndWithHoursWorked_thenReturnTaxReliefCalculatedForTwoDifferentPeriods() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2021", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2021", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(3, Month.JUIN, "2021", 650D, 20, 20, 20, 10.0, 1)
        );
        List<RateSmicApi> rateSmicApiList = Arrays.asList(
                new RateSmicApi("2021-07", "10.48"),
                new RateSmicApi("2021-06", "10.48"),
                new RateSmicApi("2021-02", "10.25"),
                new RateSmicApi("2021-01", "10.25")
        );
        double calculateTaxReliefForTwoPeriod = (20 * 2 + 10D * 2 / 8) * (10.25 * 3) + (20 + 10D / 8) * (10.48 * 3);//1974.975
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        when(rateSmicProxyMock.getRateSmicByInseeApi(anyString(), anyString())).thenReturn(rateSmicApiList);
        Double taxReliefResult = calculateTaxReliefServiceImplTest.calculateTaxReliefByChild("2021", 1);
        //THEN
        assertEquals(calculateTaxReliefForTwoPeriod, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareTwoTimesAndWithHoursWorkedAndUpWareInJune_thenReturnTaxReliefCalculatedForTwoDifferentPeriods() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2021", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2021", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2021", 650D, 20, 20, 20, 10.0, 1)
        );
        List<RateSmicApi> rateSmicApiList = Arrays.asList(
                new RateSmicApi("2021-07", "10.48"),
                new RateSmicApi("2021-06", "10.48"),
                new RateSmicApi("2021-02", "10.25"),
                new RateSmicApi("2021-01", "10.25")
        );
        //  0 car il n'y a rien avant Janvier
        double calculateTaxReliefForTwoPeriod = (20 * 2 + 10.00 * 2 / 8) * (10.25 * 3)  + (20 * 1 + 10.00 * 1 / 8) * (10.48 * 3);//1974.975
        //WHEN
        when(rateSmicProxyMock.getRateSmicByInseeApi(anyString(), anyString())).thenReturn(rateSmicApiList);
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        Double taxReliefResult = calculateTaxReliefServiceImplTest.calculateTaxReliefByChild("2021", 1);
        //THEN
        assertEquals(calculateTaxReliefForTwoPeriod, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateTaxReliefByChildTest_whenRateSmicUpWareTwoTimesAndWithHoursWorkedAndUpWareInDecember_thenReturnTaxReliefCalculatedForTwoDifferentPeriods() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2021", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2021", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2021", 650D, 20, 20, 20, 10.0, 1)
        );
        List<RateSmicApi> rateSmicApiList = Arrays.asList(
                new RateSmicApi("2021-07", "10.48"),
                new RateSmicApi("2021-06", "10.48"),
                new RateSmicApi("2021-02", "10.25"),
                new RateSmicApi("2021-01", "10.25")
        );
        //  0 car il n'y a rien avant Janvier
        double calculateTaxReliefForTwoPeriod = (20 * 2 + 10.00 * 2 / 8) * (10.25 * 3) + (20 + 10.00 / 8) * (10.48 * 3);//1974.975
        //WHEN
        when(rateSmicProxyMock.getRateSmicByInseeApi(anyString(), anyString())).thenReturn(rateSmicApiList);
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        Double taxReliefResult = calculateTaxReliefServiceImplTest.calculateTaxReliefByChild("2021", 1);
        //THEN
        assertEquals(calculateTaxReliefForTwoPeriod, taxReliefResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateTaxReliefByChildTest_whenNoEntryWasFoundForYear2022_thenThrowMonthlyNotFoundException() {
        //GIVEN
        List<Monthly> monthliesByYearEmpty = new ArrayList<>();
        List<RateSmicApi> rateSmicApiList = Arrays.asList(
                new RateSmicApi("2021-07", "10.00"),
                new RateSmicApi("2021-06", "10.00"),
                new RateSmicApi("2021-02", "10.00"),
                new RateSmicApi("2021-01", "10.00")
        );
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYearEmpty);
       // when(rateSmicProxyMock.getRateSmicByInseeApi(anyString(), anyString())).thenReturn(rateSmicApiList);
        //THEN
        assertThrows(MonthlyNotFoundException.class, () -> calculateTaxReliefServiceImplTest.calculateTaxReliefByChild("2026", 1));
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }
}
