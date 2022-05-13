package com.myprojet.calculabatement.proxies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myprojet.calculabatement.configuration.CustomProperties;
import com.myprojet.calculabatement.exceptions.IllegalYearException;
import com.myprojet.calculabatement.exceptions.ConversionResponseApiXmlToJsonNullException;
import com.myprojet.calculabatement.models.RateSmicApi;
import com.myprojet.calculabatement.models.SeriesSmic;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class RateSmicProxy {
    private CustomProperties customProperties;

    private RestTemplate restTemplate;

    @Autowired
    public RateSmicProxy(CustomProperties customProperties, RestTemplate restTemplate) {
        this.customProperties = customProperties;
        this.restTemplate = restTemplate;
    }

    public List<RateSmicApi> getRateSmicByInseeApi(String year, String monthValue) {
        if (Integer.parseInt(year) > LocalDate.now().getYear() || Integer.parseInt(year) <= 1951) {
            log.error("Proxy: Invalid year for requête!");
            throw new IllegalYearException("L'année n'est pas valide");
        }
        //add a zero before number of the month to obtain 2 digits
        monthValue = StringUtils.leftPad(monthValue, 2, "0");
        String baseApiUrl = customProperties.getApiInseeBdmUrl();
        String getRateSmicUrl = baseApiUrl + "/data/SERIES_BDM/000822484?startPeriod=" + year + "-01" + "&endPeriod=" + year + "-" + monthValue;

//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_XML);
//        headers.set("Authorization", "Bearer 6bc53e08-2e61-3693-b166-19a02350b0c4");

        ResponseEntity<String> serieSmicResponse = WebClient.create()
                .get()
                .uri(getRateSmicUrl)
                .header("Authorization", "Bearer 6bc53e08-2e61-3693-b166-19a02350b0c4")
                .retrieve()
                .toEntity(String.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(100)))
                .block();

        SeriesSmic seriesSmic = getMappedObjectFromJson(serieSmicResponse);
        if (seriesSmic == null) {
            log.error("Proxy: An Error occurred during the mapping of the object, the variable seriesSmic is null");
            throw new NullPointerException("Erreur lors du mapping de l'objet");
        }
        log.info("Proxy: display list of smic values for year: " + year);
        return seriesSmic.getObs();
    }

    private String conversionResponseApiXmlToJson(ResponseEntity<String> response) {
        //Conversion Xml en Json
        if (response.getBody() == null) {
            log.error("Proxy: No result found for request at Insee Api!");
            throw new ConversionResponseApiXmlToJsonNullException("La requête n'a reçu aucune réponse!");
        }
        JSONObject json = XML.toJSONObject(response.getBody());
        String jsonString = json.toString(4);
        //Découpage du json pour récupérer uniquement le tableau Obs avec les valeurs du smic
        String jsonSubStringObs = "{" + jsonString.substring(jsonString.indexOf("Series") + 100, jsonString.indexOf("LAST_UPDATE") - 15) + "}";
        return jsonSubStringObs;
    }

    private SeriesSmic getMappedObjectFromJson(ResponseEntity<String> response) {
        String jsonSubStringObs = conversionResponseApiXmlToJson(response);
        SeriesSmic seriesSmic = null;
        //Mapping en objet Java
        try {
            seriesSmic = new ObjectMapper().readValue(jsonSubStringObs, SeriesSmic.class);
        } catch (JsonProcessingException e) {
            log.error("RateSmicProxy: error occurred during deserialization !");
        }
        return seriesSmic;
    }
}
