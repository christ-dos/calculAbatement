package com.myprojet.calculabatement.utils;

import com.myprojet.calculabatement.exceptions.BirthdateNotValidException;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CalculateAgeTest {
    @Test
    public void testGetAge_whenBirthDateIsAStringTheLastMonthlySavedWasInFebruary2022_thenReturnAgeEqualOneYearOld() {
        // GIVEN
        String birthDate = "09/05/2020";
        // WHEN
        int resultAge = CalculateAge.getAge(birthDate,"2022", "02");
        // THEN
        assertEquals(1, resultAge);
    }

    @Test
    public void testGetAge_whenBirthDateIsAStringTheLastMonthlySavedWasInDecember2022AndChildWasBornInJanuary2022_thenReturnAge() {
        // GIVEN
        String birthDate = "09/01/2022";
        // WHEN
        int resultAge = CalculateAge.getAge(birthDate,"2022", "12");
        // THEN
        assertEquals(0, resultAge);
    }

    @Test
    public void testGetAge_whenBirthDateIsEmpty_thenThrowAnIllegalArgumentException() {
        // GIVEN
        String birthDate = "";
        // WHEN
        // THEN
        assertThrows(BirthdateNotValidException.class, () -> CalculateAge.getAge(birthDate, "2022", "02"));
    }

    @Test
    public void testGetAge_whenDateToCompareIsBeforeBirthDate_thenThrowABirthdateNotValidException() {
        // GIVEN
        String birthDate = "09/01/2022";
        // WHEN
        // THEN
        assertThrows(BirthdateNotValidException.class, () -> CalculateAge.getAge(birthDate, "2021", "02"));
    }

    @Test
    public void testGetAge_whenBirthDateIsAfterCurrentDate_thenThrowABirthdateNotValidException() {
        // GIVEN
        String birthDate = "09/01/2050";
        // WHEN
        // THEN
        assertThrows(BirthdateNotValidException.class, () -> CalculateAge.getAge(birthDate, "2021", "02"));
    }

}
