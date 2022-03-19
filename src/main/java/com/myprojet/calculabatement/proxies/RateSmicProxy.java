package com.myprojet.calculabatement.proxies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myprojet.calculabatement.configuration.CustomProperties;
import com.myprojet.calculabatement.models.RateSmicApi;
import com.myprojet.calculabatement.models.SeriesSmic;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class RateSmicProxy {
    @Autowired
    private CustomProperties customProperties;

    public List<RateSmicApi> getRateSmicByInseeApi(String year, String monthValue) throws JsonProcessingException {
        String baseApiUrl = customProperties.getApiInseeBdmUrl();
        monthValue = StringUtils.leftPad(monthValue, 2,"0");
        String getRateSmicUrl = baseApiUrl + "/data/SERIES_BDM/000822484?startPeriod=" + year + "-01" + "&endPeriod=" + year + "-" +  monthValue;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[]{(MediaType.APPLICATION_XML)}));
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.set("Authorization", "Bearer 6bc53e08-2e61-3693-b166-19a02350b0c4");
        headers.set("Accept", "application/vnd.sdmx.structurespecificdata+xml;version=2.1");

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                getRateSmicUrl,
                HttpMethod.GET,
                entity,
                String.class
        );
        //conversion Xml en Json
        JSONObject json = XML.toJSONObject(response.getBody());
        String jsonString = json.toString(4);
        //découpage du json pour récupérer uniquement le tableau Obs
        String jsonSubStringObs = "{" + jsonString.substring(jsonString.indexOf("Series") + 100, jsonString.indexOf("LAST_UPDATE") -15 ) + "}";
        //Mapping en objet Java
        SeriesSmic seriesSmic = new ObjectMapper().readValue(jsonSubStringObs, SeriesSmic.class);

        return seriesSmic.getObs();
    }
}
