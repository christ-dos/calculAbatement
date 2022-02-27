package com.myprojet.calculabatement.repositories;

import com.myprojet.calculabatement.models.Mensuelle;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensuelleRepository extends CrudRepository<Mensuelle, Integer> {
}
