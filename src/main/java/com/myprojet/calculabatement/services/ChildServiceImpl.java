package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.exceptions.ChildAlreadyExistException;
import com.myprojet.calculabatement.exceptions.ChildNotFoundException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.repositories.ChildRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ChildServiceImpl implements ChildService {
    private ChildRepository childRepository;

    @Autowired
    public ChildServiceImpl(ChildRepository childRepository) {
        this.childRepository = childRepository;
    }

    @Override
    public Child addChild(Child child) {
        boolean childExist = childRepository.existsById(child.getId());
        if (childExist) {
            log.error("Service: Child with ID: "+ child.getId() + " already exists!");
            throw new ChildAlreadyExistException("L'enfant que vous essayez d'ajouter existe déja!");
        }
        log.debug("Service: Child added with email: " + child.getId());
        return childRepository.save(child);
    }

    @Override
    public Child updateChild(Child child) {
        Optional<Child> childToUpdate = childRepository.findById(child.getId());
        if (!childToUpdate.isPresent()) {
            log.error("Service: Child with ID: " + child.getId() + " not found!");
            throw new ChildNotFoundException("L'enfant que vous essayez de mettre à jour n'existe pas!");
        }
        childToUpdate.get().setFirstname(child.getFirstname());
        childToUpdate.get().setLastname(child.getLastname());
        childToUpdate.get().setBeginContract(child.getBeginContract());
        childToUpdate.get().setBirthDate(child.getBirthDate());

        log.debug("Service: Child updated with ID: " + child.getId());
        return childRepository.save(childToUpdate.get());
    }

    @Override
    public String deleteChildById(int childId) {
        childRepository.deleteById(childId);
        log.debug("Service: Child deleted with ID: " + childId);
        return "L'enfant a été supprimé avec succes!";
    }

    @Override
    public Iterable<Child> getChildrenByUserEmail(String userEmail) {
        return childRepository.findChildrenByUserEmail(userEmail);
    }

    @Override
    public Child getChildById(int childId) {
        Optional<Child> childFound = childRepository.findById(childId);
        if (!childFound.isPresent()) {
            log.error("Service: Child not found with ID: " + childId);
            throw new ChildNotFoundException("Service: L'enfant n'a pas été trouvé!");
        }
        log.debug("Service: Child found with: " + childId);
        return childFound.get();
    }
}