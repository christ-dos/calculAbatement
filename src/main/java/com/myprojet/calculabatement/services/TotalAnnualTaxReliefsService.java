package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.utils.CalculateAge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public double getTotalAnnualReliefs(String year, double feeLunch, double feeTaste) {
        List<Child> childrenByCurrentUser = (List<Child>) childService.getChildrenByUserEmail(currentUser);
        List<Integer> listChildId = getListChildrenIdByCurrentUser(childrenByCurrentUser);

        double SumTaxReliefAllChildrenByCurrentUser = getSumTaxReliefAllChildrenByCurrentUser(year, listChildId);
        double SumFoodCompensationAllChildrenByCurrentUser = getSumFoodCompensationAllChildrenByCurrentUser(year, feeLunch, feeTaste, listChildId);
        double sumTaxableSalaryForAllChildrenByYear = getSumTaxableSalaryAllChildrenByCurrenUser(year, childrenByCurrentUser);


        double reportableAmounts = sumTaxableSalaryForAllChildrenByYear - SumTaxReliefAllChildrenByCurrentUser + SumFoodCompensationAllChildrenByCurrentUser;
        System.out.println("SumFoodCompensationAllChildrenByCurrentUser " + SumFoodCompensationAllChildrenByCurrentUser);
        // System.out.println(SumFoodCompensationAllChildrenByCurrentUser);
        return sumTaxableSalaryForAllChildrenByYear;
    }

    private List<Integer> getListChildrenIdByCurrentUser(List<Child> childrenByCurrentUser) {
        List<Integer> listChildId = new ArrayList<>();
        for (Child child : childrenByCurrentUser) {
            listChildId.add(child.getId());
        }
        log.info("get list of children ID by current user: " + currentUser);
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

    private double getSumFoodCompensationAllChildrenByCurrentUser(String year, double feeLunch, double feeTaste, List<Integer> listChildId) {
        List<Child> childrenByCurrentUser = (List<Child>) childService.getChildrenByUserEmail(currentUser);
        double sumFoodCompensationChildrenCurrentUser1 =
                childrenByCurrentUser.stream()
                        .map(child-> {
                            double foodCompensationByYearAndByChildId = 0D;
                            if (CalculateAge.getAge(child.getBirthDate()) == 1) {
                                String birthDate = child.getBirthDate();
                                List<String> birthDateSplit = Arrays.stream(birthDate.split("/")).collect(Collectors.toList());
                                int birthDateMonth = Integer.parseInt(birthDateSplit.get(1));
                                foodCompensationByYearAndByChildId = child.getMonthlies().stream()
                                        .filter(monthly->monthly.getMonth().getValue() > birthDateMonth)
                                        .map(childId -> calculateFoodCompensationService.calculateFoodCompensationByYearAndByChildId(year, feeLunch, feeTaste, child.getId()))
                                        .mapToDouble(Double::doubleValue).sum();

                                   // foodCompensationByYearAndByChildId = calculateFoodCompensationService.calculateFoodCompensationByYearAndByChildId(year, feeLunch, feeTaste, child.getId());
                                    //todo à tester pou verifier que ca marche
                               log.info("je suis dans == 1");
                                System.out.println("date anniv  mois" + foodCompensationByYearAndByChildId);

                            } else if (CalculateAge.getAge(child.getBirthDate()) < 1) {
                                foodCompensationByYearAndByChildId = 0D;
                                log.info("je suis dans < 1");
                            }else{
                                foodCompensationByYearAndByChildId = calculateFoodCompensationService.calculateFoodCompensationByYearAndByChildId(year, feeLunch, feeTaste, child.getId());
                                log.info("je suis dans >1");
                            }
                            return foodCompensationByYearAndByChildId;
                        })
                        // .forEach(x->System.out.println("MA LISTE" + x));
//                        .map(chilId -> chilId.getId())
//                        .map(childId -> calculateFoodCompensationService.calculateFoodCompensationByYearAndByChildId(year, feeLunch, feeTaste, childId))
                        .mapToDouble(Double::doubleValue)
                        .sum();


        double sumFoodCompensationChildrenCurrentUser = listChildId.stream()
                .map(childId -> calculateFoodCompensationService.calculateFoodCompensationByYearAndByChildId(year, feeLunch, feeTaste, childId))
                .mapToDouble(Double::doubleValue)
                .sum();
        log.info("Service: get total food compensation for all children by current user: " + currentUser + " for year: " + year);
        return sumFoodCompensationChildrenCurrentUser1; // todo a finir
    }

    private double getSumTaxableSalaryAllChildrenByCurrenUser(String year, List<Child> childrenByCurrentUser) {
        //List<Child> childrenByUser = (List<Child>) childService.getChildrenByUserEmail(currentUser);
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
}
