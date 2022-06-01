package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.models.RateSmicApi;
import com.myprojet.calculabatement.proxies.RateSmicProxy;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CalculateTaxReliefServiceImpl implements CalculateTaxReliefService {
    private MonthlyRepository monthlyRepository;

    private RateSmicProxy rateSmicProxy;

    private List<RateSmicApi> smicValues = new ArrayList<>();

    @Autowired
    public CalculateTaxReliefServiceImpl(MonthlyRepository monthlyRepository, RateSmicProxy rateSmicProxy) {
        this.monthlyRepository = monthlyRepository;
        this.rateSmicProxy = rateSmicProxy;
    }


    @Override
    public double calculateTaxReliefByChild(String year, int childId) {
        double taxRelief = 0;
        List<Monthly> monthliesByYear = (List<Monthly>) monthlyRepository.findMonthlyByYear(year);
        if (monthliesByYear.isEmpty()) {
            log.error("Service: Monthly not found for year: " + year);
            throw new MonthlyNotFoundException("Il n'y a aucune entrée enregistré pour l'année: " + year);
        }
        if (smicValues.isEmpty()) {
            try {
                //get smic values by Insee Api
                smicValues = rateSmicProxy.getRateSmicByInseeApi(year, "12");
            } catch (Exception e) {
                System.out.println("Cause: " + e.getCause()); //todo revoir ce catch
            }
        } else {
            List<String> YearTimePeriodList = Arrays.asList(smicValues.get(smicValues.size() - 1).getTimePeriod().split("-"));
            if (!YearTimePeriodList.get(0).equals(year)) {
                System.out.println("la donnée ne correspond pas dc on appel la requete!");
                smicValues = rateSmicProxy.getRateSmicByInseeApi(year, "12");
            }
        }
        //group rateSmic by values
        List<List<RateSmicApi>> listsRateSmicGroupByRateSmicValue = smicValues.stream()
                .collect(Collectors.groupingBy(RateSmicApi::getSmicValue)).values().stream()
                .filter(smicWithSameValue -> smicWithSameValue.size() >= 1)
                .collect(Collectors.toList());

        int monthOfIncrease = getMonthOfIncrease(listsRateSmicGroupByRateSmicValue);

        //Si le tarif du Smic a changé une seule fois dans l'année, on calcul l'abatement pour l'année entière.
        if (listsRateSmicGroupByRateSmicValue.size() == 1) {
            taxRelief = getTaxReliefByChildForAFullYear(monthliesByYear, childId, listsRateSmicGroupByRateSmicValue);
            log.debug("Service: Calculation tax relief for a full year: " + year);
        } else if (listsRateSmicGroupByRateSmicValue.size() >= 2) {
            //Sinon on calcul sur 2 periodes avec 2 tarifs smic différents
            taxRelief = getTaxReliefByChildWhenUpwardOccurredTwoTimesInYear(
                    monthliesByYear, childId, monthOfIncrease, listsRateSmicGroupByRateSmicValue);
            log.debug("Service: Calculation tax relief for two periods in month of increase: " + monthOfIncrease);
        }
        log.debug("Service: The tax relief value: " + taxRelief + " child ID: " + childId + " and by year: " + year);
        return taxRelief;
    }


    private double convertHoursWorkedInDaysAndRoundedUpToNextInteger(double hoursWorked) {
        log.info("Service: Convert hours sum to days");
        return hoursWorked / 8;
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
        double totalDaysWorked = sumDaysWorked + convertHoursWorkedInDaysAndRoundedUpToNextInteger(sumHoursWorked);
        return totalDaysWorked * (rateSmic1 * 3);
    }

    private double getTaxReliefByChildWhenUpwardOccurredTwoTimesInYear(List<Monthly> monthliesByYear, int childId, int monthOfIncrease, List<List<RateSmicApi>> listsSmicValuesGroupByRateSmicValue) {
        List<Double> listSmicValues = getRateSmicValue(listsSmicValuesGroupByRateSmicValue);
        double rateSmic1 = listSmicValues.get(0);
        //Value of rateSmic after increase
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

        double totalDaysWorkedFirstPeriod = sumDaysWorkedFirstPeriod + convertHoursWorkedInDaysAndRoundedUpToNextInteger(sumHoursWorkedFirstPeriod);
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

        double totalDaysWorkedSecondPeriod = sumDaysWorkedSecondPeriod +
                convertHoursWorkedInDaysAndRoundedUpToNextInteger(sumHoursWorkedSecondPeriod);
        double taxReliefSecondPeriod = totalDaysWorkedSecondPeriod * (rateSmic2 * 3);

        return taxReliefFirstPeriod + taxReliefSecondPeriod;
    }

    private List<Double> getRateSmicValue(List<List<RateSmicApi>> listsSmicValuesGroupByRateSmicValue) {
        double rateSmic1;
        double rateSmic2 = 0;
        List<Double> listSmicValues = new ArrayList<>();

        if (listsSmicValuesGroupByRateSmicValue.size() >= 2) {
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
        log.info("Service: Return list rate smic values");
        return listSmicValues;
    }

    private int getMonthOfIncrease(List<List<RateSmicApi>> listsRateSmicGroupByRateSmicValue) {
        List<RateSmicApi> firstListGroupByRateSmicValue = listsRateSmicGroupByRateSmicValue
                .get(0);
        //Obtain last element of the group list and get timePeriod of increase
        String timePeriodLastRateSmicApi = firstListGroupByRateSmicValue.get(firstListGroupByRateSmicValue.size() - 1)
                .getTimePeriod();
        List<String> listStringTimePeriod = Arrays.asList(timePeriodLastRateSmicApi.split("-"));
        int monthOfIncrease = Integer.parseInt(listStringTimePeriod.get(1));

        log.info("Service : Get the month of increase: " + monthOfIncrease);
        return monthOfIncrease;
    }
}

