package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.Child;

public interface TotalAnnualTaxReliefsService {
    double getTotalAnnualReportableAmountsForAllChildren(String year);

    double getTotalAnnualReportableAmountsByChild(Child child, String year);
}
