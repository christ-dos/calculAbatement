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
    private ChildService childService;
    private CalculateTaxReliefService calculateTaxReliefService;
    private CalculateFoodCompensationService calculateFoodCompensationService;
    private MonthlyService monthlyService;

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

    public double getTotalAnnualReportableAmounts(String year, double feeLunch, double feeTaste) {
        List<Child> childrenByCurrentUser = (List<Child>) childService.getChildrenByUserEmail(currentUser);
        List<Integer> listChildId = getListChildrenIdByCurrentUser(childrenByCurrentUser);

        double SumTaxReliefAllChildrenByCurrentUser = getSumTaxReliefAllChildrenByCurrentUser(year, listChildId);
        double SumFoodCompensationAllChildrenByCurrentUser = getSumFoodCompensationAllChildrenByCurrentUser(year, feeLunch, feeTaste);
        double sumTaxableSalaryForAllChildrenByYear = getSumTaxableSalaryAllChildrenByCurrenUser(year, childrenByCurrentUser);

        double reportableAmounts = sumTaxableSalaryForAllChildrenByYear - SumTaxReliefAllChildrenByCurrentUser + SumFoodCompensationAllChildrenByCurrentUser;
        if (reportableAmounts < 0) {
            reportableAmounts = 0D;
        }
        log.info("Service: get reportable amounts to declare for year: " + year);
        return Precision.round(reportableAmounts, 2);
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
        List<Child> childrenByCurrentUser = (List<Child>) childService.getChildrenByUserEmail(currentUser);
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
                log.info("Service: The child is one year old and over");
                foodCompensationByYearAndByChildId += calculateFoodCompensationService.calculateFoodCompensationByYearAndByChildId(
                        year, feeLunch, feeTaste, monthliesByYear, child.getId());
            }
        }
        log.info("Service: get total food compensation for all children by current user: " + currentUser + " for year: " + year);
        return foodCompensationByYearAndByChildId;
    }

    private double getSumTaxableSalaryAllChildrenByCurrenUser(String year, List<Child> childrenByCurrentUser) {
        double sumTaxableSalaryForAllChildrenByYear =
                childrenByCurrentUser.stream()
                        .map(child -> child.getMonthlies()
                                .stream()
                                .filter(monthly -> monthly.getYear().equals(year))
                                .mapToDouble(Monthly::getTaxableSalary).sum()
                        ).mapToDouble(Double::doubleValue).sum();
        log.info("Service: get total taxable salary for all children by current user: " + currentUser + " for year: " + year);
        return sumTaxableSalaryForAllChildrenByYear;
    }

    private double getSumFoodCompensationWhenChildIsOneYearOld(Child child, String year, double feeLunch, double feeTaste) {
        double foodCompensationByYearAndByChildId = 0D;

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
