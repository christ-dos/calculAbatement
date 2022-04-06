package com.myprojet.calculabatement.restControllers;

import com.myprojet.calculabatement.services.MonthlyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/child")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class MonthlyRestController {
    @Autowired
    private MonthlyService monthlyService;
}
