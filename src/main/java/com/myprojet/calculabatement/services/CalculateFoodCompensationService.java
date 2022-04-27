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

    public double calculateFoodCompensationByYearAndByChild(String year, List<Monthly> monthlies, double feesLunch, double feesSnacks) {
        if (monthlies.isEmpty()) {
            log.error("Service: Monthly not found for year: " + year);
            throw new MonthlyNotFoundException("Il n'y a aucune entrée enregistré pour l'année: " + year);
        }// todo verifier si cette exception est nécessaire?
        int sumLunchByChild = monthlies.stream()
                .filter(monthly -> monthly.getYear().equals(year))
                .map(Monthly::getLunch)
                .reduce(0, Integer::sum);
        int sumSnacksByChild = monthlies.stream()
                .filter(monthly -> monthly.getYear().equals(year))
                .map(Monthly::getSnack)
                .reduce(0, Integer::sum);

        if (sumLunchByChild > 0 && feesLunch == 0D || sumSnacksByChild > 0 && feesSnacks == 0D) {
            log.error("Service: Fees cannot be null when lunch or snacks are present");
            throw new FeesEqualZeroException("Le tarif des repas ne peut pas être null");
        }
        double sumFoodCompensation =  (sumLunchByChild * feesLunch + (sumSnacksByChild * feesSnacks));

        log.debug("Service: Food compensation by child ID : " + monthlies.get(0).getChildId() + " and by year: " + year + ", total: " + sumFoodCompensation);
        return sumFoodCompensation;
    }

}
