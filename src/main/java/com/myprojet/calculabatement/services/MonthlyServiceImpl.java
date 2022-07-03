package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.MonthNotValidException;
import com.myprojet.calculabatement.exceptions.MonthlyAlreadyExistException;
import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.exceptions.YearNotValidException;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        if (isMonthlyAlreadyExistByMonthAndYear(monthly)) {
            log.error("Service: The monthly that we try to add with ID: " + monthly.getMonthlyId() + " already exists!");
            throw new MonthlyAlreadyExistException("La déclaration mensuelle pour: "
                    + monthly.getMonth() + " "
                    + monthly.getYear()
                    + ", que vous essayez d'ajouter existe déja!");
        }
        if (isYearNotValid(monthly.getYear())) {
            throw new YearNotValidException("L'année saisie doit être comprise entre 1952 et "
                    + LocalDateTime.now().getYear() + " !");
        }
        if(isMonthNotValid(monthly.getMonth().getValue()) && Integer.parseInt(monthly.getYear()) == LocalDateTime.now().getYear()){
            int numberOfCurrentMonth = LocalDateTime.now().getMonth().getValue();
            Month currentMonth = Month.convertIntToStringOfMonth(numberOfCurrentMonth);
            throw new MonthNotValidException("Le mois saisie doit être inférieur à "
                    + currentMonth + "!"); // todo implementer integration
        }
        log.debug("Service: Monthly added to children ID: " + monthly.getChildId());
        return monthlyRepository.save(monthly);
    }

    @Override
    public Monthly updateMonthly(Monthly monthly) {
        Optional<Monthly> monthlyToUpdate = monthlyRepository.findById(monthly.getMonthlyId());
        if (!monthlyToUpdate.isPresent()) {
            log.error("Service: Monthly with ID: " + monthly.getMonthlyId() + " not found!");
            throw new MonthlyNotFoundException("La déclaration mensuelle " + monthly.getMonth() + " " + monthly.getYear() + " n'existe pas!");
        }
        if (!monthly.getMonth().equals(monthlyToUpdate.get().getMonth()) || !monthly.getYear().equals(monthlyToUpdate.get().getYear())) {
            if (isMonthlyAlreadyExistByMonthAndYear(monthly)) {
                log.error("Service: Monthly with month: " + monthly.getMonth() + " already exist for year: " + monthly.getYear());
                throw new MonthlyAlreadyExistException("Le mois: " + monthly.getMonth()
                        + " existe déja pour l'année: " + monthly.getYear() + " !");
            }
            if (isYearNotValid(monthly.getYear())) {
                throw new YearNotValidException("L'année saisie doit être comprise entre 1952 et " + LocalDateTime.now().getYear() + " !");
            }
            if(isMonthNotValid(monthly.getMonth().getValue()) && Integer.parseInt(monthly.getYear()) == LocalDateTime.now().getYear()){
                int numberOfCurrentMonth = LocalDateTime.now().getMonth().getValue();
                Month currentMonth = Month.convertIntToStringOfMonth(numberOfCurrentMonth);
                throw new MonthNotValidException("Le mois saisie doit être inférieur à "
                        + currentMonth + "!"); // todo implementer et integration
            }
            monthlyToUpdate.get().setMonth(monthly.getMonth());
            monthlyToUpdate.get().setYear(monthly.getYear());
        }
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
    public Iterable<Monthly> getAllMonthlyByYearAndChildIdOrderByMonthDesc(String year, int childId) {
        return monthlyRepository.findMonthlyByYearAndChildIdOrderByMonthDesc(year, childId);
    }

    @Override
    public Iterable<Monthly> getMonthliesByChildIdOrderByYearDescMonthDesc(int childId) {
        return monthlyRepository.findMonthlyByChildIdOrderByYearDescMonthDesc(childId);
    }

    @Override
    public Monthly getMonthlyById(int monthlyId) {
        Optional<Monthly> monthlyFound = monthlyRepository.findById(monthlyId);
        if (!monthlyFound.isPresent()) {
            log.error("Service: Monthly not found with ID: " + monthlyId);
            throw new MonthlyNotFoundException("La déclaration mensuelle avec ID: " + monthlyId + " n'existe pas!");
        }
        log.info("Service: Monthly found with ID : " + monthlyId);
        return monthlyFound.get();
    }

    private boolean isYearNotValid(String year) {
        int yearNumber = Integer.parseInt(year);
        int currentYear = LocalDateTime.now().getYear();
        if (yearNumber < 1952 || yearNumber > currentYear) {
            return true;
        }
        return false;
    }

    private boolean isMonthNotValid(int month) {
        int currentMonth = LocalDateTime.now().getMonthValue();
        if (month > currentMonth) {
            return true;
        }
        return false;
    }

    private boolean isMonthlyAlreadyExistByMonthAndYear(Monthly monthly) {
        List<Monthly> monthliesByYearAndChildId =
                (List<Monthly>) monthlyRepository.findMonthlyByYearAndChildIdOrderByMonthDesc(monthly.getYear(), monthly.getChildId());

        return monthliesByYearAndChildId.stream().anyMatch(x -> x.getMonth().equals(monthly.getMonth()));
    }
}
