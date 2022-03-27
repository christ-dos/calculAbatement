package com.myprojet.calculabatement.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class SecurityUtilities {

    public static String getCurrentUser(){
        String currentUser = "christine@email.fr";
        return currentUser;
    }
}
