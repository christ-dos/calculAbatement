package com.myprojet.calculabatement.repositories;

import com.myprojet.calculabatement.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
}
