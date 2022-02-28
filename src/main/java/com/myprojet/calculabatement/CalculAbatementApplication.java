package com.myprojet.calculabatement;

import com.myprojet.calculabatement.models.User;
import com.myprojet.calculabatement.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CalculAbatementApplication implements CommandLineRunner {
    @Autowired
    private UserServiceImpl userService;

    public static void main(String[] args) {
        SpringApplication.run(CalculAbatementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("hello World");
        Iterable<User> users = userService.getAllUsers();
        users.forEach(user-> System.out.println(user.getEmail()));
       User  user = new User("sylvia@mail.fr", "pass", "Dupont", "Sylvia");

       System.out.println(userService.addUser(user));


    }
}
