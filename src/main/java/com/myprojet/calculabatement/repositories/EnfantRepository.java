package com.myprojet.calculabatement.repositories;

import com.myprojet.calculabatement.models.Enfant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnfantRepository extends CrudRepository<Enfant,Integer> {
}
