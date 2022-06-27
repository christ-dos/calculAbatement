package com.myprojet.calculabatement.repositories;

import com.myprojet.calculabatement.models.Child;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChildRepository extends CrudRepository<Child, Integer> {

    Iterable<Child> findChildrenByUserEmailOrderByDateAddedDesc(String userEmail);

    boolean existsByFirstnameAndLastnameAndBirthDate(String firstname, String lastName, String birthDate);
    //todo implemementer le test
}
