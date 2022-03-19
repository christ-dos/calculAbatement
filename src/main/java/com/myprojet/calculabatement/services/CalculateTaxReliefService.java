package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.models.RateSmicApi;
import com.myprojet.calculabatement.proxies.RateSmicProxy;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CalculateTaxReliefService {
    private MonthlyRepository monthlyRepository;

    private RateSmicProxy rateSmicProxy;

    @Autowired
    public CalculateTaxReliefService(MonthlyRepository monthlyRepository, RateSmicProxy rateSmicProxy) {
        this.monthlyRepository = monthlyRepository;
        this.rateSmicProxy = rateSmicProxy;
    }

    public double calculateTaxReliefByChild(String year, int childId) {
        double taxRelief = 0;
        //get smic values by insee Api
        List<RateSmicApi> smicValues = rateSmicProxy.getRateSmicByInseeApi(year, "12");
        List<Monthly> monthliesByYear = (List<Monthly>) monthlyRepository.findMonthlyByYear(year);
        if (monthliesByYear.isEmpty() || Integer.parseInt(year) > LocalDate.now().getYear()) {
            log.error("Service: Monthly not found for year: " + year);
            throw new MonthlyNotFoundException("Il n'y a aucune entrée enregistré pour l'année: " + year);
        }
        //group rateSmic by values
        List<List<RateSmicApi>> listsRateSmicGroupByRateSmicValue = smicValues.stream()
                .collect(Collectors.groupingBy(RateSmicApi::getSmicValue)).values().stream()
                .filter(smicWithSameValue -> smicWithSameValue.size() >= 1)
                .collect(Collectors.toList());

        int monthOfIncrease = getMonthOfIncrease(listsRateSmicGroupByRateSmicValue );

        //Si le tarif du Smic a changé une seule fois dans l'année, on calcul l'abatement pour l'année entière.
        if (listsRateSmicGroupByRateSmicValue.size() == 1) {
            taxRelief = getTaxReliefByChildForAFullYear(monthliesByYear, childId, listsRateSmicGroupByRateSmicValue);
            log.debug("Service: Calculation tax relief for a full year: " + year);
        } else if (listsRateSmicGroupByRateSmicValue.size() == 2) {
            //Sinon on calcul sur 2 periodes avec 2 tarifs smic différents
            taxRelief = getTaxReliefByChildWhenUpwardOccurredTwoTimesInYear(
                    monthliesByYear, childId, monthOfIncrease, listsRateSmicGroupByRateSmicValue);
            log.debug("Service: Calculation tax relief for two periods in month of increase: " + monthOfIncrease);
        }
        log.info("Service: Display the value of the tax relief by child id and by year");
        return Precision.round(taxRelief, 2);
    }

    private int convertHoursWorkedInDaysAndRoundedUpToNextInteger(double hoursWorked) {
        log.info("Service: Convert the sum of hours in days");
        return (int) Math.ceil(hoursWorked / 8);
    }

    private double getTaxReliefByChildForAFullYear(List<Monthly> monthliesByYear, int childId,
                                                   List<List<RateSmicApi>> listsSmicValuesGroupByRateSmicValue) {
        List<Double> listSmicValues = getRateSmicValue(listsSmicValuesGroupByRateSmicValue);
        double rateSmic1 = listSmicValues.get(0);
        double rateSmic2 = listSmicValues.get(1);

        int sumDaysWorked = monthliesByYear.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .map(Monthly::getDayWorked)
                .reduce(0, Integer::sum);

        double sumHoursWorked = monthliesByYear.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .map(Monthly::getHoursWorked)
                .mapToDouble(Double::doubleValue)
                .sum();
        int totalDaysWorked = sumDaysWorked + convertHoursWorkedInDaysAndRoundedUpToNextInteger(sumHoursWorked);
        return totalDaysWorked * (rateSmic1 * 3);
    }

    private double getTaxReliefByChildWhenUpwardOccurredTwoTimesInYear(List<Monthly> monthliesByYear, int childId, int monthOfIncrease, List<List<RateSmicApi>> listsSmicValuesGroupByRateSmicValue) {
        List<Double> listSmicValues = getRateSmicValue(listsSmicValuesGroupByRateSmicValue);
        double rateSmic1 = listSmicValues.get(0);
        double rateSmic2 = listSmicValues.get(1);

        //Si le tarif du Smic a changé 2 fois dans l'année, on calcul l'abatement pour 2 périodes distinctes.
        Integer sumDaysWorkedFirstPeriod = monthliesByYear.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .filter(monthly -> monthly.getMonth().getValue() < monthOfIncrease)
                .map(Monthly::getDayWorked)
                .reduce(0, Integer::sum);
        double sumHoursWorkedFirstPeriod = monthliesByYear.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .filter(monthly -> monthly.getMonth().getValue() < monthOfIncrease)
                .map(Monthly::getHoursWorked)
                .mapToDouble(Double::doubleValue)
                .sum();

        int totalDaysWorkedFirstPeriod = sumDaysWorkedFirstPeriod + convertHoursWorkedInDaysAndRoundedUpToNextInteger(sumHoursWorkedFirstPeriod);
        double taxReliefFirstPeriod = totalDaysWorkedFirstPeriod * (rateSmic1 * 3);

        Integer sumDaysWorkedSecondPeriod = monthliesByYear.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .filter(monthly -> monthly.getMonth().getValue() >= monthOfIncrease)
                .map(Monthly::getDayWorked)
                .reduce(0, Integer::sum);
        double sumHoursWorkedSecondPeriod = monthliesByYear.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .filter(monthly -> monthly.getMonth().getValue() >= monthOfIncrease)
                .map(Monthly::getHoursWorked)
                .mapToDouble(Double::doubleValue)
                .sum();

        int totalDaysWorkedSecondPeriod = sumDaysWorkedSecondPeriod +
                convertHoursWorkedInDaysAndRoundedUpToNextInteger(sumHoursWorkedSecondPeriod);
        double taxReliefSecondPeriod = totalDaysWorkedSecondPeriod * (rateSmic2 * 3);

        return taxReliefFirstPeriod + taxReliefSecondPeriod;
    }

    private List<Double> getRateSmicValue(List<List<RateSmicApi>> listsSmicValuesGroupByRateSmicValue) {
        double rateSmic1;
        double rateSmic2 = 0;
        List<Double> listSmicValues = new ArrayList<>();

        if (listsSmicValuesGroupByRateSmicValue.size() == 2) {
            rateSmic2 = Double.parseDouble(listsSmicValuesGroupByRateSmicValue.get(0).get(0).getSmicValue());
            rateSmic1 = Double.parseDouble(listsSmicValuesGroupByRateSmicValue.get(1)
                    .get(listsSmicValuesGroupByRateSmicValue.get(1).size() - 1).getSmicValue());
            listSmicValues.add(rateSmic1);
            listSmicValues.add(rateSmic2);
        }
        if (listsSmicValuesGroupByRateSmicValue.size() == 1) {
            rateSmic1 = Double.parseDouble(listsSmicValuesGroupByRateSmicValue.get(0).get(0).getSmicValue());
            listSmicValues.add(rateSmic1);
            listSmicValues.add(rateSmic2);
        }
        return listSmicValues;
    }

    private int getMonthOfIncrease(List<List<RateSmicApi>> listsRateSmicGroupByRateSmicValue ){
        List<RateSmicApi> firstListGroupByRateSmicValue = listsRateSmicGroupByRateSmicValue
                .get(0);
        //obtain last element of the group list and get timePeriod of increase
        String timePeriodLastRateSmicApi = firstListGroupByRateSmicValue.get(firstListGroupByRateSmicValue.size() - 1)
                .getTimePeriod();
        List<String> listStringTimePeriod = Arrays.asList(timePeriodLastRateSmicApi.split("-"));
        int monthOfIncrease = Integer.parseInt(listStringTimePeriod.get(1));

        log.info("Service : get the month of increase");
        return monthOfIncrease;
    }
}

