package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.FeesEqualZeroException;
import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
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

@ExtendWith(MockitoExtension.class)
public class CalculateFoodCompensationServiceTest {
    private CalculateFoodCompensationService calculateFoodCompensationServiceTest;
    @Mock
    private MonthlyRepository monthlyRepositoryMock;

    private Child child;

    @BeforeEach
    public void setPerTest() {
        calculateFoodCompensationServiceTest = new CalculateFoodCompensationService(monthlyRepositoryMock);
        child = new Child(1, "Benoit", "George",
                "20/05/2020", "01/09/2020", null, 1.00, 0.50, null, null, "chistine@email.fr", null);

    }

    @Test
    public void calculateFoodCompensationByYearAndByChild_whenTasteEqualZero_thenReturnFoodCompensationWithOnlyLunch() {
        //GIVEN
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 650D, 20, 0, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 20, 0, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 20, 0, 20, 10.0, 1)
        );
        double foodCompensationExpected = (20 * 3 * 1) + (0 * 3 * 0.5); //60.0
        //WHEN
        child.setMonthlies(monthliesByYear);
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChild("2022", monthliesByYear, 1D, 0.5);
        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
    }

    @Test
    public void calculateFoodCompensationByYearAndByChild_whenLunchEqualZeroFound_thenReturnFoodCompensationWithOnlyTaste() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 650D, 0, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 0, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 0, 10, 20, 10.0, 1)
        );
        double foodCompensationExpected = (0 * 3 * 1) + (10 * 3 * 0.5); //15.0
        //WHEN
        child.setMonthlies(monthliesByYear);
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChild("2022", monthliesByYear, child.getFeesLunch(), child.getFeesSnacks());
        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
    }

    @Test
    public void calculateFoodCompensationByYearAndByChild_whenNoLunchFoundAndNoTasteFound_thenReturnFoodCompensationEqualZero() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 650D, 0, 0, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 0, 0, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 0, 0, 20, 10.0, 1)
        );
        double foodCompensationExpected = (0 * 3 * 1) + (0 * 3 * 0.5); //0.00
        //WHEN
        child.setMonthlies(monthliesByYear);
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChild("2022", monthliesByYear, child.getFeesLunch(), child.getFeesSnacks());
        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
    }

    @Test
    public void calculateFoodCompensationByYearAndByChild_whenFeesForLunchesEqualZeroButWeHaveSixtyLunches_thenThrowFeesEqualZeroException() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 20, 10, 20, 10.0, 1)
        );
        //WHEN
        child.setFeesLunch(0D);
        assertThrows(FeesEqualZeroException.class, () ->
                calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChild("2022", monthliesByYear, child.getFeesLunch(), child.getFeesLunch()));
    }

    @Test
    public void calculateFoodCompensationByYearAndByChild_whenFeesForLunchesAndTasteEqualZeroButWeHaveLunchesAndTastes_thenThrowFeesEqualZeroException() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 20, 10, 20, 10.0, 1)
        );
        //WHEN
        child.setFeesLunch(0D);
        child.setFeesSnacks(0D);
        //THEN
        assertThrows(FeesEqualZeroException.class, () ->
                calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChild("2022", monthliesByYear, child.getFeesLunch(), child.getFeesLunch()));
    }

    @Test
    public void calculateFoodCompensationByYearAndByChild_whenFeesForLunchEqualZeroAndNoLunch_thenReturnFoodCompensationOnlyForTaste() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 650D, 0, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 0, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 0, 10, 20, 10.0, 1)
        );
        double foodCompensationExpected = (0 * 3 * 0) + (10 * 3 * 0.5); //15.0
        //WHEN
        child.setMonthlies(monthliesByYear);
        child.setFeesLunch(0D);
        child.setFeesSnacks(0.5);
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChild("2022", monthliesByYear, child.getFeesLunch(), child.getFeesSnacks());

        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
    }

    @Test
    public void calculateFoodCompensationByYearAndByChild_whenSixtyLunchesAndThirtyTastes_thenReturnFoodCompensationEqualThirty() {
        List<Monthly> monthliesByYear = Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 20, 10, 20, 10.0, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 20, 10, 20, 10.0, 1)
        );
        double foodCompensationExpected = (20 * 3 * 1) + (10 * 3 * 0.5); //75.0
        //WHEN
        child.setMonthlies(monthliesByYear);
        double foodCompensationResult = calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChild("2022", monthliesByYear, child.getFeesLunch(), child.getFeesSnacks());

        //THEN
        assertEquals(foodCompensationExpected, foodCompensationResult);
    }

    @Test
    public void calculateFoodCompensationByYearAndByChild_whenNoMonthliesFound_thenThrownMonthlyNotFoundException() {
        List<Monthly> monthliesByYear3026 = new ArrayList<>();
        //WHEN
        child.setMonthlies(monthliesByYear3026);
        //THEN
        assertThrows(MonthlyNotFoundException.class, () ->
                calculateFoodCompensationServiceTest.calculateFoodCompensationByYearAndByChild("2022", monthliesByYear3026, child.getFeesLunch(), child.getFeesSnacks()));
    }
}
