package com.myprojet.calculabatement.restControllers;

import com.myprojet.calculabatement.exceptions.ChildAlreadyExistException;
import com.myprojet.calculabatement.exceptions.MonthlyAlreadyExistException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.services.MonthlyService;
import com.myprojet.calculabatement.services.TaxableSalaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/monthly")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class MonthlyRestController {
    @Autowired
    private MonthlyService monthlyService;
    @Autowired
    private TaxableSalaryService taxableSalaryService;

    @PostMapping("/add")
    public ResponseEntity<Monthly> addMonthly(@RequestBody Monthly monthly) {
        Monthly newMonthly = null;
        try {
            newMonthly = monthlyService.addMonthly(monthly);
        } catch (MonthlyAlreadyExistException e) {
            e.getMessage();
        }
        log.debug("Controller: Monthly added with child ID: " +monthly.getChildId());
        return new ResponseEntity<>(newMonthly, HttpStatus.CREATED);
    }

    @GetMapping("/all/year/childid")
    public ResponseEntity<Iterable<Monthly>> getAllMonthliesByYearAndChildIdOrderByMonthDesc(String year, int childId) {
        Iterable<Monthly> monthlies = monthlyService.getAllMonthlyByYearAndChildIdOrderByMonthDesc(year, childId);
        log.info("Controller: Display list of monthlies by year order by desc");
        return new ResponseEntity<>(monthlies, HttpStatus.OK);
    }

    @GetMapping("/taxablesalarysibling")
    public ResponseEntity<Double> getTaxableSalarySibling(double netSalary, double netBrutCoefficient, double maintenanceCost){
        double taxableSalarySibling = taxableSalaryService.calculateTaxableSalarySiblingByMonth(netSalary, netBrutCoefficient, maintenanceCost);
        log.info("Controller: Display taxable salary sibling");
        return new ResponseEntity<>(taxableSalarySibling, HttpStatus.OK);
    }
}
