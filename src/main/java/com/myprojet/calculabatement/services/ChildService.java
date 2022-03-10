package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.Child;

public interface ChildService {
    Child addChild(Child child);

    Child updateChild(Child child);

    String deleteChildById(int childId);

    Iterable<Child> getChildrenByUserEmail(String userEmail);

    Child getChildById(int childId);
}
