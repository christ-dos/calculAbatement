package com.myprojet.calculabatement;

import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.models.User;
import com.myprojet.calculabatement.repositories.ChildRepository;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import com.myprojet.calculabatement.services.UserService;
import com.myprojet.calculabatement.services.UserServiceImpl;
import javafx.util.converter.LocalDateStringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SpringBootApplication
public class CalculAbatementApplication implements CommandLineRunner {
    @Autowired
    private UserService userService;

    @Autowired
    private MonthlyRepository monthlyRepository;

    @Autowired
    private ChildRepository childRepository;

    public static void main(String[] args) {
        SpringApplication.run(CalculAbatementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("hello World");
        List<String> months = Arrays.asList("janvier", "septembre" , "juin");
       // List<Monthly> monthly= (List<Monthly>) monthlyRepository.findAllByMonth("janvier");

//       childRepository.save( new Child(1, "Benoit", "Evan", "14/12/2014", "15/03/2020", "christine@email.fr"));
//
//        monthlyRepository.save(new Monthly(1, Month.JANUARY, "2022", 650D, 20, 20, 20, 0, 1));
//        monthlyRepository.save(new Monthly(2, Month.AUGUST, "2022", 650D, 20, 20, 20, 0, 1));
//        monthlyRepository.save(new Monthly(3, Month.DECEMBER, "2022", 650D, 20, 20, 20, 0, 1));
//       // monthly.forEach(x-> System.out.println(x));
        //LocalDateTime date  = LocalDateTime.now();
      //  System.out.println("le mois: " + date.getMonthValue());
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMMM-yyyy");
//
//        String date = "16-janvier-2016";
//
//        LocalDate localDate = LocalDate.parse(date, formatter);
//
//        System.out.println(localDate.getMonth().getValue());  //default, print ISO_LOCAL_DATE
//
//        System.out.println(formatter.format(localDate)); // print formatter date
        System.out.println(Month.JANUARY);


    }
}
