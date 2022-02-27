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
    private UserServiceImpl utilisateurService;

    public static void main(String[] args) {
        SpringApplication.run(CalculAbatementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("hello World");
        Iterable<User> utilisateurs = utilisateurService.getAllUsers();
        utilisateurs.forEach(user-> System.out.println(user.getEmail()));
       User  utilisateur = new User("sylvia@mail.fr", "pass", "Dupont", "Sylvia");

       System.out.println(utilisateurService.addUser(utilisateur));


    }
}
