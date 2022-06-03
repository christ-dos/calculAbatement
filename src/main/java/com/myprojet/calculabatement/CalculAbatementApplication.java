package com.myprojet.calculabatement;

import com.myprojet.calculabatement.configuration.CustomProperties;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.proxies.RateSmicProxy;
import com.myprojet.calculabatement.repositories.ChildRepository;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import com.myprojet.calculabatement.repositories.UserRepository;
import com.myprojet.calculabatement.services.*;
import com.myprojet.calculabatement.utils.CalculateAge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.Optional;


@SpringBootApplication
public class CalculAbatementApplication implements CommandLineRunner {
//    @Autowired
//    private UserService userService;

//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private MonthlyRepository monthlyRepository;
//
//    @Autowired
//    private ChildRepository childRepository;
//
//    @Autowired
//    private CalculateTaxReliefServiceImpl calculateTaxReliefService;
//
//    @Autowired
//    private CalculateFoodCompensationService calculateFoodCompensationService;
//
//    @Autowired
//    private TaxableSalaryService taxableSalaryService;
//
//    @Autowired
//    private CustomProperties customProperties;
//
//    @Autowired
//    private RateSmicProxy rateSmicProxy;
//
//    @Autowired
//    private TotalAnnualTaxReliefsServiceImpl totalAnnualTaxReliefsServiceImpl;
//
//    @Autowired
//    private ChildService childService;

//    @Autowired
//     private MonthlyService monthlyService;

    public static void main(String[] args) {
        SpringApplication.run(CalculAbatementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        //todo clean CommandeLineRunner  + property
    }
}
