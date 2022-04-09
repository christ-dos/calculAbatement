package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.models.Child;

public interface ChildService {
    Child addChild(Child child);

    Child updateChild(Child child);

    String deleteChildById(int childId);

    Iterable<Child> getChildrenByUserEmailOrderByBeginContractDesc();

    Child getChildById(int childId);
}
