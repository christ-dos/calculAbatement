package com.myprojet.calculabatement.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ConvertObjectToJsonString {

    public static String asJsonString(final Object obj) {
        ObjectMapper objectMapper =
                new ObjectMapper().registerModule(new JavaTimeModule())
                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("The obj does not be writting", e);
        }
    }
}
