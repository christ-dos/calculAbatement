package com.myprojet.calculabatement.restControllers;

import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.services.MonthlyService;
import com.myprojet.calculabatement.services.TaxableSalaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

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
    public ResponseEntity<?> addMonthly(@RequestBody Monthly monthly) {
        Monthly newMonthly = monthlyService.addMonthly(monthly);
        log.debug("Controller: Monthly added for child with ID: " + monthly.getChildId());
        return new ResponseEntity<>(newMonthly, HttpStatus.CREATED);
    }

    @GetMapping("/all/year/childid")
    public ResponseEntity<Iterable<Monthly>> getAllMonthliesByYearAndChildIdOrderByMonthDesc(@Valid String year, @Valid int childId) {
        Iterable<Monthly> monthlies = monthlyService.getAllMonthlyByYearAndChildIdOrderByMonthDesc(year, childId);
        log.info("Controller: Display list of monthlies by year order by desc");
        return new ResponseEntity<>(monthlies, HttpStatus.OK);
    }

    @GetMapping("/all/childid")
    public ResponseEntity<Iterable<Monthly>> getMonthliesByChildIdOrderByYearDescMonthDesc(@Valid int childId) {
        Iterable<Monthly> monthlies = monthlyService.getMonthliesByChildIdOrderByYearDescMonthDesc(childId);
        log.info("Controller: Display list of monthlies by child ID order year Desc and month by Desc");
        return new ResponseEntity<>(monthlies, HttpStatus.OK);
    }

    @GetMapping("/months")
    public ResponseEntity<List<Month>> getMonths() {
        List<Month> monthsList = Arrays.asList(Month.values());
        return new ResponseEntity<>(monthsList, HttpStatus.OK);

    }

    @GetMapping("/taxablesalarysibling")
    public ResponseEntity<?> getTaxableSalarySibling(Double netSalary, Double netBrutCoefficient, Double maintenanceCost) {
        double taxableSalarySibling = taxableSalaryService.calculateTaxableSalarySiblingByMonth(netSalary, netBrutCoefficient, maintenanceCost);
        log.info("Controller: Display taxable salary sibling");
        return new ResponseEntity<>(taxableSalarySibling, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Monthly> updateMonthly(@RequestBody Monthly monthly) {
        Monthly updateMonthly = monthlyService.updateMonthly(monthly);
        log.debug("Controller: Monthly updated with ID: " + updateMonthly.getMonthlyId());
        return new ResponseEntity<>(updateMonthly, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMonthlyById(@PathVariable("id") int monthlyId) {
        String successMessage  = monthlyService.deleteMonthlyById(monthlyId);
        log.debug("Controller: monthly deleted with ID: " + monthlyId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
