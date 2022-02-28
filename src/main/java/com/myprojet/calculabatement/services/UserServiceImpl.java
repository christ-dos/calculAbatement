package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.UserAlreadyExistException;
import com.myprojet.calculabatement.exceptions.UserNotFoundException;
import com.myprojet.calculabatement.models.User;
import com.myprojet.calculabatement.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(User user) {
        boolean userExist = userRepository.existsById(user.getEmail());
        if (userExist) {
            log.error("Service: user not found!");
            throw new UserAlreadyExistException("L'utilisateur que vous essayez d'ajouter existe déja!");
        }
        log.debug("Service: User added with email: " + user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        Optional<User> userToUpdate = userRepository.findById(user.getEmail());
        if (!userToUpdate.isPresent()) {
            log.error("Service: User with email: " + user.getEmail() + " not found!");
            throw new UserNotFoundException("L'utilisateur que vous essayez de mettre à jour n'existe pas!");
        }
        userToUpdate.get().setFirstname(user.getFirstname());
        userToUpdate.get().setLastname(user.getLastname());
        userToUpdate.get().setPassword(user.getPassword());

        log.debug("Service: User updated with email: " + user.getEmail());
        return userRepository.save(userToUpdate.get());
    }

    @Override
    public String deleteUserById(String userId){
        userRepository.deleteById(userId);
        log.debug("Service: User deleted with email: " + userId);
        return "L'utilisateur a bien été supprimé!";
    }

    @Override
    public Iterable<User> getAllUsers() {
        log.info("Service: List of Users is displayed!");
        return userRepository.findAll();
    }

    @Override
    public User getUserById(String userId) {
        Optional<User> userFound = userRepository.findById(userId);
        if (!userFound.isPresent()) {
            log.error("Service: User not found with email: " + userId);
            throw new UserNotFoundException("L'utilisateur n'a pas été trouvé!");
        }
        log.info("Service: User found with: " + userId);
        return userFound.get();
    }
}
