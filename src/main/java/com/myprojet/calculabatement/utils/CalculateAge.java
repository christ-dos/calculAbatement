package com.myprojet.calculabatement.utils;

import com.myprojet.calculabatement.exceptions.BirthdateNotValidException;
import com.myprojet.calculabatement.models.Month;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;



@Slf4j
public class CalculateAge {

    public static int getAge(String birthDate, String year, String month) {
       // month = StringUtils.leftPad(month, 2, "0");
        String dateToCompare = "01/" + month + "/" + year ;
        if (birthDate != null) {
            LocalDateTime birthDateParse = LocalDateTime.parse(birthDate, DateTimeFormat.forPattern("dd/MM/yyyy"));
           // LocalDateTime currentDate = LocalDateTime.now();
            LocalDateTime dateToCompareParse = LocalDateTime.parse(dateToCompare, DateTimeFormat.forPattern("dd/MM/yyyy")); //todo refactor test avec une date a comparer et non plus
                                                                                                                         // la date courante
            if (dateToCompareParse.isAfter(birthDateParse)) {
                Years age = Years.yearsBetween(birthDateParse, dateToCompareParse);

                log.debug("DateUtils: Age calculated for birthDate: " + birthDate);
                return age.getYears();
            }
        }
        log.error("DateUtils: The birthdate is not valid");
        throw  new BirthdateNotValidException("La date d'anniversaire n'est pas valide");
    }
}
