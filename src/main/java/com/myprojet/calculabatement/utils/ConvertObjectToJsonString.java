package com.myprojet.calculabatement.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConvertObjectToJsonString {

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("The obj does not be writting", e);
        }
    }
}
