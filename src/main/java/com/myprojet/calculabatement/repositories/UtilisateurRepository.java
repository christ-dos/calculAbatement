package com.myprojet.calculabatement.repositories;

import com.myprojet.calculabatement.models.Utilisateur;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtilisateurRepository extends CrudRepository<Utilisateur,String> {
}
