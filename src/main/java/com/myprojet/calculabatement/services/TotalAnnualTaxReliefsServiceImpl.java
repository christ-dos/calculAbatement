package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.utils.CalculateAge;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TotalAnnualTaxReliefsServiceImpl implements TotalAnnualTaxReliefsService {
    private final ChildService childService;
    private final CalculateTaxReliefService calculateTaxReliefService;
    private final CalculateFoodCompensationService calculateFoodCompensationService;
    private TaxableSalaryService taxableSalaryService;

    @Autowired
    public TotalAnnualTaxReliefsServiceImpl(ChildService childService,
                                            CalculateTaxReliefService calculateTaxReliefService,
                                            CalculateFoodCompensationService calculateFoodCompensationService,
                                            TaxableSalaryService taxableSalaryService) {
        this.childService = childService;
        this.calculateTaxReliefService = calculateTaxReliefService;
        this.calculateFoodCompensationService = calculateFoodCompensationService;
        this.taxableSalaryService = taxableSalaryService;
    }

    @Override
    public double getTotalAnnualReportableAmountsForAllChildren(String year, double feeLunch, double feeTaste) {
        List<Child> childrenByCurrentUser = (List<Child>) childService.getChildrenByUserEmail();
        double TotalAnnualReportableAmounts = childrenByCurrentUser.stream()
                .map(child -> getTotalAnnualReportableAmountsByChild(child.getId(), year, feeLunch, feeTaste))
                .mapToDouble(Double::doubleValue).sum();
        log.info("Service: Get reportable amounts for all children to declare in year: " + year + ", Value: " + TotalAnnualReportableAmounts);
        return Precision.round(TotalAnnualReportableAmounts, 2);
    }

    @Override
    public double getTotalAnnualReportableAmountsByChild(int childId, String year, double feeLunch, double feeTaste) {
        Child child = childService.getChildById(childId);

        double taxRelief = calculateTaxReliefService.calculateTaxReliefByChild(year, childId);
        double foodCompensation = getSumFoodCompensationByYearAndByChild(child, year, feeLunch, feeTaste);
        double taxableSalaryByYear = taxableSalaryService.getSumTaxableSalaryByChildAndByYear(child, year);

        double reportableAmountsByChild = taxableSalaryByYear - taxRelief + foodCompensation;
        if (reportableAmountsByChild < 0) {
            reportableAmountsByChild = 0D;
        }
        log.info("Service: Calculate reportable amounts by child ID: " + child.getId() + ", for year: " + year + ", Value: " + reportableAmountsByChild);
        return reportableAmountsByChild;
    }



    private double getSumFoodCompensationByYearAndByChild(Child child, String year, double feeLunch, double feeTaste) {
        double foodCompensationByYearAndByChildId = 0D;
        int childAge = CalculateAge.getAge(child.getBirthDate());

        if (childAge == 1) {
            foodCompensationByYearAndByChildId = getSumFoodCompensationWhenChildIsOneYearOld(child, year, feeLunch, feeTaste);
            log.info("Service: The child is one year old");
        } else if (childAge < 1) {
            log.info("Service: The child is less than one year old");
            foodCompensationByYearAndByChildId = 0D;
        } else {
            log.info("Service: The child is over 1 years old");
            foodCompensationByYearAndByChildId = calculateFoodCompensationService.calculateFoodCompensationByYearAndByChildId(
                    year, feeLunch, feeTaste, child.getMonthlies(), child.getId());
        }
        log.info("Service: Get total food compensation by child Id : " + child.getId() + " for year: " + year);
        return foodCompensationByYearAndByChildId;
    }

    private double getSumFoodCompensationWhenChildIsOneYearOld(Child child, String year, double feeLunch, double feeTaste) {
        double foodCompensationByYearAndByChildId;

        String birthDate = child.getBirthDate();
        List<String> birthDateSplit = Arrays.stream(birthDate.split("/")).collect(Collectors.toList());
        int birthDateMonth = Integer.parseInt(birthDateSplit.get(1));

        List<Monthly> monthliesAfterBirthDateMonth = new ArrayList<>();
        for (Monthly monthly : child.getMonthlies()) {
            if (monthly.getMonth().getValue() > birthDateMonth && monthly.getYear().equals(year)) {
                monthliesAfterBirthDateMonth.add(monthly);
            }
        }
        if (!monthliesAfterBirthDateMonth.isEmpty()) {
            foodCompensationByYearAndByChildId = calculateFoodCompensationService.calculateFoodCompensationByYearAndByChildId(
                    year, feeLunch, feeTaste, monthliesAfterBirthDateMonth, child.getId());
        } else {
            foodCompensationByYearAndByChildId = 0D;
        }
        log.info("Service: Get total food compensation if child is one year old");
        return foodCompensationByYearAndByChildId;
    }
}
