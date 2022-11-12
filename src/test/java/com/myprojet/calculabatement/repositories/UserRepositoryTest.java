package com.myprojet.calculabatement.repositories;

import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@ActiveProfiles("test")
@Sql(value = {"/abatementTest.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepositoryTest;

    private User userSaved;

    @BeforeEach
    public void setPerTest() {
        userSaved = new User("christine@email.fr", "pass", "Duarte", "Christine");
        userRepositoryTest.save(userSaved);
    }

    @Test
    public void existByIdTest_whenUserExist_thenReturnTrue() {
        //GIVEN
        String userId = "christine@email.fr";
        //WHEN
        boolean userExist = userRepositoryTest.existsById(userId);
        //THEN
        assertTrue(userExist);
    }

    @Test
    public void existByIdTest_whenUserNotExist_thenReturnFalse() {
        //GIVEN
        User userNotExist = new User("notExist@email.fr", "pass", "Babar", "Elephant");
        //WHEN
        boolean childNotExist = userRepositoryTest.existsById(userNotExist.getEmail());
        //THEN
        //verify that user is not present in DB
        assertFalse(childNotExist);
    }

    @Test
    public void saveUserTest_thenReturnUserAdded() {
        //GIVEN
        User userToSave = new User("kendra@email.fr", "pass", "Fernandes", "Kendra");
        //WHEN
        User userResult = userRepositoryTest.save(userToSave);
        //THEN
        assertEquals("kendra@email.fr", userResult.getEmail());
        assertEquals("Fernandes", userResult.getLastname());
        assertEquals("Kendra", userResult.getFirstname());
    }

    @Test
    public void findByIdTest_whenUserExist_thenReturnUserFound() {
        //GIVEN
        String userId = "christine@email.fr";
        //WHEN
        Optional<User> userFound = userRepositoryTest.findById(userId);
        //THEN
        //verify that child is present in DB
        assertTrue(userFound.isPresent());
    }

    @Test
    public void findByIdTest_whenUserNotExist_thenReturnEmptyOptional() {
        //GIVEN
        User userNotExist = new User("alain@email.fr", "pass", "Dupont", "Alain");
        //WHEN
        Optional<User> userFind = userRepositoryTest.findById(userNotExist.getEmail());
        //THEN
        //verify that child is not present in DB
        assertFalse(userFind.isPresent());
    }

    @Test
    public void deleteByIdTest_whenUserExist() {
        //GIVEN
        //WHEN
        Optional<User> userFoundBeforeDeletion = userRepositoryTest.findById(userSaved.getEmail());
        userRepositoryTest.deleteById(userSaved.getEmail());
        Optional<User> userFoundAfterDeletion = userRepositoryTest.findById(userSaved.getEmail());
        //THEN
        //verify that child exists in DB after adding
        assertTrue(userFoundBeforeDeletion.isPresent());
        //verify that child not exists in DB after deletion
        assertFalse(userFoundAfterDeletion.isPresent());
    }
}
