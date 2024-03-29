package com.myprojet.calculabatement.proxies;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myprojet.calculabatement.configuration.BeansConfiguration;
import com.myprojet.calculabatement.exceptions.IllegalYearException;
import com.myprojet.calculabatement.exceptions.SmicValueByApiNotFoundException;
import com.myprojet.calculabatement.models.RateSmicApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;


@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = BeansConfiguration.class)
public class RateSmicProxyTest {
    @InjectMocks
    private RateSmicProxy rateSmicProxyTest;

    @Mock
    private WebClient myWebClientForApiInseeMock;

    String myXmlString;

    List<RateSmicApi> rateSmicApiList;

    String baseApiUrl;
    @Mock
    ObjectMapper mapper;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;


    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;


    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;


    @Mock
    private Mono<ResponseEntity<String>> responseEntityMono;

    @BeforeEach
    public void setPerTest() {
        rateSmicProxyTest = new RateSmicProxy(myWebClientForApiInseeMock);
        baseApiUrl = "https://api.insee.fr/series/BDM/V1";
        myXmlString = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<message:StructureSpecificData xmlns:ss=\"http://www.sdmx.org/resources/sdmxml/schemas/v2_1/data/structurespecific\" xmlns:footer=\"http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message/footer\" xmlns:ns1=\"urn:sdmx:org.sdmx.infomodel.datastructure.Dataflow=FR1:SERIES_BDM(1.0):ObsLevelDim:TIME_PERIOD\" xmlns:message=\"http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message\" xmlns:common=\"http://www.sdmx.org/resources/sdmxml/schemas/v2_1/common\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\">\n" +
                "    <message:Header>\n" +
                "        <message:ID>SERIES_BDM_1647874764090</message:ID>\n" +
                "        <message:Test>false</message:Test>\n" +
                "        <message:Prepared>2022-03-21T15:59:24</message:Prepared>\n" +
                "        <message:Sender id=\"FR1\">\n" +
                "            <common:Name xml:lang=\"fr\">Institut national de la statistique et des études économiques</common:Name>\n" +
                "        </message:Sender>\n" +
                "        <message:Structure structureID=\"FR1_SERIES_BDM_1_0\" namespace=\"urn:sdmx:org.sdmx.infomodel.datastructure.Dataflow=FR1:SERIES_BDM(1.0):ObsLevelDim:TIME_PERIOD\" dimensionAtObservation=\"TIME_PERIOD\">\n" +
                "            <common:StructureUsage>\n" +
                "                <Ref agencyID=\"FR1\" id=\"SERIES_BDM\" version=\"1.0\"/>\n" +
                "            </common:StructureUsage>\n" +
                "        </message:Structure>\n" +
                "        <message:Source xml:lang=\"fr\">Banque de données macro-économiques</message:Source>\n" +
                "    </message:Header>\n" +
                "    <message:DataSet ss:dataScope=\"DataStructure\" xsi:type=\"ns1:DataSetType\" ss:structureRef=\"FR1_SERIES_BDM_1_0\">\n" +
                "        <Series IDBANK=\"000822484\" FREQ=\"M\" TITLE_FR=\"Salaire minimum brut interprofessionnel de croissance (en euros par heure)\" TITLE_EN=\"Guaranteed minimum growth wage (in euros per hour)\" LAST_UPDATE=\"2022-03-08\" UNIT_MEASURE=\"EUROS\" UNIT_MULT=\"0\" REF_AREA=\"FE\" DECIMALS=\"2\">\n" +
                "            <Obs TIME_PERIOD=\"2021-03\" OBS_VALUE=\"10.57\" OBS_STATUS=\"A\" OBS_QUAL=\"DEF\" OBS_TYPE=\"A\"/>\n" +
                "            <Obs TIME_PERIOD=\"2021-02\" OBS_VALUE=\"10.57\" OBS_STATUS=\"A\" OBS_QUAL=\"DEF\" OBS_TYPE=\"A\"/>\n" +
                "            <Obs TIME_PERIOD=\"2021-01\" OBS_VALUE=\"10.57\" OBS_STATUS=\"A\" OBS_QUAL=\"DEF\" OBS_TYPE=\"A\"/>\n" +
                "        </Series>\n" +
                "    </message:DataSet>\n" +
                "</message:StructureSpecificData>";

        rateSmicApiList = Arrays.asList(
                new RateSmicApi("2021-03", "10.57"),
                new RateSmicApi("2021-02", "10.57"),
                new RateSmicApi("2021-01", "10.57")
        );
    }

    @Test
    public void getRateSmicByInseeApiTest_WhenRequestIsValid_thenReturnListRateSmicApiObject() {
        //GIVEN
        //WHEN
        ResponseEntity<String> response = new ResponseEntity<>(myXmlString, HttpStatus.OK);

        when(myWebClientForApiInseeMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.toEntity(String.class)).thenReturn(responseEntityMono);
        when(responseEntityMono.retryWhen(any())).thenReturn(responseEntityMono);
        when(responseEntityMono.block()).thenReturn(response);

        List<RateSmicApi> rateSmicApisResult = rateSmicProxyTest.getRateSmicByInseeApi("2022", "03");
        //THEN
        assertTrue(rateSmicApisResult.size() == 3);
        assertEquals(rateSmicApiList.get(0).getTimePeriod(), rateSmicApisResult.get(0).getTimePeriod());
        assertEquals(rateSmicApiList.get(0).getSmicValue(), rateSmicApisResult.get(0).getSmicValue());
        // Mockito.verify(customPropertiesMock, times(1)).getApiInseeBdmUrl();
        verify(myWebClientForApiInseeMock, times(1)).get();
        verify(requestHeadersUriSpecMock, times(1)).uri(anyString());
        verify(requestHeadersSpecMock, times(1)).retrieve();
        verify(responseSpecMock, times(1)).toEntity(String.class);
        verify(responseEntityMono, times(1)).retryWhen(any());
        verify(responseEntityMono, times(1)).block();
    }

    @Test
    public void getRateSmicByInseeApiTest_WhenYearIsNotValid_thenThrowIllegalYearException() {
        //GIVEN
        String year = "2050";
        //WHEN
        //THEN
        assertThrows(IllegalYearException.class, () -> rateSmicProxyTest.getRateSmicByInseeApi(year, "12"));
    }

    @Test
    public void getRateSmicByInseeApiTest_WhenRequestResponseIsNull_thenThrowSmicValueByApiNotFoundException() {
        //GIVEN
        String year = "2022";
        String monthValue = " 12";
        String NoContentFound = null;
        //WHEN
        ResponseEntity<String> response = new ResponseEntity<>(NoContentFound, HttpStatus.NO_CONTENT);
        when(myWebClientForApiInseeMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.toEntity(String.class)).thenReturn(responseEntityMono);
        when(responseEntityMono.retryWhen(any())).thenReturn(responseEntityMono);
        when(responseEntityMono.block()).thenReturn(response);

        //THEN
        assertThrows(SmicValueByApiNotFoundException.class, () -> rateSmicProxyTest.getRateSmicByInseeApi(year, monthValue));
    }

    @Test
    public void getRateSmicByInseeApiTest_WhenObjectMappedIsNull_thenThrowNullPointerException() {
        //GIVEN
        //WHEN
        //THEN
        assertThrows(NullPointerException.class, () -> rateSmicProxyTest.getRateSmicByInseeApi("2022", "13"));
    }


}
