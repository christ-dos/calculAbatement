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

import com.myprojet.calculabatement.models.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                new Monthly(1, Month.JANVIER, "2022", 650D, 20, 0, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 20, 0, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 20, 0, 20, 10.0, 1)
        );
        double foodCompensationExpected = (20 * 3 * 1) + (0 * 3 * 0.5); //60.0
        //WHEN
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2022", 1, 0.5, monthliesByYear, 1);
        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenLunchEqualZeroFound_thenReturnFoodCompensationWithOnlyTaste() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 650D, 0, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 0, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 0, 10, 20, 10.0, 1)
        );
        double foodCompensationExpected = (0 * 3 * 1) + (10 * 3 * 0.5); //15.0
        //WHEN
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2022", 1, 0.5, monthliesByYear, 1);
        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenNoLunchFoundAndNoTasteFound_thenReturnFoodCompensationEqualZero() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 650D, 0, 0, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 0, 0, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 0, 0, 20, 10.0, 1)
        );
        double foodCompensationExpected = (0 * 3 * 1) + (0 * 3 * 0.5); //0.00
        //WHEN
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2022", 1, 0.5, monthliesByYear, 1);
        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenFeesForLunchesEqualZeroButWeHaveSixtyLunches_thenThrowFeesEqualZeroException() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 20, 10, 20, 10.0, 1)
        );
        //WHEN
        assertThrows(FeesEqualZeroException.class, () ->
                calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2022", 0D, 1, monthliesByYear, 1));
    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenFeesForLunchesAndTasteEqualZeroButWeHaveLunchesAndTastes_thenThrowFeesEqualZeroException() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 20, 10, 20, 10.0, 1)
        );
        //WHEN
        //THEN
        assertThrows(FeesEqualZeroException.class, () ->
                calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2022", 0D, 0D, monthliesByYear, 1));
    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenFeesForLunchEqualZeroAndNoLunch_thenReturnFoodCompensationOnlyForTaste() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 650D, 0, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 0, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 0, 10, 20, 10.0, 1)
        );
        double foodCompensationExpected = (0 * 3 * 0) + (10 * 3 * 0.5); //15.0
        //WHEN
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2022", 0D, 0.5, monthliesByYear, 1);

        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenSixtyLunchesAndThirtyTastes_thenReturnFoodCompensationEqualThirty() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 20, 10, 20, 10.0, 1)
        );
        double foodCompensationExpected = (20 * 3 * 1) + (10 * 3 * 0.5); //75.0
        //WHEN
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2022", 1, 0.5, monthliesByYear, 1);

        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
    }

    @Test
    public void calculateFoodCompensationByYearAndByChildId_whenNoMonthliesFound_thenThrownMonthlyNotFoundException() {
        List<Monthly> monthliesByYear2022 = new ArrayList<>();
        //WHEN
        //THEN
        assertThrows(MonthlyNotFoundException.class, () ->
                calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChildId("2026", 1.00, 0.5, monthliesByYear2022, 1));
    }
}
