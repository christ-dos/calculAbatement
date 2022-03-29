package com.myprojet.calculabatement;

import com.myprojet.calculabatement.configuration.CustomProperties;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.proxies.RateSmicProxy;
import com.myprojet.calculabatement.repositories.ChildRepository;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import com.myprojet.calculabatement.repositories.UserRepository;
import com.myprojet.calculabatement.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CalculAbatementApplication implements CommandLineRunner {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

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

    @Autowired
    private TotalAnnualTaxReliefsService totalAnnualTaxReliefsService;

    @Autowired
    private ChildService childService;


    public static void main(String[] args) {
        SpringApplication.run(CalculAbatementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        // List<String> months = Arrays.asList("janvier", "septembre", "juin");
        // List<Monthly> monthly= (List<Monthly>) monthlyRepository.findAllByMonth("janvier");
//        userRepository.save(new User("sylvie@email.fr", "pass", "Fernandes", "Sylvie"));
        childRepository.save(new Child(1, "Benoit", "Evan", "24/05/2021", "24/05/2021", "https://www.hdwallpaper.nu/wp-content/uploads/2015/03/New-Baby-Photos-Hd.jpg", "christine@email.fr"));
        childRepository.save(new Child(2, "Bernard", "Alice", "20/02/2021", "24/05/2020", "https://www.hdwallpaper.nu/wp-content/uploads/2015/03/Cute-Babies-HD-Wallpapers.jpg", "christine@email.fr"));
        childRepository.save(new Child(3, "Dupuis", "Romane", "24/05/2019", "24/05/2020", "https://www.hdwallpaper.nu/wp-content/uploads/2015/03/Cute-Babies_Hd-Wallpapers.jpg", "christine@email.fr"));
////
//        monthlyRepository.save(new Monthly(1, Month.JANUARY, "2022", 500D, 1, 1, 10, 10.0, 1));
//        monthlyRepository.save(new Monthly(2, Month.AUGUST, "2022", 500D, 1, 1, 10, 10.0, 2));
//        monthlyRepository.save(new Monthly(3, Month.FEBRUARY, "2022", 500D, 1, 1, 10, 10.00, 1));
//        monthlyRepository.save(new Monthly(4, Month.MARCH, "2022", 500D, 1, 1, 10, 10D, 2));
//        monthlyRepository.save(new Monthly(5, Month.DECEMBER, "2022", 500D, 1, 1, 10, 10.0, 2));
//        monthlyRepository.save(new Monthly(6, Month.MAY, "2022", 500D, 1, 1, 10, 10.0, 2));
//////       // monthly.forEach(x-> System.out.println(x));
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
//        double result = taxableSalarySiblingService.calculateTaxableSalarySibling(245.7, 0.7801, 0);
//        System.out.println(customProperties.getApiInseeBdmUrl());
//        double results = calculateTaxReliefService.calculateTaxReliefByChild("2021", 1);
//       // Arrays.stream(results).forEach(x->System.out.println(x));
//       System.out.println(results);
        //results.forEach(x-> System.out.println(x));
//        double total = totalAnnualTaxReliefsService.getTotalAnnualReportableAmounts("2022", 1.00, 0.50);
//       // double totalfood = totalAnnualTaxReliefsService.getTotalAnnualReliefs("2021", 1.00, 0.50);
//        System.out.println("total " + total);
//       // System.out.println("totalfood " + totalfood);
        //User user = userService.getUserById("christine@email.fr");
        // System.out.println("age: " + CalculateAge.getAge("20/03/2021"));
        // result.forEach(x -> System.out.println(x));
        //  System.out.println(result);
    }
}
