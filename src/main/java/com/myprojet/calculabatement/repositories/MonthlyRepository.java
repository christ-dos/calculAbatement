package com.myprojet.calculabatement.repositories;

import com.myprojet.calculabatement.models.Monthly;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Month;
import java.util.List;

@Repository
public interface MonthlyRepository extends CrudRepository<Monthly, Integer> {

    Iterable<Monthly> findMonthlyByChildId(int childId);

    Iterable<Monthly> findMonthlyByYear(String year);

    Iterable<Monthly> findMonthlyByYearAndChildIdOrderByMonthDesc(String year, int childId);

    Iterable<Monthly> findMonthlyByChildIdOrderByYearDescMonthDesc(int childId); // todo implementer les tests
}
