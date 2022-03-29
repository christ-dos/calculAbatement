package com.myprojet.calculabatement.repositories;

import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@ActiveProfiles("test")
@Sql(value = {"/abatementTest.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class MonthlyRepositoryTest {
    @Autowired
    private MonthlyRepository monthlyRepositoryTest;

    @Autowired
    private ChildRepository childRepositoryTest;

    @Autowired
    private UserRepository userRepositoryTest;

    @BeforeEach
    public void setPerTest() {
        User userSaved = new User("christine@email.fr", "pass", "Duarte", "Christine");
        userRepositoryTest.save(userSaved);

        Child childSaved = new Child(1, "Benoit", "Evan", "14/12/2014", "15/03/2020", "http://image.jpeg","christine@email.fr");
        childRepositoryTest.save(childSaved);

        Monthly monthlyTest = new Monthly(1, Month.JANUARY, "2022", 650D, 20, 20, 20, 0, 1);
        monthlyRepositoryTest.save(monthlyTest);
    }

    @Test
    public void saveMonthlyTest_thenReturnMonthlyAdded() {
        //GIVEN
        Monthly monthlyToSave = new Monthly(2, Month.JANUARY, "2022", 650D, 20, 20, 20, 0, 1);
        //WHEN
        Monthly monthlyResult = monthlyRepositoryTest.save(monthlyToSave);
        //THEN
        assertEquals(2, monthlyResult.getMonthlyId());
        assertEquals(Month.JANUARY, monthlyResult.getMonth());
        assertEquals("2022", monthlyResult.getYear());
        assertEquals(1, monthlyResult.getChildId());
    }

    @Test
    public void existByIdTest_whenMonthlyExist_thenReturnTrue() {
        //GIVEN
        //WHEN
        boolean monthlyExist = monthlyRepositoryTest.existsById(1);
        //THEN
        assertTrue(monthlyExist);
    }

    @Test
    public void existByIdTest_whenMonthlyNotExist_thenReturnFalse() {
        //GIVEN
        Monthly monthlyTest = new Monthly(999, Month.JANUARY, "2022", 650D, 20, 20, 20, 0, 1);
        //WHEN
        boolean monthlyNotExist = monthlyRepositoryTest.existsById(999);
        //THEN
        //verify that monthly is not present in DB
        assertFalse(monthlyNotExist);
    }

    @Test
    public void deleteByIdTest_whenMonthlyExist() {
        //WHEN
        Optional<Monthly> monthlyFoundBeforeDeletion = monthlyRepositoryTest.findById(1);
        monthlyRepositoryTest.deleteById(1);
        Optional<Monthly> monthlyFoundAfterDeletion = monthlyRepositoryTest.findById(1);
        //THEN
        //verify that monthly exists in DB after adding
        assertTrue(monthlyFoundBeforeDeletion.isPresent());
        //verify that monthly  not exists in DB after deletion
        assertFalse(monthlyFoundAfterDeletion.isPresent());
    }

    @Test
    public void findAllTest_whenMonthliesContainThreeElements_thenReturnIterableWithThreeElements() {
        //GIVEN
        List<Monthly> monthlies = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 20, 20, 20, 0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 20, 20, 0, 1),
                new Monthly(3, Month.MARCH, "2022", 650D, 20, 20, 20, 0, 2)
        );
        Child childSavedIdTwo = new Child(2, "Benoit", "Evan", "14/12/2014", "15/03/2020","http://image.jpeg", "christine@email.fr");
        //WHEN
        childRepositoryTest.save(childSavedIdTwo);
        monthlyRepositoryTest.saveAll(monthlies);
        List<Monthly> monthliesResult = (List<Monthly>) monthlyRepositoryTest.findAll();
        //THEN
        assertEquals(3, monthliesResult.size());
        assertEquals(1, monthliesResult.get(0).getMonthlyId());
        assertEquals(3, monthliesResult.get(2).getMonthlyId());
        assertEquals(1, monthliesResult.get(0).getChildId());
        assertEquals(2, monthliesResult.get(2).getChildId());
    }

    @Test
    public void findMonthlyByChildIdTest_whenMonthliesContainThreeElements_thenReturnIterableWithTwoElementsWithChildIdOne() {
        //GIVEN
        List<Monthly> monthlies = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2022", 650D, 20, 20, 20, 0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 20, 20, 0, 1),
                new Monthly(3, Month.MARCH, "2022", 650D, 20, 20, 20, 0, 2)
        );
        Child childSavedIdTwo = new Child(2, "Benoit", "Evan", "14/12/2014", "15/03/2020","http://image.jpeg", "christine@email.fr");
        //WHEN
        childRepositoryTest.save(childSavedIdTwo);
        monthlyRepositoryTest.saveAll(monthlies);
        List<Monthly> monthliesResult = (List<Monthly>) monthlyRepositoryTest.findMonthlyByChildId(1);
        //THEN
        assertEquals(2, monthliesResult.size());
        assertEquals(1, monthliesResult.get(0).getMonthlyId());
        assertEquals(2, monthliesResult.get(1).getMonthlyId());
        assertEquals(1, monthliesResult.get(0).getChildId());
        assertEquals(1, monthliesResult.get(1).getChildId());
    }

    @Test
    public void findMonthlyByYearTest_whenMonthliesContainThreeElements_thenReturnIterableWithTwoElementsWithYear2022() {
        //GIVEN
        List<Monthly> monthlies = Arrays.asList(
                new Monthly(1, Month.JANUARY, "2021", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(2, Month.FEBRUARY, "2022", 650D, 20, 20, 20, 10.5, 1),
                new Monthly(3, Month.MARCH, "2022", 650D, 20, 20, 20, 15, 1)
        );
        //WHEN
        monthlyRepositoryTest.saveAll(monthlies);
        List<Monthly> monthliesResult = (List<Monthly>) monthlyRepositoryTest.findMonthlyByYear("2022");
        //THEN
        assertEquals(2, monthliesResult.size());
        assertEquals(2, monthliesResult.get(0).getMonthlyId());
        assertEquals(3, monthliesResult.get(1).getMonthlyId());
        assertEquals("2022", monthliesResult.get(0).getYear());
        assertEquals("2022", monthliesResult.get(1).getYear());
    }

    @Test
    public void findByIdTest_whenMonthlyExist_thenReturnMonthlyFound() {
        //GIVEN
        //WHEN
        Optional<Monthly> monthlyFound = monthlyRepositoryTest.findById(1);
        //THEN
        //verify that monthly is present in DB
        assertTrue(monthlyFound.isPresent());
    }

    @Test
    public void findByIdTest_whenMonthlyNotExist_thenReturnEmptyOptional() {
        //GIVEN
        Monthly monthlyTest = new Monthly(999, Month.JANUARY, "2022", 650D, 20, 20, 20, 0, 1);
        //WHEN
        Optional<Monthly> monthlyFound = monthlyRepositoryTest.findById(999);
        //THEN
        //verify that monthly is present in DB
        assertFalse(monthlyFound.isPresent());
    }
}
