package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.utils.CalculateAge;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TotalAnnualTaxReliefsServiceImpl implements TotalAnnualTaxReliefsService {
    private final ChildService childService;
    private final CalculateTaxReliefServiceImpl calculateTaxReliefService;
    private final CalculateFoodCompensationService calculateFoodCompensationService;
    private TaxableSalaryService taxableSalaryService;

    @Autowired
    public TotalAnnualTaxReliefsServiceImpl(ChildService childService,
                                            CalculateTaxReliefServiceImpl calculateTaxReliefService,
                                            CalculateFoodCompensationService calculateFoodCompensationService,
                                            TaxableSalaryService taxableSalaryService) {
        this.childService = childService;
        this.calculateTaxReliefService = calculateTaxReliefService;
        this.calculateFoodCompensationService = calculateFoodCompensationService;
        this.taxableSalaryService = taxableSalaryService;
    }

    @Override
    public double getTotalAnnualReportableAmountsForAllChildren(String year) {
        List<Child> childrenByCurrentUser = (List<Child>) childService.getChildrenByUserEmailOrderByDateAddedDesc();
        double TotalAnnualReportableAmounts = childrenByCurrentUser.stream()
                .filter(child -> child.getEndContract() == null || getYearEndContract(child.getEndContract()) == LocalDateTime.now().getYear() - 1)
                .map(child -> getTotalAnnualReportableAmountsByChild(child, year))
                .mapToDouble(Double::doubleValue).sum();
        log.info("Service: Get reportable amounts for all children to declare in year: " + year + ", Value: " + TotalAnnualReportableAmounts);

        return Precision.round(TotalAnnualReportableAmounts, 2);
    }

    @Override
    public double getTotalAnnualReportableAmountsByChild(Child child, String year) {
        List<Monthly> monthliesByYear = child.getMonthlies().stream().filter(monthly -> monthly.getYear().equals(year)).collect(Collectors.toList());
        if (monthliesByYear.isEmpty()) {
            log.error("Service: Monthly not found for year: " + year + ", and for child ID: "+ child.getId()); //todo implement test pr ce if ajouter
            throw new MonthlyNotFoundException("Il n'y a aucune entrée enregistré pour l'année: " + year);
        }
        double taxRelief = calculateTaxReliefService.calculateTaxReliefByChild(year, child.getId());
        double foodCompensation = getSumFoodCompensationByYearAndByChild(child, year);
        double taxableSalaryByYear = taxableSalaryService.getSumTaxableSalaryByChildAndByYear(year, child.getId());

        double reportableAmountsByChild = taxableSalaryByYear - taxRelief + foodCompensation;
        if (reportableAmountsByChild < 0) {
            reportableAmountsByChild = 0D;
        }
        log.info("Service: Calculate reportable amounts by child ID: " + child.getId() + ", for year: " + year + ", Value: " + reportableAmountsByChild);
        return reportableAmountsByChild;
    }

    private int getYearEndContract(String endContract) {
        List<String> endContractArray = Arrays.asList(endContract.split("/"));
        return Integer.parseInt(endContractArray.get(2));
    }

    private double getSumFoodCompensationByYearAndByChild(Child child, String year) {
        double foodCompensationByYearAndByChildId = 0;

        Integer intValueOfMaxMonthInMonthliesFilteredByYear = child.getMonthlies().stream()
                .filter(monthly -> monthly.getYear().equals(year))
                .map(monthly -> monthly.getMonth().getValue()).max(Integer::compare).get();

        int childAge = CalculateAge.getAge(child.getBirthDate(), year, Integer.toString(intValueOfMaxMonthInMonthliesFilteredByYear));
        if (childAge == 1) {
            foodCompensationByYearAndByChildId = getSumFoodCompensationWhenChildIsOneYearOld(child, year);
            log.info("Service: The child is one year old and food compensation equal: " + foodCompensationByYearAndByChildId);
        } else if (childAge < 1) {
            log.info("Service: The child is less than one year old and food compensation equal: " + foodCompensationByYearAndByChildId);
            foodCompensationByYearAndByChildId = 0D;
        } else {
            log.info("Service: The child is over 1 years old and food compensation equal: " + foodCompensationByYearAndByChildId);
            foodCompensationByYearAndByChildId = calculateFoodCompensationService.calculateFoodCompensationByYearAndByChild(
                    year, child.getMonthlies(), child.getFeesLunch(), child.getFeesSnack());
        }
        log.info("Service: Get total food compensation by child Id : " + child.getId() + " for year: " + year);
        return foodCompensationByYearAndByChildId;
    }

    private double getSumFoodCompensationWhenChildIsOneYearOld(Child child, String year) {
        double foodCompensationByYearAndByChildId = 0;
        int birthDateMonth = getBirthDateMonth(child.getBirthDate());

        List<Monthly> monthliesAfterBirthDateMonth = new ArrayList<>();
        for (Monthly monthly : child.getMonthlies()) {
            if (monthly.getMonth().getValue() > birthDateMonth && monthly.getYear().equals(year)) {
                monthliesAfterBirthDateMonth.add(monthly);
            }
        }
        if (!monthliesAfterBirthDateMonth.isEmpty()) {
            foodCompensationByYearAndByChildId = calculateFoodCompensationService.calculateFoodCompensationByYearAndByChild(
                    year, monthliesAfterBirthDateMonth, child.getFeesLunch(), child.getFeesSnack());
        } else {
            foodCompensationByYearAndByChildId = 0D;
        }

        log.info("Service: Get total food compensation if child: " + child.getId() + " is one year old");
        return foodCompensationByYearAndByChildId;
    }

    private int getBirthDateMonth(String birthDate) {
        List<String> birthDateSplit = Arrays.stream(birthDate.split("/")).collect(Collectors.toList());
        return Integer.parseInt(birthDateSplit.get(1));
    }
}
