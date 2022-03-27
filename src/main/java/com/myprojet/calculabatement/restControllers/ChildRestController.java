package com.myprojet.calculabatement.restControllers;

import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.services.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChildRestController {
    @Autowired
    private ChildService childService;

    public Iterable<Child> getAllChildren(){
        return childService.getChildrenByUserEmail();
    }
}
