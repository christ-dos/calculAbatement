package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.FeesEqualZeroException;
import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CalculateFoodCompensationService {
    private MonthlyRepository monthlyRepository;

    @Autowired
    public CalculateFoodCompensationService(MonthlyRepository monthlyRepository) {
        this.monthlyRepository = monthlyRepository;
    }

    public double calculateFoodCompensationByYearAndByChild(String year, Child child) {
        if (child.getMonthlies().isEmpty()) {
            log.error("Service: Monthly not found for year: " + year);
            throw new MonthlyNotFoundException("Il n'y a aucune entrée enregistré pour l'année: " + year);
        }
        int sumLunchByChild = child.getMonthlies().stream()
               // .filter(monthly -> monthly.getChildId() == child.getId()) //todo clean code
                .filter(monthly -> monthly.getYear().equals(year))
                .map(Monthly::getLunch)
                .reduce(0, Integer::sum);
        int sumTasteByChild = child.getMonthlies().stream()
               // .filter(monthly -> monthly.getChildId() == child.getId())//todo clean code
                .filter(monthly -> monthly.getYear().equals(year))
                .map(Monthly::getTaste)
                .reduce(0, Integer::sum);

        if (sumLunchByChild > 0 && child.getFeesLunch() == 0D || sumTasteByChild > 0 && child.getFeesTaste() == 0D) {
            log.error("Service: Fees cannot be null when lunch or taste are present");
            throw new FeesEqualZeroException("Le tarif des repas ne peut pas être null");
        }
        log.debug("Service: Food compensation by child ID : " + child.getId() + " and by year: " + year);
        return (sumLunchByChild * child.getFeesLunch()) + (sumTasteByChild * child.getFeesTaste());
    }
}
