package com.myprojet.calculabatement.exceptions;

public class ChildAlreadyExistException extends RuntimeException {
    public ChildAlreadyExistException(String message) {
        super(message);
    }
}
