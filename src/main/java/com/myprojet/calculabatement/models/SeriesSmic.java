package com.myprojet.calculabatement.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SeriesSmic {
    @JsonProperty("Obs")
    private List<RateSmicApi> obs;
}
