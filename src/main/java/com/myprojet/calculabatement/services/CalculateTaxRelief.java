package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalculateTaxRelief {
    private MonthlyRepository monthlyRepository;

    @Autowired
    public CalculateTaxRelief(MonthlyRepository monthlyRepository) {
        this.monthlyRepository = monthlyRepository;
    }

    public double calculateTaxReliefByChild(double rateSmic1, double rateSmic2, Month monthOfIncrease, String year, int childId) {
        List<Monthly> monthliesByYear;
        int daysPresencesForPeriodOne;
        int daysPresencesForPeriodTwo;
        // List<Monthly> sumDaysWorkedByYear = new ArrayList<>();
        double taxRelief = 0D;


        monthliesByYear = (List<Monthly>) monthlyRepository.findMonthlyByYear("2022");
        //Si le tarif du Smic a changé une seule fois dans l'année, on calcul l'abatement pour l'année entière.
        if (rateSmic2 == 0D) {
            taxRelief = getNumberOfDaysWorkedForAFullYear(monthliesByYear, childId, rateSmic1);
        }
        //Si le tarif du Smic a changé 2 fois dans l'année, on calcul l'abatement pour 2 periodes distinctes.
        List<Monthly> monthValuesFirstPeriod = monthliesByYear.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .filter(monthly -> monthly.getMonth().getValue() < monthOfIncrease.getValue())
                .collect(Collectors.toList());
        List<Monthly> monthValuesSecondPeriod = monthliesByYear.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .filter(monthly -> monthly.getMonth().getValue() >= monthOfIncrease.getValue())
                .collect(Collectors.toList());

        return Precision.round(taxRelief, 2);
    }

    private int convertHoursWorkedInDaysAndRoundedUpToNextInteger(double hoursWorked) {
        return (int) Math.ceil(hoursWorked / 8);
    }

    private double getNumberOfDaysWorkedForAFullYear(List<Monthly> monthliesByYear, int childId, double rateSmic1) {
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


}

