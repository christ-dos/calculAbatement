package com.myprojet.calculabatement.services;

public interface TotalAnnualTaxReliefsService {
    double getTotalAnnualReportableAmountsForAllChildren(String year, double feeLunch, double feeTaste);

    double getTotalAnnualReportableAmountsByChild(int childId, String year, double feeLunch, double feeTaste);
}
