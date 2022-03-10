package com.myprojet.calculabatement.exceptions;

public class MonthlyAlreadyExistException extends RuntimeException{
    public MonthlyAlreadyExistException(String message) {
        super(message);
    }
}
