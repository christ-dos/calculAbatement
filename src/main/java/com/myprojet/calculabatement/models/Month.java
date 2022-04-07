package com.myprojet.calculabatement.models;

public enum Month {
    JANVIER,
    FEVRIER,
    MARS,
    AVRIL,
    MAI,
    JUIN,
    JUILLET,
    AOUT,
    SEPTEMBRE,
    OCTOBRE,
    NOVEMBRE,
    DECEMBRE;

    public int getValue() {
        return ordinal() + 1;
    }

}
