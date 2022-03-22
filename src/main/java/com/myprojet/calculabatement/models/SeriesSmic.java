package com.myprojet.calculabatement.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SeriesSmic {
    @JsonProperty("Obs")
    private List<RateSmicApi> obs;
}
