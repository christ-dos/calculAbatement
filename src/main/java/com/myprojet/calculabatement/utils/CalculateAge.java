package com.myprojet.calculabatement.utils;

import com.myprojet.calculabatement.exceptions.BirthdateNotValidException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;


@Slf4j
public class CalculateAge {

    public static int getAge(String birthDate, String year, String month) {
        String dateToCompare = "01/" + month + "/" + year;
        if (birthDate.equals("")) {
            log.error("DateUtils: The birthdate can not be empty");
            throw new BirthdateNotValidException("La date d'anniversaire ne peut être une chaîne vide!");
        }
        LocalDateTime birthDateParse = LocalDateTime.parse(birthDate, DateTimeFormat.forPattern("dd/MM/yyyy"));
        LocalDateTime dateToCompareParse = LocalDateTime.parse(dateToCompare, DateTimeFormat.forPattern("dd/MM/yyyy"));

        if (dateToCompareParse.isBefore(birthDateParse) || birthDateParse.isAfter(LocalDateTime.now())) {
            log.error("DateUtils: The birthdate is after the date to compare or after the current date");
            throw new BirthdateNotValidException("La date d'anniversaire ne peut ni être superieur à la date à comparer ni à la date courante!");
        }
        Years age = Years.yearsBetween(birthDateParse, dateToCompareParse);

        log.debug("DateUtils: Age calculated for birthDate: " + birthDate);
        return age.getYears();
    }
}
