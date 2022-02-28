package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.UserAlreadyExistException;
import com.myprojet.calculabatement.models.User;
import com.myprojet.calculabatement.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        boolean userExist = userRepository.existsById(user.getEmail());
        if (userExist) {
            throw new UserAlreadyExistException("L'utilisateur que vous essayez d'ajouter existe déja");
        }
        return userRepository.save(user);
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}
