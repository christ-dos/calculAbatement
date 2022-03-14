package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.ChildAlreadyExistException;
import com.myprojet.calculabatement.exceptions.ChildNotFoundException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.repositories.ChildRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        Child childNotExist = new Child(550, "Riza", "Lazar", "19/08/2020", "02/05/2020", "christine@email.fr");
        //WHEN
        when(childRepositoryMock.existsById(anyInt())).thenReturn(false);
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
        Child childAlreadyExist = new Child(1, "Riboulet", "Romy", "12/05/2020", "02/05/2020", "christine@email.fr");
        //WHEN
        when(childRepositoryMock.existsById(anyInt())).thenReturn(true);
        //THEN
        assertThrows(ChildAlreadyExistException.class, () -> childServiceTest.addChild(childAlreadyExist));
        verify(childRepositoryMock, times(1)).existsById(anyInt());
        verify(childRepositoryMock, times(0)).save(isA(Child.class));
    }

    @Test
    public void updateChildTest_whenChildNotExist_thenThrowChildNotFoundException() {
        //GIVEN
        Child childNotExist = new Child(990, "Martin", "Paul", "12/09/2020", "02/05/2020", "christine@email.fr");
        //WHEN
        when(childRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());
        //THEN
        assertThrows(ChildNotFoundException.class, () -> childServiceTest.updateChild(childNotExist));
        verify(childRepositoryMock, times(1)).findById(anyInt());
        verify(childRepositoryMock, times(0)).save(isA(Child.class));
    }

    @Test
    public void updateChildTest_whenChildExist_thenReturnChildUpdated() {
        //GIVEN
        Child childExist = new Child(1, "Riboulet", "Romy", "12/05/2020", "02/05/2020", "christine@email.fr");
        Child childExistUpdated = new Child(1, "RibouletUpdated", "Romy", "12/05/2020", "02/05/2020", "christine@email.fr");
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
        int childId = 1;
        //WHEN
        doNothing().when(childRepositoryMock).deleteById(childId);
        String responseDeletionChild = childServiceTest.deleteChildById(childId);
        //THEN
        assertEquals("L'enfant a été supprimé avec succes!", responseDeletionChild);
        verify(childRepositoryMock, times(1)).deleteById(anyInt());
    }

    @Test
    public void getChildrenByUserEmailTest_whenListContainThreeElements_thenReturnThreeElements() {
        //GIVEN
        List<Child> children = Arrays.asList(
                new Child(1, "Riboulet", "Romy", "12/01/2020", "02/05/2020", "christine@email.fr"),
                new Child(2, "Cacahuette", "Manon", "30/11/2017", "01/03/2020", "christine@email.fr"),
                new Child(3, "Charton", "Nathan", "14/05/2021", "24/08/2021", "christine@email.fr")
        );
        //WHEN
        when(childRepositoryMock.findChildrenByUserEmail(isA(String.class))).thenReturn(children);
        List<Child> childrenResult = (List<Child>) childServiceTest.getChildrenByUserEmail("christine@email.fr");
        //THEN
        assertEquals(3, childrenResult.size());
        assertEquals(1, childrenResult.get(0).getId());
        assertEquals(3, childrenResult.get(2).getId());
        verify(childRepositoryMock, times(1)).findChildrenByUserEmail(anyString());
    }

    @Test
    public void getChildByIdTest_whenChildExist_thenReturnChildFound() {
        //GIVEN
        Child childExist = new Child(1, "Riboulet", "Romy", "12/05/2020", "02/05/2020", "christine@email.fr");
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
        Child childNotExist = new Child(1, "Riboulet", "Romy", "12/05/2020", "02/05/2020", "christine@email.fr");
        //WHEN
        when(childRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());
        //THEN
        assertThrows(ChildNotFoundException.class, () -> childServiceTest.getChildById(childNotExist.getId()));
        verify(childRepositoryMock, times(1)).findById(anyInt());
    }
}