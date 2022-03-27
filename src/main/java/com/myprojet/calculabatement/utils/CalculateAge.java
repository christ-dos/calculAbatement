package com.myprojet.calculabatement.utils;

import com.myprojet.calculabatement.exceptions.NotBirthdateNotValidException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;



@Slf4j
public class CalculateAge {

    public static int getAge(String birthDate) {
        if (birthDate != null) {
            LocalDateTime birthDateParse = LocalDateTime.parse(birthDate, DateTimeFormat.forPattern("dd/MM/yyyy"));
            LocalDateTime currentDate = LocalDateTime.now();

            if (currentDate.isAfter(birthDateParse)) {
                Years age = Years.yearsBetween(birthDateParse, currentDate);
                Months monthOfBirthDate = Months.monthsBetween(birthDateParse, currentDate);

                log.debug("DateUtils: Age calculated for birthDate: " + birthDate);
                return age.getYears();
                //return monthOfBirthDate.getMonths();

            }
        }
        log.error("DateUtils: The birthdate is not valid");
        throw  new NotBirthdateNotValidException("La date d'anniversaire n'est pas valide");
    }
}
