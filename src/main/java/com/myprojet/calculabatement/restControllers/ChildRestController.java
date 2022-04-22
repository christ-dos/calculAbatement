package com.myprojet.calculabatement.restControllers;

import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.services.ChildService;
import com.myprojet.calculabatement.services.TaxableSalaryService;
import com.myprojet.calculabatement.services.TotalAnnualTaxReliefsService;
import com.myprojet.calculabatement.utils.SecurityUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/child")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class ChildRestController {
    @Autowired
    private ChildService childService;
    @Autowired
    private TaxableSalaryService taxableSalaryService;
    @Autowired
    private TotalAnnualTaxReliefsService totalAnnualTaxReliefsService;

    @GetMapping("/all")
    public ResponseEntity<Iterable<Child>> getAllChildren() {
        Iterable<Child> children = childService.getChildrenByUserEmailOrderByDateAddedDesc();
        log.info("Controller: Display list of children");
        return new ResponseEntity<>(children, HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Child> getChildById(@PathVariable("id") int childId) {
        Child child = childService.getChildById(childId);
        log.debug("Controller: Find child by ID: " + childId);
        return new ResponseEntity<>(child, HttpStatus.OK);
    }

    @GetMapping("/taxablesalary")
    public ResponseEntity<Double> getAnnualTaxableSalaryByChild(@RequestParam int childId, @RequestParam String year) {
        double taxableSalary = taxableSalaryService.getSumTaxableSalaryByChildAndByYear(year, childId);
        log.info("Controller: Taxable salary got for child ID: " + childId + " Value: " + taxableSalary);
        return new ResponseEntity<>(taxableSalary, HttpStatus.OK);
    }

    @GetMapping("/reportableamounts")
    public ResponseEntity<Double> getAnnualReportableAmountsByChild(@RequestParam int childId, @RequestParam String year) {
        Child child = childService.getChildById(childId);
        double reportableAmounts = totalAnnualTaxReliefsService.getTotalAnnualReportableAmountsByChild(child, year);

        log.info("Controller: Reportable amounts got for child ID: " + child.getId() + " Value: " + reportableAmounts);
        return new ResponseEntity<>(reportableAmounts, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Child> addChild(@RequestBody Child child) {
        Child newChild = childService.addChild(child);
        log.info("Controller: Child added");
        return new ResponseEntity<>(newChild, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Child> updateChild(@RequestBody Child child) {
        child.setUserEmail(SecurityUtilities.getCurrentUser());
        Child updateChild = childService.updateChild(child);
        log.debug("Controller: Child updated with ID: " + updateChild.getId());
        return new ResponseEntity<>(updateChild, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteChild(@PathVariable("id") int childId) {
        childService.deleteChildById(childId);
        log.debug("Controller: Child deleted with ID: " + childId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
