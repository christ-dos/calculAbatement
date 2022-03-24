package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.FeesEqualZeroException;
import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CalculateFoodCompensationService {
    private MonthlyRepository monthlyRepository;

    @Autowired
    public CalculateFoodCompensationService(MonthlyRepository monthlyRepository) {
        this.monthlyRepository = monthlyRepository;
    }

    public double calculateFoodCompensationByYearAndByChildId(String year, double feeLunch, double feeTaste, List<Monthly> monthlyList, int childId) {
        if (monthlyList.isEmpty()) {
            log.error("Service: Monthly not found for year: " + year);
            throw new MonthlyNotFoundException("Il n'y a aucune entrée enregistré pour l'année: " + year);
        }

        int sumLunchByChildId = monthlyList.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .map(Monthly::getLunch)
                .reduce(0, Integer::sum);
        int sumTasteByChildId = monthlyList.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .map(Monthly::getTaste)
                .reduce(0, Integer::sum);

        if (sumLunchByChildId > 0 && feeLunch == 0 || sumTasteByChildId > 0 && feeTaste == 0) {
            log.error("Service: Fees cannot be null when lunch or taste are present");
            throw new FeesEqualZeroException("Le tarif des repas ne peut pas être null");
        }
        log.debug("Service: Food compensation by child id : " + childId + " and by year: " + year);
        return (sumLunchByChildId * feeLunch) + (sumTasteByChildId * feeTaste);
    }
}
