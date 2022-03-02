package com.myprojet.calculabatement.repositories;

import com.myprojet.calculabatement.models.Child;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@ActiveProfiles("test")
@Sql(value = {"/abatementTest.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class ChildRepositoryTest {
    @Autowired
    private ChildRepository childRepositoryTest;

    @Test
    public void findChildByUserEmailTest_ThenReturnListWithTwoChildren() {
        // GIVEN
        List<Child> children = Arrays.asList(new Child(2, "Riboulet", "Manon", "30/11/2017", "01/03/2017", "christine@email.fr"),
                new Child(3, "Thomaset", "Lubin", "14/12/2020", "15/03/2020", "christine@email.fr"),
                new Child(4, "Babar", "Elephant", "12/05/2020", "02/05/2020", "sylvie@email.fr")
        );
        // WHEN
        childRepositoryTest.saveAll(children);
        List<Child> childrenByUserEmail = (List<Child>) childRepositoryTest.findChildrenByUserEmail("christine@email.fr");
        // THEN
        assertEquals(2, childrenByUserEmail.size());
    }

    @Test
    public void saveChildTest_thenReturnChildAdded() {
        //GIVEN
        Child childToSave = new Child(1, "Benoit", "Evan", "14/12/2014", "15/03/2020", "christine@email.fr");
        //WHEN
        Child childResult = childRepositoryTest.save(childToSave);
        //THEN
        assertEquals(1, childResult.getId());
        assertEquals("Benoit", childResult.getLastname());
        assertEquals("Evan", childResult.getFirstname());
    }

    @Test
    public void existByIdTest_whenChildExist_thenReturnTrue(){
        //GIVEN
        Child childTest = new Child(1, "Benoit", "Evan", "14/12/2014", "15/03/2020", "christine@email.fr");
        //WHEN
        Child childResult = childRepositoryTest.save(childTest);
        Boolean childExist = childRepositoryTest.existsById(1);
        //THEN
        assertTrue(childExist);
    }

    @Test
    public void existByIdTest_whenChildNotExist_thenReturnFalse(){
        //GIVEN
        Child childTest = new Child(1, "Benoit", "Evan", "14/12/2014", "15/03/2020", "christine@email.fr");
        //WHEN
        Boolean childExist = childRepositoryTest.existsById(1);
        //THEN
        //verify that child is not present in DB
        assertFalse(childExist);
    }

    @Test
    public void findByIdTest_whenChildExist_thenReturnChildFound(){
        //GIVEN
        Child childTest= new Child(1, "Benoit", "Evan", "14/12/2014", "15/03/2020", "christine@email.fr");
        //WHEN
        Child childResult = childRepositoryTest.save(childTest);
        Optional<Child> childFind = childRepositoryTest.findById(1);
        //THEN
        //verify that child is present in DB
        assertTrue(childFind.isPresent());
    }

    @Test
    public void findByIdTest_whenChildNotExist_thenReturnEmptyOptinal(){
        //GIVEN
        Child childTest = new Child(1, "Benoit", "Evan", "14/12/2014", "15/03/2020", "christine@email.fr");
        //WHEN
        Optional<Child> childFind = childRepositoryTest.findById(1);
        //THEN
        //verify that child is not present in DB
        assertFalse(childFind.isPresent());
    }

    @Test
    public void deleteByIdTest_whenChildExist() {
        Child childTest = new Child(1, "Benoit", "Evan", "14/12/2014", "15/03/2020", "christine@email.fr");
        //WHEN
        Child childResult = childRepositoryTest.save(childTest);
        Optional<Child> childFoundBeforeDeletion = childRepositoryTest.findById(1);
        childRepositoryTest.deleteById(1);
        Optional<Child> childFoundAfterDeletion = childRepositoryTest.findById(1);
        //THEN
        //verify that child exists in DB after adding
        assertTrue(childFoundBeforeDeletion.isPresent());
        //verify that child not exists in DB after deletion
        assertFalse(childFoundAfterDeletion.isPresent());
    }
}
