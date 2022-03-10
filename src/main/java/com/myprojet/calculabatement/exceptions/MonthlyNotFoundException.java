package com.myprojet.calculabatement.exceptions;

public class MonthlyNotFoundException extends RuntimeException{
    public MonthlyNotFoundException(String message) {
        super(message);
    }
}
