package com.myprojet.calculabatement.proxies;

import com.myprojet.calculabatement.configuration.CustomProperties;
import com.myprojet.calculabatement.models.RateSmicApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class RateSmicProxy {
    @Autowired
    private CustomProperties customProperties;

    public Iterable<RateSmicApi> getRateSmicByInseeApi()  {
        String baseApiUrl = customProperties.getApiInseeBdmUrl();
        String getRateSmicUrl = baseApiUrl + "/data/SERIES_BDM/000822484?startPeriod=2021-01&endPeriod=2021-12";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer 6bc53e08-2e61-3693-b166-19a02350b0c4");
        headers.set("Accept", "application/vnd.sdmx.structurespecificdata+xml;version=2.1");

               HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Iterable<RateSmicApi>> response = restTemplate.exchange(
                getRateSmicUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Iterable<RateSmicApi>>(){}
        );


        return response.getBody();
    }
}
