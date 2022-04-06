package com.myprojet.calculabatement.restControllers;

import com.myprojet.calculabatement.exceptions.ChildAlreadyExistException;
import com.myprojet.calculabatement.exceptions.MonthlyAlreadyExistException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.services.MonthlyService;
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

    @PostMapping("/add")
    public ResponseEntity<Monthly> addMonthly(@RequestBody Monthly monthly) {
        Monthly newMonthly = null;
        try {
            newMonthly = monthlyService.addMonthly(monthly);
        } catch (MonthlyAlreadyExistException e) {
            e.getMessage();
        }
        log.info("Controller: Monthly added");
        return new ResponseEntity<>(newMonthly, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<Monthly>> getAllMonthlies() {
        Iterable<Monthly> monthlies = monthlyService.getAllMonthly();
        log.info("Controller: Display list of monthlies");
        return new ResponseEntity<>(monthlies, HttpStatus.OK);
    }
}
