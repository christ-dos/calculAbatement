package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.Child;

public interface TotalAnnualTaxReliefsService {
    double getTotalAnnualReportableAmountsForAllChildren(String year);

    double getTotalAnnualReportableAmountsByChild(Child child, String year);

    //double getTotalAnnualReportableAmountsByChild(int childId, String year, double feeLunch, double feeTaste); //todo clean code
}
