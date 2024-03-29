package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.ChildAlreadyExistException;
import com.myprojet.calculabatement.exceptions.ChildNotFoundException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.repositories.ChildRepository;
import com.myprojet.calculabatement.utils.SecurityUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class ChildServiceImpl implements ChildService {
    private ChildRepository childRepository;
    private final static String currentUser = "christine@email.fr";

    @Autowired
    public ChildServiceImpl(ChildRepository childRepository) {
        this.childRepository = childRepository;
    }

    @Override
    public Child addChild(Child child) {
        boolean childExist = childRepository.existsByFirstnameAndLastnameAndBirthDate(child.getFirstname(), child.getLastname(), child.getBirthDate());
        if (childExist) {
            log.error("Service: Child: " + child.getFirstname() + " " + child.getLastname() + " already exists!");
            throw new ChildAlreadyExistException("L'enfant "
                    + child.getFirstname().toUpperCase() + " "
                    + child.getLastname().toUpperCase().toUpperCase()
                    + " né en " + child.getBirthDate()
                    + ", que vous essayez d'ajouter existe déja!");
        }
        child.setUserEmail(SecurityUtilities.getCurrentUser());
        child.setDateAdded(LocalDateTime.now());

        log.debug("Service: Child added for user email: " + child.getId());
        return childRepository.save(child);
    }

    @Override
    public Child updateChild(Child child) {
        Optional<Child> childToUpdate = childRepository.findById(child.getId());
        if (!childToUpdate.isPresent()) {
            log.error("Service: Child with ID: " + child.getId() + " not found!");
            throw new ChildNotFoundException("L'enfant " + child.getFirstname().toUpperCase() + " " + child.getLastname().toUpperCase() + " que vous essayez de mettre à jour n'existe pas!");
        }
        if (!child.getFirstname().equals(childToUpdate.get().getFirstname())
                || !child.getLastname().equals(childToUpdate.get().getLastname())
                || !child.getBirthDate().equals(childToUpdate.get().getBirthDate())) {
            Optional<List<Child>> childAlreadyExistsByFirstNameAndLastnameAndBirthDate =
                    childRepository.findByFirstnameAndLastnameAndBirthDate(child.getFirstname(), child.getLastname(), child.getBirthDate());

            if (childAlreadyExistsByFirstNameAndLastnameAndBirthDate.get().size() > 0) {
                log.error("Service: Child: " + child.getFirstname() + " " + child.getLastname() + " already exists!");
                throw new ChildAlreadyExistException("L'enfant: "
                        + child.getFirstname().toUpperCase() + " "
                        + child.getLastname().toUpperCase()
                        + " né en " + child.getBirthDate()
                        + ", est déjà enregistré dans la base de données!");
            }
        }
        childToUpdate.get().setFirstname(child.getFirstname());
        childToUpdate.get().setLastname(child.getLastname());
        childToUpdate.get().setBeginContract(child.getBeginContract());
        childToUpdate.get().setEndContract(child.getEndContract());
        childToUpdate.get().setFeesLunch(child.getFeesLunch());
        childToUpdate.get().setFeesSnack(child.getFeesSnack());
        childToUpdate.get().setDateAdded(LocalDateTime.now());
        childToUpdate.get().setBirthDate(child.getBirthDate());
        childToUpdate.get().setImageUrl(child.getImageUrl());

        log.debug("Service: Child updated with ID: " + child.getId());
        return childRepository.save(childToUpdate.get());
    }

    @Override
    public String deleteChildById(int childId) {
        childRepository.deleteById(childId);
        log.debug("Service: Child deleted with ID: " + childId);
        return "L'enfant a été supprimé avec succés!";
    }

    @Override
    public Iterable<Child> getChildrenByUserEmailOrderByDateAddedDesc() {
        log.info("Service: list of children by user ordered by date added found!");
        return childRepository.findChildrenByUserEmailOrderByDateAddedDesc(SecurityUtilities.getCurrentUser());
    }

    @Override
    public Child getChildById(int childId) {
        Optional<Child> childFound = childRepository.findById(childId);
        if (!childFound.isPresent()) {
            log.error("Service: Child not found with ID: " + childId);
            throw new ChildNotFoundException("L'enfant avec ID: " + childId + " n'a pas été trouvé!");
        }
        log.debug("Service: Child found with ID: " + childId);
        return childFound.get();
    }
}
