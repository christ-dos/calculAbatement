package com.myprojet.calculabatement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class CalculateAbatementApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CalculateAbatementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //todo clean CommandeLineRunner  + property
    }
}
