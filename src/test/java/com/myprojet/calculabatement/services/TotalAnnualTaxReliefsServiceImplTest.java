package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TotalAnnualTaxReliefsServiceImplTest {
    private TotalAnnualTaxReliefsServiceImpl totalAnnualTaxReliefsServiceImplTest;
    @Mock
    private ChildServiceImpl childServiceMock;
    @Mock
    private CalculateTaxReliefService calculateTaxReliefServiceMock;
    @Mock
    private CalculateFoodCompensationService calculateFoodCompensationServiceMock;
    @Mock
    private TaxableSalaryServiceImpl taxableSalaryServiceImplMock;

    List<Child> childrenByCurrentUser = new ArrayList<>();

    @BeforeEach
    public void setPerTest() {
        totalAnnualTaxReliefsServiceImplTest = new TotalAnnualTaxReliefsServiceImpl(childServiceMock,
                calculateTaxReliefServiceMock, calculateFoodCompensationServiceMock, taxableSalaryServiceImplMock);
    }

    @Test
    public void getTotalAnnualReportableAmountsForAllChildrenTests_whenChildAgeGreaterThanOne_thenReturnTotalAnnualReportableAmountsEqual545() {
        //GIVEN
        childrenByCurrentUser = Arrays.asList(
                new Child(1, "Riboulet", "Romy", "12/01/2019", "02/05/2021", "https://www.hdwallpaper.nu.jpg", "christine@email.fr", Arrays.asList(
                        new Monthly(1, Month.JANVIER, "2022", 500D, 10, 10, 20, 0.0, 1),
                        new Monthly(2, Month.FEVRIER, "2022", 500D, 10, 10, 20, 0.0, 1)
                )),
                new Child(2, "Cacahuette", "Manon", "10/03/2019", "01/03/2019", "https://www.hdwallpaper.nu.jpg", "christine@email.fr", Arrays.asList(
                        new Monthly(3, Month.MARS, "2022", 500D, 10, 10, 20, 0.0, 2)
                ))
        );
        //WHEN
        when(calculateTaxReliefServiceMock.calculateTaxReliefByChild(anyString(), anyInt())).thenReturn(1000D, 500D);
        when(taxableSalaryServiceImplMock.getSumTaxableSalaryByChildAndByYear(anyString(), anyInt())).thenReturn(1000D, 500D);
        when(calculateFoodCompensationServiceMock.calculateFoodCompensationByYearAndByChild(anyString(), anyList(),anyDouble(), anyDouble())).thenReturn(30D, 15D);
        when(childServiceMock.getChildrenByUserEmailOrderByDateAddedDesc()).thenReturn(childrenByCurrentUser);
        double totalAnnualReportableAmountsResult = totalAnnualTaxReliefsServiceImplTest.getTotalAnnualReportableAmountsForAllChildren("2022");
        //THEN
        //totalTaxableSalary = 1500.0, totalFoodCompensation = 45.0, totalTaxRelief = 1500.0
        assertEquals(45D, totalAnnualReportableAmountsResult);
    }

    @Test
    public void getTotalAnnualReportableAmountsForAllChildrenTests_whenChildAgeIsEqualsToOne_thenReturnTotalAnnualReportableAmountsEqual515() {
        //GIVEN
        childrenByCurrentUser = Arrays.asList(
                new Child(1, "Riboulet", "Romy", "12/01/2021", "02/05/2021", "https://www.hdwallpaper.nu.jpg", "christine@email.fr", Arrays.asList(
                        new Monthly(1, Month.JANVIER, "2022", 500D, 10, 10, 20, 0.0, 1),
                        new Monthly(2, Month.FEVRIER, "2022", 500D, 10, 10, 20, 0.0, 1)
                )),
                new Child(2, "Cacahuette", "Manon", "10/03/2021", "01/03/2019", "https://www.hdwallpaper.nu.jpg", "christine@email.fr", Arrays.asList(
                        new Monthly(3, Month.MARS, "2022", 500D, 10, 10, 20, 0.0, 2)
                ))
        );
        //WHEN
        when(calculateTaxReliefServiceMock.calculateTaxReliefByChild(anyString(), anyInt())).thenReturn(1000D, 500D);
        when(taxableSalaryServiceImplMock.getSumTaxableSalaryByChildAndByYear(anyString(), anyInt())).thenReturn(1000D, 500D);
        when(calculateFoodCompensationServiceMock.calculateFoodCompensationByYearAndByChild(anyString(), anyList(),anyDouble(), anyDouble())).thenReturn(15D, 0D);
        when(childServiceMock.getChildrenByUserEmailOrderByDateAddedDesc()).thenReturn(childrenByCurrentUser);
        double totalAnnualReportableAmountsResult = totalAnnualTaxReliefsServiceImplTest.getTotalAnnualReportableAmountsForAllChildren("2022");
        //THEN
        //totalTaxableSalary = 1500.0, totalFoodCompensation = 15.0, totalTaxRelief = 1500.0
        assertEquals(15D, totalAnnualReportableAmountsResult);
    }

    @Test
    public void getTotalAnnualReportableAmountsForAllChildrenTests_whenChildIsLessThanOne_thenReturnTotalAnnualReportableAmountsEqual500() {
        //GIVEN
        childrenByCurrentUser = Arrays.asList(
                new Child(1, "Riboulet", "Romy", "22/03/2022", "02/05/2021", "https://www.hdwallpaper.nu.jpg", "christine@email.fr", Arrays.asList(
                        new Monthly(1, Month.JANVIER, "2022", 600D, 10, 10, 20, 0.0, 1),
                        new Monthly(2, Month.FEVRIER, "2022", 600D, 10, 10, 20, 0.0, 1)
                )),
                new Child(2, "Cacahuette", "Manon", "10/03/2022", "01/03/2019", "https://www.hdwallpaper.nu.jpg", "christine@email.fr", Arrays.asList(
                        new Monthly(3, Month.MARS, "2022", 600D, 10, 10, 20, 0.0, 2)
                ))
        );
        //WHEN
        when(calculateTaxReliefServiceMock.calculateTaxReliefByChild(anyString(), anyInt())).thenReturn(1000D, 500D);
        when(taxableSalaryServiceImplMock.getSumTaxableSalaryByChildAndByYear(anyString(), anyInt())).thenReturn(1200D, 600D);
        when(childServiceMock.getChildrenByUserEmailOrderByDateAddedDesc()).thenReturn(childrenByCurrentUser);
        double totalAnnualReportableAmountsResult = totalAnnualTaxReliefsServiceImplTest.getTotalAnnualReportableAmountsForAllChildren("2022");
        //THEN
        //totalTaxableSalary = 1800.0, totalFoodCompensation = 0.0, totalTaxRelief = 1500.0
        assertEquals(300D, totalAnnualReportableAmountsResult);
    }

    @Test
    public void getTotalAnnualReportableAmountsForAllChildrenTests_whenChildrenAreAgeDifferent_thenReturnTotalAnnualReportableAmountsEqual530() {
        //GIVEN
        childrenByCurrentUser = Arrays.asList(
                new Child(1, "Riboulet", "Romy", "12/01/2021", "02/05/2021", "https://www.hdwallpaper.nu.jpg", "christine@email.fr", Arrays.asList(
                        new Monthly(1, Month.JANVIER, "2022", 500D, 10, 10, 20, 0.0, 1),
                        new Monthly(2, Month.FEVRIER, "2022", 500D, 10, 10, 20, 0.0, 1)
                )),
                new Child(2, "Cacahuette", "Manon", "10/03/2019", "01/03/2019", "https://www.hdwallpaper.nu.jpg", "christine@email.fr", Arrays.asList(
                        new Monthly(3, Month.MARS, "2022", 500D, 10, 10, 20, 0.0, 2)
                ))
        );
        //WHEN
        when(calculateTaxReliefServiceMock.calculateTaxReliefByChild(anyString(), anyInt())).thenReturn(1000D, 500D);
        when(taxableSalaryServiceImplMock.getSumTaxableSalaryByChildAndByYear(anyString(), anyInt())).thenReturn(1000D, 500D);
        when(calculateFoodCompensationServiceMock.calculateFoodCompensationByYearAndByChild(anyString(), anyList(),anyDouble(), anyDouble())).thenReturn(15D, 15D);
        when(childServiceMock.getChildrenByUserEmailOrderByDateAddedDesc()).thenReturn(childrenByCurrentUser);
        double totalAnnualReportableAmountsResult = totalAnnualTaxReliefsServiceImplTest.getTotalAnnualReportableAmountsForAllChildren("2022");
        //THEN
        //totalTaxableSalary = 1500.0, totalFoodCompensation = 30.0, totalTaxRelief = 1500.0
        //one child is one year old and the other is over one year old
        assertEquals(30D, totalAnnualReportableAmountsResult);
    }

    @Test
    public void getTotalAnnualReportableAmountsForAllChildrenTests_whenTotalAnnualReportableAmountsIsLessThanZero_thenReturnTotalAnnualReportableAmountsEqualZero() {
        //GIVEN
        childrenByCurrentUser = Arrays.asList(
                new Child(1, "Riboulet", "Romy", "12/04/2021", "02/05/2021", "https://www.hdwallpaper.nu.jpg", "christine@email.fr", Arrays.asList(
                        new Monthly(1, Month.JANVIER, "2022", 200D, 10, 10, 20, 0.0, 1),
                        new Monthly(2, Month.FEVRIER, "2022", 200D, 10, 10, 20, 0.0, 1)
                )),
                new Child(2, "Cacahuette", "Manon", "10/03/2019", "01/03/2019", "https://www.hdwallpaper.nu.jpg", "christine@email.fr", Arrays.asList(
                        new Monthly(3, Month.MARS, "2022", 200D, 10, 10, 20, 0.0, 2)
                ))
        );
        //WHEN
        when(childServiceMock.getChildrenByUserEmailOrderByDateAddedDesc()).thenReturn(childrenByCurrentUser);
        when(calculateTaxReliefServiceMock.calculateTaxReliefByChild(anyString(), anyInt())).thenReturn(500D);
        when(calculateFoodCompensationServiceMock.calculateFoodCompensationByYearAndByChild(anyString(), anyList(),anyDouble(), anyDouble())).thenReturn(15D, 15D);
        double totalAnnualReportableAmountsResult = totalAnnualTaxReliefsServiceImplTest.getTotalAnnualReportableAmountsForAllChildren("2022");
        //THEN
        //totalTaxableSalary = 600.0, totalFoodCompensation = 30.0, totalTaxRelief = 1000.0
        //one child is one year old and the other is over one year old
        //when totalTaxRelief is greater the totalTaxableSalary and totalAnnualReportableAmountsResult is
        //negative return 0.0
        assertEquals(0D, totalAnnualReportableAmountsResult);
    }

    @Test
    public void getTotalAnnualReportableAmountsByChildTest_whenYearIs2022AndChildIdIsOne_thenReturnTotalAnnualReportableForChildIdOneEquals250D() {
        //GIVEN
        Child childRomy = new Child(1, "Riboulet", "Romy", "12/01/2021", "02/05/2021", "https://www.hdwallpaper.nu.jpg", "christine@email.fr", Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 200D, 10, 10, 20, 0.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 200D, 10, 10, 20, 0.0, 1)
        ));
        double taxRelief = 200D;
        double foodCompensation = 50D;
        double taxableSalary = 400D;
        //WHEN
        when(taxableSalaryServiceImplMock.getSumTaxableSalaryByChildAndByYear(anyString(), anyInt())).thenReturn(taxableSalary);
        when(calculateTaxReliefServiceMock.calculateTaxReliefByChild(anyString(), anyInt())).thenReturn(taxRelief);
        when(calculateFoodCompensationServiceMock.calculateFoodCompensationByYearAndByChild(anyString(), anyList(),anyDouble(), anyDouble())).thenReturn(foodCompensation);
        double totalAnnualReportableAmountsResult = totalAnnualTaxReliefsServiceImplTest.getTotalAnnualReportableAmountsByChild(childRomy, "2022");
        //THEN
        assertEquals(250D, totalAnnualReportableAmountsResult);
    }

    @Test
    public void getTotalAnnualReportableAmountsByChildTest_whenYearIs2022ReportableAmountsIsNegative_thenReturnTotalAnnualReportableEqualToZero() {
        //GIVEN
        Child childRomy = new Child(1, "Riboulet", "Romy", "12/01/2021", "02/05/2021", "https://www.hdwallpaper.nu.jpg", "christine@email.fr", Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 200D, 10, 10, 20, 0.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 200D, 10, 10, 20, 0.0, 1)
        ));
        double taxRelief = 451D;
        //WHEN
        when(calculateTaxReliefServiceMock.calculateTaxReliefByChild(anyString(), anyInt())).thenReturn(taxRelief);
        double totalAnnualReportableAmountsResult = totalAnnualTaxReliefsServiceImplTest.getTotalAnnualReportableAmountsByChild(childRomy, "2022");
        //THEN
        //totalAnnualReportableAmountsResult is negative result is zero
        assertEquals(0D, totalAnnualReportableAmountsResult);
    }

}
