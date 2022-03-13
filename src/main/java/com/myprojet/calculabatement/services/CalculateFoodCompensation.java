package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculateFoodCompensation {
    private MonthlyRepository monthlyRepository;

    @Autowired
    public CalculateFoodCompensation(MonthlyRepository monthlyRepository) {
        this.monthlyRepository = monthlyRepository;
    }

    public double calculateFoodCompensationByYearAndByChildId(String year, double feeLunch, double feeTaste, int childId) {
        List<Monthly> monthliesByYear = (List<Monthly>) monthlyRepository.findMonthlyByYear(year);
        int sumLunchByChildId = monthliesByYear.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .map(Monthly::getLunch)
                .reduce(0, Integer::sum);
        int sumTasteByChildId = monthliesByYear.stream()
                .filter(monthly -> monthly.getChildId() == childId)
                .map(Monthly::getTaste)
                .reduce(0, Integer::sum);

        return (sumLunchByChildId * feeLunch) + (sumTasteByChildId * feeTaste);
    }
}
