package com.myprojet.calculabatement;

import com.myprojet.calculabatement.configuration.CustomProperties;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.models.RateSmicApi;
import com.myprojet.calculabatement.proxies.RateSmicProxy;
import com.myprojet.calculabatement.repositories.ChildRepository;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import com.myprojet.calculabatement.services.CalculateFoodCompensationService;
import com.myprojet.calculabatement.services.CalculateTaxReliefService;
import com.myprojet.calculabatement.services.TaxableSalarySiblingService;
import com.myprojet.calculabatement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Month;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class CalculAbatementApplication implements CommandLineRunner {
    @Autowired
    private UserService userService;

    @Autowired
    private MonthlyRepository monthlyRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private CalculateTaxReliefService calculateTaxReliefService;

    @Autowired
    private CalculateFoodCompensationService calculateFoodCompensationService;

    @Autowired
    private TaxableSalarySiblingService taxableSalarySiblingService;

    @Autowired
    private CustomProperties customProperties;

    @Autowired
    private RateSmicProxy rateSmicProxy;


    public static void main(String[] args) {
        SpringApplication.run(CalculAbatementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("hello World");
        List<String> months = Arrays.asList("janvier", "septembre", "juin");
        // List<Monthly> monthly= (List<Monthly>) monthlyRepository.findAllByMonth("janvier");

        childRepository.save(new Child(1, "Benoit", "Evan", "14/12/2014", "15/03/2020", "christine@email.fr"));
        childRepository.save(new Child(2, "Benoit", "Alice", "14/12/2014", "15/03/2020", "christine@email.fr"));

        monthlyRepository.save(new Monthly(1, Month.JANUARY, "2022", 650D, 10, 10, 20, 10, 1));
        monthlyRepository.save(new Monthly(2, Month.AUGUST, "2022", 650D, 10, 10, 20, 10.00, 1));
        monthlyRepository.save(new Monthly(3, Month.DECEMBER, "2022", 650D, 10, 10, 20, 10.50, 1));
        monthlyRepository.save(new Monthly(4, Month.MARCH, "2022", 650D, 20, 10, 20, 0D, 2));
        monthlyRepository.save(new Monthly(5, Month.DECEMBER, "2021", 650D, 20, 20, 20, 0, 2));
////       // monthly.forEach(x-> System.out.println(x));
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
        // List<Monthly> montglies = (List<Monthly>) monthlyRepository.findByMonthLessThan(Month.DECEMBER.getValue());
        //Arrays.stream(Month.values()).forEach(x->System.out.println(x));
        // double month = calculateTaxReliefService.calculateTaxReliefByChild(10.15, 10.20, Month.AUGUST, "2022", 1);
        // month.forEach(x->System.out.println("les valeurs: " +x));
        //double totalRepas = calculateFoodCompensationService.calculateFoodCompensationByYearAndByChildId("2022", 1D,0.5,1);
        //totalRepas.forEach(x->System.out.println("les valeurs: " +x));
        double result = taxableSalarySiblingService.calculateTaxableSalarySibling(245.7, 0.7801, 0);
        System.out.println(customProperties.getApiInseeBdmUrl());
        Iterable<RateSmicApi> results = rateSmicProxy.getRateSmicByInseeApi();
        System.out.println(results);
        //  results.forEach(x-> System.out.println(x));


    }
}
