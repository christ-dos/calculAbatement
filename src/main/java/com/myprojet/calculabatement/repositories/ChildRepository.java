package com.myprojet.calculabatement.repositories;

import com.myprojet.calculabatement.models.Child;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildRepository extends CrudRepository<Child,Integer> {
}
