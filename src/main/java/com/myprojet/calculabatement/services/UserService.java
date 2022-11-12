package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.User;

public interface UserService {
    User addUser(User user);

    Iterable<User> getAllUsers();

    User getUserById(String userId);

    User updateUser(User user);

    String deleteUserById(String userId);
}
