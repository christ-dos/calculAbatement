package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
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

    public List<Monthly> calculateTaxRelief(double rateSmic1, double rateSmic2, String month, String year){
        int daysPresencesForPeriodOne;
        int daysPresencesForPeriodTwo;
       Iterable<Monthly> monthly = (List<Monthly>) monthlyRepository.findAllByMonth(month);


        return (List<Monthly>) monthly;
    }

    private double convertHoursInDays(double hoursWorked){
        return hoursWorked / 8;
    }
}

