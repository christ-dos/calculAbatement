package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.List;

@Service
public class CalculateTaxRelief {
    private MonthlyRepository monthlyRepository;

    @Autowired
    public CalculateTaxRelief(MonthlyRepository monthlyRepository) {
        this.monthlyRepository = monthlyRepository;
    }

    public double calculateTaxReliefByChild(double rateSmic1, double rateSmic2, Month monthOfIncrease, String year, int childId) {
        List<Monthly> monthliesByYear;
        double taxRelief;

        monthliesByYear = (List<Monthly>) monthlyRepository.findMonthlyByYear(year);
        //Si le tarif du Smic a changé une seule fois dans l'année, on calcul l'abatement pour l'année entière.
        if (rateSmic2 == 0D) {
            taxRelief = getTaxReliefByChildForAFullYear(monthliesByYear, childId, rateSmic1);
        }else {
            //Sinon on calcul sur 2 periodes avec 2 tarif smic différents
            taxRelief = getTaxReliefByChildWhenUpwardOccurredTwoTimesInYear(monthliesByYear, childId, monthOfIncrease, rateSmic1, rateSmic2);
        }
        return Precision.round(taxRelief, 2);
    }

    private int convertHoursWorkedInDaysAndRoundedUpToNextInteger(double hoursWorked) {
        return (int) Math.ceil(hoursWorked / 8);
    }

    private double getTaxReliefByChildForAFullYear(List<Monthly> monthliesByYear, int childId, double rateSmic1) {
        int sumDaysWorked = monthliesByYear.stream().filter(monthly -> monthly.getChildId() == childId)
                .map(Monthly::getDayWorked)
                .reduce(0, Integer::sum);

        double sumHoursWorked = monthliesByYear.stream().filter(monthly -> monthly.getChildId() == childId)
                .map(Monthly::getHoursWorked)
                .mapToDouble(Double::doubleValue)
                .sum();
        int totalDaysWorked = sumDaysWorked + convertHoursWorkedInDaysAndRoundedUpToNextInteger(sumHoursWorked);
        return totalDaysWorked * (rateSmic1 * 3);
    }

    private double getTaxReliefByChildWhenUpwardOccurredTwoTimesInYear(List<Monthly> monthliesByYear, int childId, Month monthOfIncrease, double rateSmic1, double rateSmic2) {
        //Si le tarif du Smic a changé 2 fois dans l'année, on calcul l'abatement pour 2 periodes distinctes.
        Integer sumDaysWorkedFirstPeriod = monthliesByYear.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .filter(monthly -> monthly.getMonth().getValue() < monthOfIncrease.getValue())
                .map(Monthly::getDayWorked)
                .reduce(0, Integer::sum);
        double sumHoursWorkedFirstPeriod = monthliesByYear.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .filter(monthly -> monthly.getMonth().getValue() < monthOfIncrease.getValue())
                .map(Monthly::getHoursWorked)
                .mapToDouble(Double::doubleValue)
                .sum();

        int totalDaysWorkedFirstPeriod = sumDaysWorkedFirstPeriod + convertHoursWorkedInDaysAndRoundedUpToNextInteger(sumHoursWorkedFirstPeriod);
        double taxReliefFirstPeriod = totalDaysWorkedFirstPeriod * (rateSmic1 * 3);

        Integer sumDaysWorkedSecondPeriod = monthliesByYear.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .filter(monthly -> monthly.getMonth().getValue() >= monthOfIncrease.getValue())
                .map(Monthly::getDayWorked)
                .reduce(0, Integer::sum);
        double sumHoursWorkedSecondPeriod = monthliesByYear.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .filter(monthly -> monthly.getMonth().getValue() >= monthOfIncrease.getValue())
                .map(Monthly::getHoursWorked)
                .mapToDouble(Double::doubleValue)
                .sum();

        int totalDaysWorkedSecondPeriod = sumDaysWorkedSecondPeriod + convertHoursWorkedInDaysAndRoundedUpToNextInteger(sumHoursWorkedSecondPeriod);
        double taxReliefSecondPeriod = totalDaysWorkedSecondPeriod * (rateSmic2 * 3);

        return taxReliefFirstPeriod + taxReliefSecondPeriod;
    }
}

