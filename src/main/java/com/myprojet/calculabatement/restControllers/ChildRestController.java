package com.myprojet.calculabatement.restControllers;

import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.services.ChildService;
import com.myprojet.calculabatement.services.TaxableSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ChildRestController {
    @Autowired
    private ChildService childService;
    @Autowired
    private TaxableSalaryService taxableSalaryService;


    @GetMapping("/child/all")
    public ResponseEntity<Iterable<Child>> getAllChildren(){
        Iterable<Child> children = childService.getChildrenByUserEmail();
        return new ResponseEntity<>(children, HttpStatus.OK);
    }

    @GetMapping("/child/taxablesalary")
    public ResponseEntity<List<Double>>getAnnualTaxableSalary(int childId, String year){
        List<Double> taxableSalaries = Collections.singletonList(taxableSalaryService.getSumTaxableSalaryByChildAndByYear(year, childId));
        return null;
    }
}
