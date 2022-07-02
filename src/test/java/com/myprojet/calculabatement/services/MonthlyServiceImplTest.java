package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.MonthlyAlreadyExistException;
import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.exceptions.YearNotValidException;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.myprojet.calculabatement.models.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class MonthlyServiceImplTest {

    private MonthlyServiceImpl monthlyServiceTest;

    @Mock
    private MonthlyRepository monthlyRepositoryMock;


    @BeforeEach
    public void setPerTest() {
        monthlyServiceTest = new MonthlyServiceImpl(monthlyRepositoryMock);
    }

    @Test
    public void addMonthlyTest_whenMonthlyNotExist_thenReturnChildAdded() {
        //GIVEN
        Monthly monthlyNotExist = new Monthly(500, Month.JANVIER, "2022", 650D, 20, 20, 20, 10.0, 1);
        //WHEN
        when(monthlyRepositoryMock.save(isA(Monthly.class))).thenReturn(monthlyNotExist);
        Monthly monthlyadded = monthlyServiceTest.addMonthly(monthlyNotExist);
        //THEN
        assertEquals(500, monthlyadded.getMonthlyId());
        assertEquals(Month.JANVIER, monthlyadded.getMonth());
        assertEquals("2022", monthlyadded.getYear());
        assertEquals(650D, monthlyadded.getTaxableSalary());
        assertEquals(1, monthlyadded.getChildId());

        verify(monthlyRepositoryMock, times(1)).save(isA(Monthly.class));
    }

    @Test
    public void addMonthlyTest_whenMonthlyAlreadyExistByMonthAndByYear_thenThrowMonthlyAlreadyExistException() {
        //GIVEN
        List<Monthly> monthliesByChildIdAndByYear = Arrays.asList(
                new Monthly(1, Month.DECEMBRE, "2022", 620D, 18, 18, 20, 8, 1),
                new Monthly(2, Month.JANVIER, "2022", 620D, 18, 18, 20, 8, 1),
                new Monthly(3, Month.FEVRIER, "2022", 620D, 18, 18, 20, 8, 1)
        );
        Monthly monthlyAlreadyExistByMonthAndByYear = new Monthly(250, Month.DECEMBRE, "2022", 680D, 18, 18, 20, 8, 1);
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByYearAndChildIdOrderByMonthDesc(anyString(),anyInt())).thenReturn(monthliesByChildIdAndByYear);
        //THEN
        assertThrows(MonthlyAlreadyExistException.class, () -> monthlyServiceTest.addMonthly(monthlyAlreadyExistByMonthAndByYear));
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYearAndChildIdOrderByMonthDesc(anyString(), anyInt());
        verify(monthlyRepositoryMock, times(0)).save(isA(Monthly.class));
    }

    @Test
    public void addMonthlyTest_whenYearIsNotValidAndLessThan1952_thenThrowYearNotValidException() {
        //GIVEN
        Monthly monthlyWithYearNotValid = new Monthly(250, Month.DECEMBRE, "1900", 680D, 18, 18, 20, 8, 1);
        //WHEN
        //THEN
        assertThrows(YearNotValidException.class, () -> monthlyServiceTest.addMonthly(monthlyWithYearNotValid));
        verify(monthlyRepositoryMock, times(0)).save(isA(Monthly.class));
    }

    @Test
    public void addMonthlyTest_whenYearIsNotValidAndGreaterThanCurrentYear_thenThrowYearNotValidException() {
        //GIVEN
        Monthly monthlyWithYearNotValid = new Monthly(250, Month.DECEMBRE, "2090", 680D, 18, 18, 20, 8, 1);
        //WHEN
        //THEN
        assertThrows(YearNotValidException.class, () -> monthlyServiceTest.addMonthly(monthlyWithYearNotValid));
        verify(monthlyRepositoryMock, times(0)).save(isA(Monthly.class));
    }

    @Test
    public void updateMonthlyTest_whenMonthlyNotExist_thenThrowMonthlyNotFoundException() {
        Monthly monthlyNotExist = new Monthly(990, Month.JANVIER, "2023", 620D, 18, 18, 20, 8, 1);
        //WHEN
        when(monthlyRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());
        //THEN
        assertThrows(MonthlyNotFoundException.class, () -> monthlyServiceTest.updateMonthly(monthlyNotExist));
        verify(monthlyRepositoryMock, times(1)).findById(anyInt());
        verify(monthlyRepositoryMock, times(0)).save(isA(Monthly.class));
    }

    @Test
    public void updateMonthlyTest_whenMonthlyExist_thenReturnMonthlyUpdated() {
        Monthly monthlyExist = new Monthly(1, Month.JANVIER, "2022", 620D, 18, 18, 20, 8, 1);
        Monthly monthlyExistUpdated = new Monthly(1, Month.FEVRIER, "2022", 650D, 18, 18, 20, 8, 1);
        //WHEN
        when(monthlyRepositoryMock.findById(anyInt())).thenReturn(Optional.of(monthlyExist));
        when(monthlyRepositoryMock.save(isA(Monthly.class))).thenReturn(monthlyExistUpdated);
        Monthly monthlyUpdatedSaved = monthlyServiceTest.updateMonthly(monthlyExistUpdated);
        //THEN
        assertEquals(1, monthlyUpdatedSaved.getMonthlyId());
        assertEquals(Month.FEVRIER, monthlyUpdatedSaved.getMonth());
        assertEquals(650D, monthlyUpdatedSaved.getTaxableSalary());

        verify(monthlyRepositoryMock, times(1)).save(isA(Monthly.class));
        verify(monthlyRepositoryMock, times(1)).findById(anyInt());
    }

    @Test
    public void updateMonthlyTest_whenYearIsNotValidAndLessThan1952_thenThrowYearNotValidException() {
        //GIVEN
        Monthly monthlyExist = new Monthly(1, Month.JANVIER, "2022", 620D, 18, 18, 20, 8, 1);
        Monthly monthlyWithYearNotValid = new Monthly(1, Month.JANVIER, "1900", 620D, 18, 18, 20, 8, 1);
        //WHEN
        when(monthlyRepositoryMock.findById(anyInt())).thenReturn(Optional.of(monthlyExist));
        //THEN
        assertThrows(YearNotValidException.class, () -> monthlyServiceTest.updateMonthly(monthlyWithYearNotValid));
        verify(monthlyRepositoryMock, times(0)).save(isA(Monthly.class));
    }

    @Test
    public void updateMonthlyTest_whenYearIsNotValidAndGreaterThanCurrentYear_thenThrowYearNotValidException() {
        //GIVEN
        Monthly monthlyExist = new Monthly(1, Month.JANVIER, "2022", 620D, 18, 18, 20, 8, 1);
        Monthly monthlyWithYearNotValid = new Monthly(1, Month.JANVIER, "2099", 620D, 18, 18, 20, 8, 1);
        //WHEN
        when(monthlyRepositoryMock.findById(anyInt())).thenReturn(Optional.of(monthlyExist));
        //THEN
        assertThrows(YearNotValidException.class, () -> monthlyServiceTest.updateMonthly(monthlyWithYearNotValid));
        verify(monthlyRepositoryMock, times(0)).save(isA(Monthly.class));
    }

    @Test
    public void updateMonthlyTest_whenMonthlyAlreadyExistByMonthAndByYear_thenThrowMonthlyAlreadyExistException() {
        //GIVEN
        List<Monthly> monthliesByChildIdAndByYear = Arrays.asList(
                new Monthly(1, Month.DECEMBRE, "2022", 620D, 18, 18, 20, 8, 1),
                new Monthly(2, Month.JANVIER, "2022", 620D, 18, 18, 20, 8, 1),
                new Monthly(3, Month.FEVRIER, "2022", 620D, 18, 18, 20, 8, 1)
        );
        Monthly monthlyFound = new Monthly(250, Month.OCTOBRE, "2022", 680D, 18, 18, 20, 8, 1);
        Monthly monthlyUpdatedAlreadyExistByMonthAndByYear = new Monthly(250, Month.DECEMBRE, "2022", 680D, 18, 18, 20, 8, 1);
        //WHEN
        when(monthlyRepositoryMock.findById(anyInt())).thenReturn(Optional.of(monthlyFound));
        when(monthlyRepositoryMock.findMonthlyByYearAndChildIdOrderByMonthDesc(anyString(),anyInt())).thenReturn(monthliesByChildIdAndByYear);
        //THEN
        assertThrows(MonthlyAlreadyExistException.class, () -> monthlyServiceTest.updateMonthly(monthlyUpdatedAlreadyExistByMonthAndByYear));
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYearAndChildIdOrderByMonthDesc(anyString(), anyInt());
        verify(monthlyRepositoryMock, times(0)).save(isA(Monthly.class));
    }



    @Test
    public void deleteMonthlyByIdTest_thenReturnMessageConfirmationOfDeletion() {
        //GIVEN
        int monthlyId = 1;
        //WHEN
        doNothing().when(monthlyRepositoryMock).deleteById(monthlyId);
        String responseDeletionMonthly = monthlyServiceTest.deleteMonthlyById(monthlyId);
        //THEN
        assertEquals("La déclaration mensuelle a été supprimé avec succés!", responseDeletionMonthly);
        verify(monthlyRepositoryMock, times(1)).deleteById(anyInt());

    }

    @Test
    public void getAllMonthlyTest_whenListContainThreeElements_thenReturnTheeElementsOfMonthly() {
        //GIVEN
        List<Monthly> allMonthlies = Arrays.asList(
                new Monthly(1, Month.DECEMBRE, "2021", 620D, 18, 18, 20, 8, 1),
                new Monthly(2, Month.JANVIER, "2022", 620D, 18, 18, 20, 8, 1),
                new Monthly(3, Month.FEVRIER, "2022", 620D, 18, 18, 20, 8, 2)
        );
        //WHEN
        when(monthlyRepositoryMock.findAll()).thenReturn(allMonthlies);
        List<Monthly> monthliesResult = (List<Monthly>) monthlyServiceTest.getAllMonthly();
        //THEN
        assertEquals(3, monthliesResult.size());
        verify(monthlyRepositoryMock, times(1)).findAll();
    }

    @Test
    public void getAllMonthlyByChildIdTest_whenListContainThreeElements_thenReturnTheeElementsOfMonthly() {
        List<Monthly> MonthliesByChildId = Arrays.asList(
                new Monthly(1, Month.DECEMBRE, "2021", 620D, 18, 18, 20, 8, 1),
                new Monthly(2, Month.JANVIER, "2022", 620D, 18, 18, 20, 8, 1),
                new Monthly(3, Month.FEVRIER, "2022", 620D, 18, 18, 20, 8, 1)
        );
        //WHEN
        when(monthlyRepositoryMock.findMonthlyByChildId(anyInt())).thenReturn(MonthliesByChildId);
        List<Monthly> monthliesByChildIdResult = (List<Monthly>) monthlyServiceTest.getAllMonthlyByChildId(1);
        //THEN
        assertEquals(3, monthliesByChildIdResult.size());

        verify(monthlyRepositoryMock, times(1)).findMonthlyByChildId(anyInt());
    }

    @Test
    public void getMonthlyByYearAndChildIdOrderByMonthDescTest_whenListContainThreeElements_thenReturnTheeElementsOfMonthlyOrderedByMonthDesc() {
        //GIVEN
        List<Monthly> MonthliesByYear = Arrays.asList(
                new Monthly(1, Month.DECEMBRE, "2021", 620D, 18, 18, 20, 8, 1),
                new Monthly(2, Month.JANVIER, "2022", 620D, 18, 18, 20, 8, 1),
                new Monthly(3, Month.DECEMBRE, "2022", 620D, 18, 18, 20, 8, 1)
        );
        when(monthlyRepositoryMock.findMonthlyByYearAndChildIdOrderByMonthDesc(anyString(), anyInt())).thenReturn(MonthliesByYear);
        List<Monthly> monthliesByYearResult = (List<Monthly>) monthlyServiceTest.getAllMonthlyByYearAndChildIdOrderByMonthDesc("2022", 1);
        //THEN
        assertEquals(3, monthliesByYearResult.size());
        //ordered by Month desc DECEMBRE is after JANVIER
        assertEquals(Month.DECEMBRE, monthliesByYearResult.get(0).getMonth());
        assertEquals(Month.JANVIER, monthliesByYearResult.get(1).getMonth());
        verify(monthlyRepositoryMock, times(1)).findMonthlyByYearAndChildIdOrderByMonthDesc(anyString(), anyInt());
    }

    @Test
    public void getMonthlyByChildIdTest_whenMonthlyExist_thenReturnMonthlyFound() {
        //GIVEN
        Monthly monthlyExist = new Monthly(1, Month.JANVIER, "2022", 620D, 18, 18, 20, 8, 1);
        //WHEN
        when(monthlyRepositoryMock.findById(anyInt())).thenReturn(Optional.of(monthlyExist));
        Monthly monthlyFound = monthlyServiceTest.getMonthlyById(anyInt());
        //THEN
        assertEquals(1, monthlyFound.getMonthlyId());
        assertEquals(Month.JANVIER, monthlyFound.getMonth());
        assertEquals("2022", monthlyFound.getYear());
        assertEquals(1, monthlyFound.getChildId());
        verify(monthlyRepositoryMock, times(1)).findById(anyInt());
    }

    @Test
    public void getMonthlyByChildIdTest_whenMonthlyNotExist_thenThrowMonthlyNotFoundException() {
        //GIVEN
        Monthly monthlyNotExist = new Monthly(900, Month.JANVIER, "2022", 620D, 18, 18, 20, 8, 1);
        //WHEN
        when(monthlyRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());
        //THEN
        assertThrows(MonthlyNotFoundException.class, () -> monthlyServiceTest.getMonthlyById(anyInt()));
        verify(monthlyRepositoryMock, times(1)).findById(anyInt());
    }
}
