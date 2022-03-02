package com.myprojet.calculabatement.repositories;

import com.myprojet.calculabatement.models.Monthly;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyRepository extends CrudRepository<Monthly, Integer> {

    Iterable<Monthly>findMonthlyByChildId(int childId);
}
