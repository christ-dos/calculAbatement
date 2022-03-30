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
public class TotalAnnualTaxReliefsService {
    private final ChildService childService;
    private final CalculateTaxReliefService calculateTaxReliefService;
    private final CalculateFoodCompensationService calculateFoodCompensationService;
    private final MonthlyService monthlyService;

    private final String currentUser = "christine@email.fr";

    @Autowired
    public TotalAnnualTaxReliefsService(ChildService childService,
                                        CalculateTaxReliefService calculateTaxReliefService,
                                        CalculateFoodCompensationService calculateFoodCompensationService,
                                        MonthlyService monthlyService) {
        this.childService = childService;
        this.calculateTaxReliefService = calculateTaxReliefService;
        this.calculateFoodCompensationService = calculateFoodCompensationService;
        this.monthlyService = monthlyService;
    }

//    public double getTotalAnnualReportableAmounts(String year, double feeLunch, double feeTaste) {
//        List<Child> childrenByCurrentUser = (List<Child>) childService.getChildrenByUserEmail();
//        List<Integer> listChildId = getListChildrenIdByCurrentUser(childrenByCurrentUser);
//
//        double SumTaxReliefAllChildrenByCurrentUser = getSumTaxReliefAllChildrenByCurrentUser(year, listChildId);
//        double SumFoodCompensationAllChildrenByCurrentUser = getSumFoodCompensationAllChildrenByCurrentUser(year, feeLunch, feeTaste);
//        double sumTaxableSalaryForAllChildrenByYear = getSumTaxableSalaryAllChildrenByCurrenUser(year, childrenByCurrentUser);
//
//        double reportableAmountsForAllChildren = sumTaxableSalaryForAllChildrenByYear - SumTaxReliefAllChildrenByCurrentUser + SumFoodCompensationAllChildrenByCurrentUser;
//        if (reportableAmountsForAllChildren < 0) {
//            reportableAmountsForAllChildren = 0D;
//        }
//        log.info("Service: get reportable amounts to declare for year: " + year);
//        return Precision.round(reportableAmountsForAllChildren, 2);
//    } //todo clean code

    public double getTotalAnnualReportableAmountsForAllChildren(String year, double feeLunch, double feeTaste) {
        List<Child> childrenByCurrentUser = (List<Child>) childService.getChildrenByUserEmail();
        double TotalAnnualReportableAmounts =
                childrenByCurrentUser.stream()
                        .map(child -> getTotalAnnualReportableAmountsByChild(child.getId(), year, feeLunch, feeTaste))
                        .mapToDouble(Double::doubleValue).sum();
        log.info("Service: Get reportable amounts for all children to declare in year: " + year);

        return Precision.round(TotalAnnualReportableAmounts, 2);
    }

    public double getTotalAnnualReportableAmountsByChild(int childId, String year, double feeLunch, double feeTaste) {
        Child child = childService.getChildById(childId);

        double taxRelief = calculateTaxReliefService.calculateTaxReliefByChild(year, childId);
        double foodCompensation = calculateFoodCompensationService.calculateFoodCompensationByYearAndByChildId(year, feeLunch, feeTaste, child.getMonthlies(), child.getId());
        double taxableSalaryByYear = getSumTaxableSalaryByChildAndByYear(child, year);

        double reportableAmountsByChild = taxableSalaryByYear - taxRelief + foodCompensation;
        if (reportableAmountsByChild < 0) {
            reportableAmountsByChild = 0D;
        }
        log.info("Service: Calculate reportable amounts by child ID: " + child.getId() + " and for year: " + year);
        return reportableAmountsByChild;
    }

    private List<Integer> getListChildrenIdByCurrentUser(List<Child> childrenByCurrentUser) {
        List<Integer> listChildId = new ArrayList<>();
        for (Child child : childrenByCurrentUser) {
            listChildId.add(child.getId());
        }
        log.info("Service: get list of children ID by current user: " + currentUser);
        return listChildId;
    }

