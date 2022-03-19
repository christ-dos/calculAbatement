package com.myprojet.calculabatement.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RateSmicApi {

    @JsonProperty("TIME_PERIOD")
    private String timePeriod;
    @JsonProperty("OBS_VALUE")
    private String smicValue;
    @JsonProperty("OBS_STATUS")
    @JsonIgnoreProperties
    private String obsStatus;
    @JsonProperty("OBS_QUAL")
    private String obsQual;
    @JsonProperty("OBS_TYPE")
    private String obsType;

    public RateSmicApi(String timePeriod, String smicValue) {
        this.timePeriod = timePeriod;
        this.smicValue = smicValue;
    }

    @Override
    public String toString() {
        return "RateSmicApi{" +
                "timePeriod='" + timePeriod + '\'' +
                ", smicValue='" + smicValue + '\'' +
                '}';
    }
}

