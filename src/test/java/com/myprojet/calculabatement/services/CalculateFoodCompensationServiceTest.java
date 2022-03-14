package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.FeesEqualZeroException;
import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
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
public class CalculateFoodCompensationServiceTest {
    private CalculateFoodCompensationService calculateFoodCompensationServiceTest;
    @Mock
    private MonthlyRepository monthlyRepositoryMock;

    @BeforeEach
    public void setPerTest() {
        calculateFoodCompensationServiceTest = new CalculateFoodCompensationService(monthlyRepositoryMock);
    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenTasteEqualZero_thenReturnFoodCompensationWithOnlyLunch() {
        //GIVEN
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 20, 0, 20, 10.0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 0, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBER, "2022", 650D, 20, 0, 20, 10.0, 1)
        );
        double foodCompensationExpected = (20 * 3 * 1) + (0 * 3 * 0.5); //60.0
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2022", 1, 0.5, 1);
        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());

    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenLunchEqualZeroFound_thenReturnFoodCompensationWithOnlyTaste() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 0, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 0, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBER, "2022", 650D, 0, 10, 20, 10.0, 1)
        );
        double foodCompensationExpected = (0 * 3 * 1) + (10 * 3 * 0.5); //15.0
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2022", 1, 0.5, 1);
        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenNoLunchFoundAndNoTasteFound_thenReturnFoodCompensationEqualZero() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 0, 0, 20, 10.0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 0, 0, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBER, "2022", 650D, 0, 0, 20, 10.0, 1)
        );
        double foodCompensationExpected = (0 * 3 * 1) + (0 * 3 * 0.5); //0.00
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2022", 1, 0.5, 1);
        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenFeesForLunchesEqualZeroButWeHaveSixtyLunches_thenThrowFeesEqualZeroException() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBER, "2022", 650D, 20, 10, 20, 10.0, 1)
        );
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        //THEN
        assertThrows(FeesEqualZeroException.class, () ->
                calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2022", 0D, 1, 1));
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenFeesForLunchesAndTasteEqualZeroButWeHaveLunchesAndTastes_thenThrowFeesEqualZeroException() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBER, "2022", 650D, 20, 10, 20, 10.0, 1)
        );
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        //THEN
        assertThrows(FeesEqualZeroException.class, () ->
                calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2022", 0D, 0D, 1));
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenFeesForLunchEqualZeroAndNoLunch_thenReturnFoodCompensationOnlyForTaste() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 0, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 0, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBER, "2022", 650D, 0, 10, 20, 10.0, 1)
        );
        double foodCompensationExpected = (0 * 3 * 0) + (10 * 3 * 0.5); //15.0
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2022", 0D, 0.5, 1);

        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenSixtyLunchesAndThirtyTastes_thenReturnFoodCompensationEqualThirty() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBER, "2022", 650D, 20, 10, 20, 10.0, 1)
        );
        double foodCompensationExpected = (20 * 3 * 1) + (10 * 3 * 0.5); //75.0
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear);
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2022", 1, 0.5, 1);

        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());
    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenNoMonthliesFound_thenThrownMonthlyNotFoundException() {
        List<Monthly> monthliesByYear2022 = new ArrayList<>();
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYear(anyString())).thenReturn(monthliesByYear2022);
        //THEN
        assertThrows(MonthlyNotFoundException.class, () ->
                calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2026", 1.00, 0.5, 1));
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYear(anyString());

    }
}