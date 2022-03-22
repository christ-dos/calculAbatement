package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.Child;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TotalAnnualTaxReliefsService {
    private ChildService childService ;
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

    public double getTotalAnnualReliefs(){
        List<Integer> listChildId = getListChildrenIdByCurrentUser();

        return getSumTaxReliefAllChildrenByCurrentUser("2021", listChildId);
    }

    private List<Integer> getListChildrenIdByCurrentUser(){
        log.info("get list of children id by current user: " + currentUser);
        List<Integer> listChildId = new ArrayList<>();
        for (Child child : childService.getChildrenByUserEmail(currentUser)) {
            listChildId.add(child.getId());
        }
        return listChildId;
    }

    private double getSumTaxReliefAllChildrenByCurrentUser(String year, List<Integer> listChildId){
        double sumTaxReliefChildrenCurrentUser =
                listChildId.stream()
                .map(childId-> calculateTaxReliefService.calculateTaxReliefByChild(year,childId))
                .mapToDouble(Double::doubleValue)
                .sum();
        log.info("Service: get total annual tax Relief for all children by current user: " + currentUser + " for year: " + year);
        return sumTaxReliefChildrenCurrentUser;
    }

    private double getSumFoodCompensationAllChildrenByCurrentUser(String year, List<Integer> listChildId){
        return 0.00;
    }
}
