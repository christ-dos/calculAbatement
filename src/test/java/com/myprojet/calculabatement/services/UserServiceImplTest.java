package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.UserAlreadyExistException;
import com.myprojet.calculabatement.exceptions.UserNotFoundException;
import com.myprojet.calculabatement.models.User;
import com.myprojet.calculabatement.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private UserServiceImpl userServiceTest;
    @Mock
    private UserRepository userRepositoryMock;

    @BeforeEach
    public void setPerTest() {
        userServiceTest = new UserServiceImpl(userRepositoryMock);
    }

    @Test
    public void addUserTest_whenUserNotExist_thenReturnUserAdded() {
        //GIVEN
        User userNotExist = new User("samsam@email.fr", "pass", "Burret", "Samantha");
        //WHEN
        when(userRepositoryMock.existsById(anyString())).thenReturn(false);
        when(userRepositoryMock.save(isA(User.class))).thenReturn(userNotExist);
        User userSaved = userServiceTest.addUser(userNotExist);
        //THEN
        assertEquals("samsam@email.fr", userSaved.getEmail());
        assertEquals("Burret", userSaved.getLastname());
        assertEquals("Samantha", userSaved.getFirstname());
        verify(userRepositoryMock, times(1)).save(isA(User.class));
    }

    @Test
    public void addUserTest_whenUserAlreadyExist_thenThrowUserAlreadyExistException() {
        User userAlreadyExist = new User("christine@email.fr", "pass", "Duarte", "Christine");
        //WHEN
        when(userRepositoryMock.existsById(anyString())).thenReturn(true);
        //THEN
        assertThrows(UserAlreadyExistException.class, () -> userServiceTest.addUser(userAlreadyExist));
        verify(userRepositoryMock, times(1)).existsById(anyString());
        verify(userRepositoryMock, times(0)).save(isA(User.class));
    }

    @Test
    public void updateUserTest_whenUserNotExist_thenThrowUserNotFoundException() {
        //GIVEN
        User userNotExist = new User("samuel@email.fr", "pass", "Barnabet", "Samuel");
        //WHEN
        when(userRepositoryMock.findById(anyString())).thenReturn(Optional.empty());
        //THEN
        assertThrows(UserNotFoundException.class, () -> userServiceTest.updateUser(userNotExist));
        verify(userRepositoryMock, times(1)).findById(anyString());
        verify(userRepositoryMock, times(0)).save(isA(User.class));
    }

    @Test
    public void updateUserTest_whenUserExist_thenReturnUserUpdated() {
        //GIVEN
        User userExist = new User("melinda@email.fr", "pass", "Barquo", "Melinda");
        User userExistUpdated = new User("melinda@email.fr", "passUpdated", "Barquo", "Melinda");
        //WHEN
        when(userRepositoryMock.findById(anyString())).thenReturn(Optional.of(userExist));
        when(userRepositoryMock.save(isA(User.class))).thenReturn(userExistUpdated);
        User userUpdatedSaved = userServiceTest.updateUser(userExistUpdated);
        //THEN
        assertEquals("melinda@email.fr", userUpdatedSaved.getEmail());
        assertEquals("Barquo", userUpdatedSaved.getLastname());
        assertEquals("passUpdated", userUpdatedSaved.getPassword());
        verify(userRepositoryMock, times(1)).save(isA(User.class));
        verify(userRepositoryMock, times(1)).findById(anyString());
    }

    @Test
    public void deleteUserByIdTest_thenReturnUtilisateurSupprime() {
        //GIVEN
        String userId = "filipa@email.fr";
        //WHEN
        doNothing().when(userRepositoryMock).deleteById(userId);
        String responseDeleted = userServiceTest.deleteUserById(userId);
        //THEN
        assertEquals("L'utilisateur a été supprimé avec succes!", responseDeleted);
    }

    @Test
    public void getUserByIdTest_whenUserExists_thenReturnUserWithEmailLola() {
        //GIVEN
        String userId = "lola@email.fr";
        User user = new User("lola@email.fr", "pass", "Sanchez", "Lola");
        //WHEN
        when(userRepositoryMock.findById(anyString())).thenReturn(java.util.Optional.of(user));
        User userTest = userServiceTest.getUserById(userId);
        //THEN
        assertEquals(userId, userTest.getEmail());
        assertEquals(user.getFirstname(), userTest.getFirstname());
        assertEquals(user.getLastname(), userTest.getLastname());
        verify(userRepositoryMock, times(1)).findById(anyString());
    }

    @Test
    public void getAllUsersTest_whenListContainThreeElements_thenReturnThreeElements() {
        //GIVEN
        List<User> users = Arrays.asList(
                new User("lola@email.fr", "pass", "Sanchez", "Lola"),
                new User("christine@email.fr", "pass", "Santos", "Christine"),
                new User("vivi@email.fr", "pass", "Fernandes", "Sylvie")
        );
        //WHEN
        when(userRepositoryMock.findAll()).thenReturn(users);
        List<User> usersResult = (List<User>) userServiceTest.getAllUsers();
        //THEN
        assertTrue(usersResult.size() > 0);
        assertEquals(users, usersResult);
        verify(userRepositoryMock, times(1)).findAll();
    }

    @Test
    public void getUserByIdTest_whenUserNotExists_thenThrowUserNotFoundException() {
        //GIVEN
        String UserIdNotExist = "notexist@email.fr";
        //WHEN
        //THEN
        assertThrows(UserNotFoundException.class, () -> userServiceTest.getUserById(UserIdNotExist));
        verify(userRepositoryMock, times(1)).findById(anyString());
    }
}
