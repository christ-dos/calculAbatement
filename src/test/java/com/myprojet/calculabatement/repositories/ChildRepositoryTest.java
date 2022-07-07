package com.myprojet.calculabatement.repositories;

import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
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

    @Autowired
    private UserRepository userRepositoryTest;


    @BeforeEach
    public void setPerTest() {
        User userSaved = new User("christine@email.fr", "pass", "duarte", "christine");
        userRepositoryTest.save(userSaved);
        User userSylvie = new User("sylvie@email.fr", "pass", "martines", "sylvie");
        userRepositoryTest.save(userSylvie);

        Child childToSave = new Child(1, "riboulet", "romy", "14/12/2020", "15/03/2020", LocalDateTime.now(),"http://image.jpeg", "sylvie@email.fr");
        childRepositoryTest.save(childToSave);

    }

    @Test
    public void findChildByUserEmailOrderByDateAddedDescTest_ThenReturnListWithTwoChildren() {
        // GIVEN
        List<Child> children = Arrays.asList(
                new Child(2, "Riboulet", "Manon", "30/11/2017", "01/03/2017",LocalDateTime.now().minusMinutes(30), "http://image.jpeg", "christine@email.fr"),
                new Child(3, "Thomaset", "Lubin", "14/12/2020", "15/03/2020",LocalDateTime.now().minusMinutes(15), "http://image.jpeg", "christine@email.fr"),
                new Child(4, "Babar", "Elephant", "12/05/2020", "02/05/2020", LocalDateTime.now() ,"http://image.jpeg", "sylvie@email.fr")
        );
        // WHEN
        childRepositoryTest.saveAll(children);
        List<Child> childrenByUserEmail = (List<Child>) childRepositoryTest.findChildrenByUserEmailOrderByDateAddedDesc("christine@email.fr");
        // THEN
        assertEquals(2, childrenByUserEmail.size());
        //order by date added child 2 is the last child  added
        assertEquals(3, childrenByUserEmail.get(0).getId());
        assertEquals("Thomaset", childrenByUserEmail.get(0).getLastname());
        assertEquals("Lubin", childrenByUserEmail.get(0).getFirstname());

    }

    @Test
    public void existsByFirstnameAndLastnameAndBirthDateTest_whenChildAlreadyExist_ThenReturnTrueIfChildExistByFirstnameAndLastnameAndBirthDate() {
       Child childToVerifyIfExist =  new Child(5, "Riboulet", "Manon", "30/11/2017", "01/04/2018", "http://image.jpeg", "christine@email.fr");
        List<Child> children = Arrays.asList(
                new Child(2, "Riboulet", "Manon", "30/11/2017", "01/03/2017",LocalDateTime.now().minusMinutes(30), "http://image.jpeg", "christine@email.fr"),
                new Child(3, "Thomaset", "Lubin", "14/12/2020", "15/03/2020",LocalDateTime.now().minusMinutes(15), "http://image.jpeg", "christine@email.fr"),
                new Child(4, "Babar", "Elephant", "12/05/2020", "02/05/2020", LocalDateTime.now() ,"http://image.jpeg", "sylvie@email.fr")
        );
        // WHEN
        childRepositoryTest.saveAll(children);
        boolean childExistResult = childRepositoryTest.existsByFirstnameAndLastnameAndBirthDate(childToVerifyIfExist.getFirstname(), childToVerifyIfExist.getLastname(),childToVerifyIfExist.getBirthDate());
        //THEN
        assertTrue(childExistResult);
    }

    @Test
    public void existsByFirstnameAndLastnameAndBirthDateTest_whenChildNotExist_ThenReturnFalseIfChildExistByFirstnameAndLastnameAndBirthDate() {
        Child childToVerifyIfExist =  new Child(5, "Riboulet", "Romy", "01/01/2020", "01/04/2018", "http://image.jpeg", "christine@email.fr");
        List<Child> children = Arrays.asList(
                new Child(2, "Riboulet", "Manon", "30/11/2017", "01/03/2017",LocalDateTime.now().minusMinutes(30), "http://image.jpeg", "christine@email.fr"),
                new Child(3, "Thomaset", "Lubin", "14/12/2020", "15/03/2020",LocalDateTime.now().minusMinutes(15), "http://image.jpeg", "christine@email.fr"),
                new Child(4, "Babar", "Elephant", "12/05/2020", "02/05/2020", LocalDateTime.now() ,"http://image.jpeg", "sylvie@email.fr")
        );
        // WHEN
        childRepositoryTest.saveAll(children);
        boolean childExistResult = childRepositoryTest.existsByFirstnameAndLastnameAndBirthDate(childToVerifyIfExist.getFirstname(), childToVerifyIfExist.getLastname(),childToVerifyIfExist.getBirthDate());
        //THEN
        assertFalse(childExistResult);
    }

    @Test
    public void saveChildTest_thenReturnChildAdded() {
        //GIVEN
        Child childToSave = new Child(2, "Benoit", "Evan", "14/12/2014", "15/03/2020",LocalDateTime.now(), "http://image.jpeg", "christine@email.fr");
        //WHEN
        System.out.println("childToSave: " + childToSave);
        Child childResult = childRepositoryTest.save(childToSave);
        //THEN
        assertEquals(2, childResult.getId());
        assertEquals("Benoit", childResult.getLastname());
        assertEquals("Evan", childResult.getFirstname());
    }

    @Test
    public void findByIdTest_whenChildExist_thenReturnChildFound() {
        //GIVEN
        //WHEN
        Optional<Child> childFound = childRepositoryTest.findById(1);
        //THEN
        //verify that child is present in DB
        assertTrue(childFound.isPresent());
    }

    @Test
    public void findByIdTest_whenChildNotExist_thenReturnEmptyOptional() {
        //GIVEN
        Child childTest = new Child(999, "Benoit", "Evan", "14/12/2014", "15/03/2020", "http://image.jpeg", "christine@email.fr");
        //WHEN
        Optional<Child> childFind = childRepositoryTest.findById(999);
        //THEN
        //verify that child is not present in DB
        assertFalse(childFind.isPresent());
    }

    @Test
    public void deleteByIdTest_whenChildExist() {
        //GIVEN
        //WHEN
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
