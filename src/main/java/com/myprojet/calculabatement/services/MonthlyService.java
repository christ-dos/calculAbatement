package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.Monthly;

public interface MonthlyService {
    Monthly addMonthly(Monthly monthly);

    Monthly updateMonthly(Monthly monthly);

    String deleteMonthlyById(int monthlyId);

    Iterable<Monthly> getAllMonthly();

    Iterable<Monthly> getAllMonthlyByChildId(int childId);

    Monthly getMonthlyById(int monthlyId);
}