    private double getSumTaxReliefAllChildrenByCurrentUser(String year, List<Integer> listChildId) {
        double sumTaxReliefChildrenCurrentUser =
                listChildId.stream()
                        .map(childId -> calculateTaxReliefService.calculateTaxReliefByChild(year, childId))
                        .mapToDouble(Double::doubleValue)
                        .sum();
        log.info("Service: get total tax Relief for all children by current user: " + currentUser + " for year: " + year);
        return sumTaxReliefChildrenCurrentUser;
    }

    private double getSumFoodCompensationAllChildrenByCurrentUser(String year, double feeLunch, double feeTaste) {
        List<Child> childrenByCurrentUser = (List<Child>) childService.getChildrenByUserEmail();
        double foodCompensationByYearAndByChildId = 0D;
        List<Monthly> monthliesByYear = (List<Monthly>) monthlyService.getAllMonthlyByYear(year);

        for (Child child : childrenByCurrentUser) {
            int childAge = CalculateAge.getAge(child.getBirthDate());
            if (childAge == 1) {
                foodCompensationByYearAndByChildId += getSumFoodCompensationWhenChildIsOneYearOld(child, year, feeLunch, feeTaste);
                log.info("Service: The child is one year old");
            } else if (childAge < 1) {
                log.info("Service: The child is less than one year old");
                foodCompensationByYearAndByChildId += 0D;
            } else {
                log.info("Service: The child is over 1 years old");
                foodCompensationByYearAndByChildId += calculateFoodCompensationService.calculateFoodCompensationByYearAndByChildId(
                        year, feeLunch, feeTaste, monthliesByYear, child.getId());
            }
        }
        log.info("Service: get total food compensation for all children by current user: " + currentUser + " for year: " + year);
        return foodCompensationByYearAndByChildId;
    }

    private double getSumTaxableSalaryByChildAndByYear(Child child, String year) {
        double sumTaxableSalaryByChildAndByYear = child.getMonthlies().stream()
                .filter(monthly -> monthly.getYear().equals(year))
                .mapToDouble(monthly -> monthly.getTaxableSalary()).sum();

        log.info("calculate taxable salary by child ID: " + child.getId() + " for year: " + year);
        return sumTaxableSalaryByChildAndByYear;
    }

    private double getSumTaxableSalaryAllChildrenByCurrenUser(String year, List<Child> childrenByCurrentUser) {
        double sumTaxableSalaryForAllChildrenByYear =
                childrenByCurrentUser.stream()
                        .map(child -> child.getMonthlies()
                                .stream()
                                .filter(monthly -> monthly.getYear().equals(year))
                                .mapToDouble(Monthly::getTaxableSalary).sum())
                        .mapToDouble(Double::doubleValue)
                        .sum();
        log.info("Service: get total taxable salary for all children by current user: " + currentUser + " for year: " + year);
        return sumTaxableSalaryForAllChildrenByYear;
    }

    private double getSumFoodCompensationWhenChildIsOneYearOld(Child child, String year, double feeLunch, double feeTaste) {
        double foodCompensationByYearAndByChildId;

        String birthDate = child.getBirthDate();
        List<String> birthDateSplit = Arrays.stream(birthDate.split("/")).collect(Collectors.toList());
        int birthDateMonth = Integer.parseInt(birthDateSplit.get(1));

        List<Monthly> monthliesAfterbirthDateMonth = new ArrayList<>();
        for (Monthly monthly : child.getMonthlies()) {
            if (monthly.getMonth().getValue() > birthDateMonth && monthly.getYear().equals(year)) {
                monthliesAfterbirthDateMonth.add(monthly);
            }
        }
        if (!monthliesAfterbirthDateMonth.isEmpty()) {
            foodCompensationByYearAndByChildId = calculateFoodCompensationService.calculateFoodCompensationByYearAndByChildId(year, feeLunch, feeTaste, monthliesAfterbirthDateMonth, child.getId());
        } else {
            foodCompensationByYearAndByChildId = 0D;
        }
        return foodCompensationByYearAndByChildId;
    }
}
