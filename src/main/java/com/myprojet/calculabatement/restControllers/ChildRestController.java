package com.myprojet.calculabatement.restControllers;

import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.services.ChildService;
import com.myprojet.calculabatement.services.TaxableSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/child")
@CrossOrigin(origins = "http://localhost:4200")
public class ChildRestController {
    @Autowired
    private ChildService childService;
    @Autowired
    private TaxableSalaryService taxableSalaryService;


    @GetMapping("/all")
    public ResponseEntity<Iterable<Child>> getAllChildren(){
        Iterable<Child> children = childService.getChildrenByUserEmail();
        return new ResponseEntity<>(children, HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Child> getChildById(@PathVariable("id") int childId){
        Child child = childService.getChildById(childId);
        return new ResponseEntity<>(child, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Child> addChild(@RequestBody Child child){
        Child newChild = childService.addChild(child);
        return new ResponseEntity<>(newChild, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Child> updateChild(@RequestBody Child child){
        Child updateChild = childService.updateChild(child);
        return new ResponseEntity<>(updateChild, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteChild(@PathVariable("id") int childId){
        childService.deleteChildById(childId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/taxablesalary")
    public ResponseEntity<List<Double>>getAnnualTaxableSalaryAllChildren(List<Integer>childIds, String year){
        //List<Double> taxableSalaries = Collections.singletonList(taxableSalaryService.getSumTaxableSalaryByChildAndByYear(year, ));
        return null;
    }
}
