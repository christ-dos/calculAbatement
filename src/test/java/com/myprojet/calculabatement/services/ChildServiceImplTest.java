package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.ChildAlreadyExistException;
import com.myprojet.calculabatement.exceptions.ChildNotFoundException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.ChildRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class ChildServiceImplTest {
    private ChildServiceImpl childServiceTest;
    @Mock
    private ChildRepository childRepositoryMock;

    @BeforeEach
    public void setPerTest() {
        childServiceTest = new ChildServiceImpl(childRepositoryMock);
    }

    @Test
    public void addChildTest_whenChildNotExist_thenReturnChildAdded() {
        //GIVEN
        Child childNotExist = new Child(550, "Riza", "Lazar", "19/08/2020", "02/05/2020", "http://image.jpeg", "christine@email.fr");
        //WHEN
        when(childRepositoryMock.existsByFirstnameAndLastnameAndBirthDate(anyString(), anyString(), anyString())).thenReturn(false);
        when(childRepositoryMock.save(isA(Child.class))).thenReturn(childNotExist);
        Child childSaved = childServiceTest.addChild(childNotExist);
        //THEN
        assertEquals(550, childSaved.getId());
        assertEquals("Riza", childSaved.getLastname());
        assertEquals("Lazar", childSaved.getFirstname());
        verify(childRepositoryMock, times(1)).save(isA(Child.class));
    }

    @Test
    public void addChildTest_whenChildAlreadyExist_thenThrowChildAlreadyExistException() {
        Child childAlreadyExist = new Child(1, "Riboulet", "Romy", "12/05/2020", "02/05/2020",  "http://image.jpeg", "christine@email.fr");
        //WHEN
        when(childRepositoryMock.existsByFirstnameAndLastnameAndBirthDate(anyString(), anyString(), anyString())).thenReturn(true);
        //THEN
        assertThrows(ChildAlreadyExistException.class, () -> childServiceTest.addChild(childAlreadyExist));
        verify(childRepositoryMock, times(1)).existsByFirstnameAndLastnameAndBirthDate(anyString(), anyString(), anyString());
        verify(childRepositoryMock, times(0)).save(isA(Child.class));
    }

    @Test
    public void updateChildTest_whenChildNotExist_thenThrowChildNotFoundException() {
        //GIVEN
        Child childNotExist = new Child(990, "Martin", "Paul", "12/09/2020", "02/05/2020", "http://image.jpeg", "christine@email.fr");
        //WHEN
        when(childRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());
        //THEN
        assertThrows(ChildNotFoundException.class, () -> childServiceTest.updateChild(childNotExist));
        verify(childRepositoryMock, times(1)).findById(anyInt());
        verify(childRepositoryMock, times(0)).save(isA(Child.class));
    }

    @Test
    public void updateChildTest_whenLastnameAndFirstnameAndBirthDateThatWeChangedAlreadyExistInDB_thenThrowChildAlreadyExistException() {
        //GIVEN
        Child childUpdated = new Child(25, "Martin", "Paul", "12/08/2020", "02/05/2020", "http://image.jpeg", "christine@email.fr");
        List<Child> childFindByFirstnameAndLastnameAndBirthDateList = Arrays.asList(
                new Child(50, "Martin", "Paul", "12/08/2020", "02/05/2020", "http://image.jpeg", "christine@email.fr")
        );
        //WHEN
        when(childRepositoryMock.findById(anyInt())).thenReturn(Optional.of(childUpdated));
        when(childRepositoryMock.findByFirstnameAndLastnameAndBirthDate(anyString(), anyString(), anyString())).thenReturn(Optional.of(childFindByFirstnameAndLastnameAndBirthDateList));
        //THEN
        assertThrows(ChildAlreadyExistException.class, () -> childServiceTest.updateChild(childUpdated));
        verify(childRepositoryMock, times(1)).findByFirstnameAndLastnameAndBirthDate(anyString(), anyString(), anyString());
        verify(childRepositoryMock, times(0)).save(isA(Child.class));
    }

    @Test
    public void updateChildTest_whenLastnameAndBirthDateThatWeChangedAlreadyExistInDBButLastnameIsDifferent_thenReturnChildUpdated() {
        //GIVEN
        Child childToUpdate = new Child(25, "Martin", "Jean", "12/12/2020", "02/05/2020", "http://image.jpeg", "christine@email.fr");
        Child childFindById = new Child(25, "Martin", "Wiliam", "12/12/2020", "02/05/2020", "http://image.jpeg", "christine@email.fr");
        List<Child> emptyListOfChild =  new ArrayList<>();
        //WHEN
        when(childRepositoryMock.findById(anyInt())).thenReturn(Optional.of(childFindById));
        when(childRepositoryMock.findByFirstnameAndLastnameAndBirthDate(anyString(), anyString(), anyString())).thenReturn(Optional.of(emptyListOfChild));
        when(childRepositoryMock.save(isA(Child.class))).thenReturn(childToUpdate);
        Child childUpdatedWithSuccessWhenLastnameAndBirthDateAlreadyExistInDB = childServiceTest.updateChild(childToUpdate)
;        //THEN
       assertEquals(childToUpdate, childUpdatedWithSuccessWhenLastnameAndBirthDateAlreadyExistInDB);
       assertEquals(25, childUpdatedWithSuccessWhenLastnameAndBirthDateAlreadyExistInDB.getId());
       assertEquals("Martin", childUpdatedWithSuccessWhenLastnameAndBirthDateAlreadyExistInDB.getLastname());
       assertEquals("Jean", childUpdatedWithSuccessWhenLastnameAndBirthDateAlreadyExistInDB.getFirstname());
    }


    @Test
    public void updateChildTest_whenChildExist_thenReturnChildUpdated() {
        //GIVEN
        Child childExist = new Child(1, "Riboulet", "Romy", "12/05/2020", "02/05/2020",  "http://image.jpeg", "christine@email.fr");
        Child childExistUpdated = new Child(1, "RibouletUpdated", "Romy", "12/05/2020", "02/05/2020", "http://image.jpeg", "christine@email.fr");
        //WHEN
        when(childRepositoryMock.findById(anyInt())).thenReturn(Optional.of(childExist));
        when(childRepositoryMock.save(isA(Child.class))).thenReturn(childExistUpdated);
        Child childUpdatedSaved = childServiceTest.updateChild(childExistUpdated);
        //THEN
        assertEquals(1, childUpdatedSaved.getId());
        assertEquals("RibouletUpdated", childUpdatedSaved.getLastname());
        assertEquals("Romy", childUpdatedSaved.getFirstname());

        verify(childRepositoryMock, times(1)).save(isA(Child.class));
        verify(childRepositoryMock, times(1)).findById(anyInt());
    }

    @Test
    public void deleteChildByIdTest_thenReturnMessageConfirmationOfDeletion() {
        //GIVEN
        Child childExist = new Child(1, "Riboulet", "Romy", "12/05/2020", "02/05/2020",  "http://image.jpeg", "christine@email.fr");
        //WHEN
        childServiceTest.addChild(childExist);
        doNothing().when(childRepositoryMock).deleteById(childExist.getId());
        String responseDeletionChild = childServiceTest.deleteChildById(childExist.getId());
        //THEN
        assertEquals("L'enfant a été supprimé avec succés!", responseDeletionChild);
        verify(childRepositoryMock, times(1)).deleteById(anyInt());
    }

    @Test
    public void deleteChildByIdTest_whenChildHadMonthliesRecorded_thenReturnMessageConfirmationOfDeletion() {
        //GIVEN
        Child childExistWithMonthlies = new Child(1, "Riboulet", "Romy", "12/05/2020",
                "02/05/2020", "http://image.jpeg", "christine@email.fr",
                Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 650D, 20, 20, 20, 10.0, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 20, 20, 20, 10.5, 1)
                ));
        //WHEN
        childServiceTest.addChild(childExistWithMonthlies);
        doNothing().when(childRepositoryMock).deleteById(childExistWithMonthlies.getId());
        String responseDeletionChild = childServiceTest.deleteChildById(childExistWithMonthlies.getId());
        //THEN
        assertEquals("L'enfant a été supprimé avec succés!", responseDeletionChild);
        verify(childRepositoryMock, times(1)).deleteById(anyInt());
    }

    @Test
    public void getChildrenByUserEmailOrderByDateAddedDescTest_whenListContainThreeElements_thenReturnThreeElements() {
        //GIVEN
        List<Child> children = Arrays.asList(
                new Child(1, "Riboulet", "Romy", "12/01/2020", "02/05/2020",  "http://image.jpeg", "christine@email.fr"),
                new Child(2, "Cacahuette", "Manon", "30/11/2017", "01/03/2020","http://image.jpeg", "christine@email.fr"),
                new Child(3, "Charton", "Nathan", "14/05/2021", "24/08/2021","http://image.jpeg", "christine@email.fr")
        );
        //WHEN
        when(childRepositoryMock.findChildrenByUserEmailOrderByDateAddedDesc(isA(String.class))).thenReturn(children);
        List<Child> childrenResult = (List<Child>) childServiceTest.getChildrenByUserEmailOrderByDateAddedDesc();
        //THEN
        assertEquals(3, childrenResult.size());
        assertEquals(1, childrenResult.get(0).getId());
        assertEquals(3, childrenResult.get(2).getId());
        verify(childRepositoryMock, times(1)).findChildrenByUserEmailOrderByDateAddedDesc(anyString());
    }

    @Test
    public void getChildByIdTest_whenChildExist_thenReturnChildFound() {
        //GIVEN
        Child childExist = new Child(1, "Riboulet", "Romy", "12/05/2020", "02/05/2020",  "http://image.jpeg", "christine@email.fr");
        //WHEN
        when(childRepositoryMock.findById(anyInt())).thenReturn(Optional.of(childExist));
        Child childFound = childServiceTest.getChildById(anyInt());
        //THEN
        assertEquals(1, childFound.getId());
        assertEquals("Riboulet", childFound.getLastname());
        assertEquals("Romy", childFound.getFirstname());
        verify(childRepositoryMock, times(1)).findById(anyInt());
    }

    @Test
    public void getChildByIdTest_whenChildNotExists_thenThrowChildNotFoundException() {
        //GIVEN
        Child childNotExist = new Child(1, "Riboulet", "Romy", "12/05/2020", "02/05/2020", "http://image.jpeg", "christine@email.fr");
        //WHEN
        when(childRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());
        //THEN
        assertThrows(ChildNotFoundException.class, () -> childServiceTest.getChildById(childNotExist.getId()));
        verify(childRepositoryMock, times(1)).findById(anyInt());
    }
}
