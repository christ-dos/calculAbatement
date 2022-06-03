package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.MonthlyAlreadyExistException;
import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MonthlyServiceImpl implements MonthlyService {
    private MonthlyRepository monthlyRepository;

    @Autowired
    public MonthlyServiceImpl(MonthlyRepository monthlyRepository) {
        this.monthlyRepository = monthlyRepository;
    }

    @Override
    public Monthly addMonthly(Monthly monthly) {
        boolean monthlyExists = monthlyRepository.existsById(monthly.getMonthlyId());
          if (monthlyExists || isMonthlyAlreadyExist(monthly)) {
            log.error("Service: The monthly that we try to add with ID: " + monthly.getMonthlyId() + " already exists!");
            throw new MonthlyAlreadyExistException("La déclaration mensuelle que vous essayez d'ajouter existe déja!");
        }
        log.debug("Service: Monthly added to children ID: " + monthly.getChildId());
        return monthlyRepository.save(monthly);
    }

    private boolean isMonthlyAlreadyExist(Monthly monthly){
        List <Monthly> monthliesByYearAndChildId = (List<Monthly>) monthlyRepository.findMonthlyByYearAndChildIdOrderByMonthDesc(monthly.getYear(), monthly.getChildId());
        boolean answer = monthliesByYearAndChildId.stream().anyMatch(x->x.getMonth().equals(monthly.getMonth()));
        System.out.println("reponse: " + answer);
        return answer;
    }

    @Override
    public Monthly updateMonthly(Monthly monthly) {
        Optional<Monthly> monthlyToUpdate = monthlyRepository.findById(monthly.getMonthlyId());
        if (!monthlyToUpdate.isPresent()) {
            log.error("Service: Monthly with ID: " + monthly.getMonthlyId() + " not found!");
            throw new MonthlyNotFoundException("La déclaration mensuelle que vous essayez de mettre à jour, n'existe pas!");
        }
        monthlyToUpdate.get().setMonth(monthly.getMonth());
        monthlyToUpdate.get().setYear(monthly.getYear());
        monthlyToUpdate.get().setDayWorked(monthly.getDayWorked());
        monthlyToUpdate.get().setHoursWorked(monthly.getHoursWorked());
        monthlyToUpdate.get().setLunch(monthly.getLunch());
        monthlyToUpdate.get().setSnack(monthly.getSnack());
        monthlyToUpdate.get().setTaxableSalary(monthly.getTaxableSalary());

        log.debug("Service: Monthly updated with ID: " + monthly.getMonthlyId());
        return monthlyRepository.save(monthlyToUpdate.get());
    }

    @Override
    public String deleteMonthlyById(int monthlyId) {
        monthlyRepository.deleteById(monthlyId);
        log.debug("Service: Monthly deleted with ID: " + monthlyId);
        return "La déclaration mensuelle a été supprimé avec succés!";
    }

    @Override
    public Iterable<Monthly> getAllMonthly() {
        log.info("Service: List of all Monthlies is displayed!");
        return monthlyRepository.findAll();
    }

    @Override
    public Iterable<Monthly> getAllMonthlyByChildId(int childId) {
        log.info("Service: List of Monthly by childId displayed!");
        return monthlyRepository.findMonthlyByChildId(childId);
    }

    @Override
    public Iterable<Monthly> getAllMonthlyByYearAndChildIdOrderByMonthDesc(String year, int childId){ // todo implement test in repository and monthlyServiceImpl
        return monthlyRepository.findMonthlyByYearAndChildIdOrderByMonthDesc(year, childId);
    }

    @Override
    public Iterable<Monthly> getMonthliesByChildIdOrderByYearDescMonthDesc(int childId){
        return monthlyRepository.findMonthlyByChildIdOrderByYearDescMonthDesc(childId);
    }

    @Override
    public Monthly getMonthlyById(int monthlyId) {

        Optional<Monthly> monthlyFound = monthlyRepository.findById(monthlyId);
        if (!monthlyFound.isPresent()) {
            log.error("Service: Monthly not found with ID: " + monthlyId);
            throw new MonthlyNotFoundException(("La déclaration mensuelle n'existe pas!"));
        }
        log.info("Service: Monthly found with ID : " + monthlyId);
        return monthlyFound.get();
    }
}
