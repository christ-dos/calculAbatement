package com.myprojet.calculabatement.restControllers;

import com.myprojet.calculabatement.exceptions.*;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import com.myprojet.calculabatement.services.MonthlyServiceImpl;
import com.myprojet.calculabatement.services.TaxableSalaryService;
import com.myprojet.calculabatement.utils.ConvertObjectToJsonString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MonthlyRestController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class MonthlyRestControllerTest {
    @Autowired
    private MockMvc mockMvcMonthly;

    @MockBean
    private MonthlyServiceImpl monthlyServiceMock;

    @MockBean
    private TaxableSalaryService taxableSalaryServiceMock;

    @MockBean
    private MonthlyRepository monthlyRepositoryMock;

    @MockBean
    private Month monthsEnum;

    private Monthly monthlyTest;

    @BeforeEach
    void setupPerTest() {
        monthlyTest = new Monthly(1, Month.JANVIER, "2021",
                500D, 20, 20, 10, 0, 1);
    }

    @Test
    void addMonthlyTest_whenMonthlyNotExistsInDB_thenReturnMonthlyAdded() throws Exception {
        //GIVEN
        //WHEN
        when(monthlyServiceMock.addMonthly(any(Monthly.class))).thenReturn(monthlyTest);
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.post("/monthly/add")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(monthlyTest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.monthlyId", is(1)))
                .andExpect(jsonPath("$.month", is("JANVIER")))
                .andExpect(jsonPath("$.year", is("2021")))
                .andExpect(jsonPath("$.taxableSalary", is(500D)))
                .andExpect(jsonPath("$.childId", is(1)))
                .andDo(print());
    }

    @Test
    void addMonthlyTest_whenMonthlyByMonthAndYearAlreadyExistInDB_thenReturnErrorMessageAndStatusBadRequest() throws Exception {
        //GIVEN
        //WHEN
        when(monthlyServiceMock.addMonthly(any(Monthly.class))).thenThrow(new MonthlyAlreadyExistException("Monthly already exists, unable to add!"));
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.post("/monthly/add")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(monthlyTest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MonthlyAlreadyExistException))
                .andExpect(result -> assertEquals("Monthly already exists, unable to add!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("Monthly already exists, unable to add!")))
                .andDo(print());
    }

    @Test
    void addMonthlyTest_whenMonthlyYearNotValid_thenReturnErrorMessageAndStatusBadRequest() throws Exception {
        //GIVEN
        //WHEN
        when(monthlyServiceMock.addMonthly(any(Monthly.class))).thenThrow(new YearNotValidException("The year entered must be between 1952 and " + LocalDateTime.now().getYear() + "!"));
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.post("/monthly/add")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(monthlyTest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof YearNotValidException))
                .andExpect(result -> assertEquals("The year entered must be between 1952 and " + LocalDateTime.now().getYear() + "!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("The year entered must be between 1952 and " + LocalDateTime.now().getYear() + "!")))
                .andDo(print());
    }

    @Test
    void addMonthlyTest_whenMonthlyYearIsCurrentYearAndMonthIsGreaterThanCurrentMonth_thenReturnErrorMessageAndStatusBadRequest() throws Exception {
        //GIVEN
        //WHEN
        when(monthlyServiceMock.addMonthly(any(Monthly.class))).thenThrow(new MonthNotValidException("The month entered must be less than " + LocalDateTime.now().getMonth() + "!"));
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.post("/monthly/add")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(monthlyTest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MonthNotValidException))
                .andExpect(result -> assertEquals("The month entered must be less than " + LocalDateTime.now().getMonth() + "!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("The month entered must be less than " + LocalDateTime.now().getMonth() + "!")))
                .andDo(print());
    }

    @Test
    void getAllMonthliesByYearAndChildIdOrderByMonthDescTest_thenDisplayedListOfMonthliesByYearOrderByMonthDesc() throws Exception {
        //GIVEN
        List<Monthly> monthliesOrderByMonthDesc = Arrays.asList(
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 20, 20, 20, 15, 1),
                new Monthly(4, Month.AVRIL, "2022", 650D, 20, 20, 20, 15, 2),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 20, 20, 20, 10.5, 1)
        );
        //WHEN
        when(monthlyServiceMock.getAllMonthlyByYearAndChildIdOrderByMonthDesc(anyString(), anyInt())).thenReturn(monthliesOrderByMonthDesc);
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.get("/monthly/all/year/childid?year=2022&childId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].month", is("DECEMBRE")))
                .andExpect(jsonPath("$.[2].month", is("FEVRIER")))
                .andExpect(jsonPath("$.[0].year", is("2022")))
                .andExpect(jsonPath("$.[2].year", is("2022")))
                .andExpect(jsonPath("$.[0].monthlyId", is(3)))
                .andExpect(jsonPath("$.[2].monthlyId", is(2)))
                .andExpect(jsonPath("$.[0].childId", is(1)))
                .andExpect(jsonPath("$.[1].childId", is(2)))
                .andDo(print());
    }

    @Test
    void getMonthliesByChildIdOrderByYearDescMonthDescTest_thenReturnListOfMonthliesByChildIdOrderByYearDescMonthDesc() throws Exception {
        //GIVEN
        List<Monthly> monthliesOrderByYearDescMonthDesc = Arrays.asList(
                new Monthly(3, Month.DECEMBRE, "2022", 650D, 20, 20, 20, 15, 1),
                new Monthly(4, Month.AVRIL, "2022", 650D, 20, 20, 20, 15, 1),
                new Monthly(2, Month.FEVRIER, "2021", 650D, 20, 20, 20, 10.5, 1)
        );
        //WHEN
        when(monthlyServiceMock.getMonthliesByChildIdOrderByYearDescMonthDesc(anyInt())).thenReturn(monthliesOrderByYearDescMonthDesc);
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.get("/monthly/all/childid?childId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].month", is("DECEMBRE")))
                .andExpect(jsonPath("$.[2].month", is("FEVRIER")))
                .andExpect(jsonPath("$.[0].year", is("2022")))
                .andExpect(jsonPath("$.[2].year", is("2021")))
                .andExpect(jsonPath("$.[0].monthlyId", is(3)))
                .andExpect(jsonPath("$.[2].monthlyId", is(2)))
                .andExpect(jsonPath("$.[0].childId", is(1)))
                .andExpect(jsonPath("$.[1].childId", is(1)))
                .andDo(print());
    }

    @Test
    void getMonthsTest_thenReturnListOfMonths() throws Exception {
        //GIVEN
        List<String> monthsListTest = Arrays.asList(Month.JANVIER.toString(), Month.FEVRIER.toString(), Month.MARS.toString(),
                Month.AVRIL.toString(), Month.MAI.toString(),
                Month.JUIN.toString(), Month.JUILLET.toString(), Month.AOUT.toString(), Month.SEPTEMBRE.toString(), Month.OCTOBRE.toString(), Month.NOVEMBRE.toString(),
                Month.DECEMBRE.toString());
        //WHEN
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.get("/monthly/months"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]", is("JANVIER")))
                .andExpect(jsonPath("$", is(monthsListTest)))
                .andExpect(jsonPath("$.[5]", is("JUIN")))
                .andExpect(jsonPath("$.[11]", is("DECEMBRE")))
                .andDo(print());
    }

    @Test
    void getTaxableSalarySiblingTest_whenNetSalaryEqual500AndNetBrutCoefficientEqual07801AndMaintenanceCostEqual30_thenReturnResult() throws Exception {
        //GIVEN
        //WHEN
        when(taxableSalaryServiceMock.calculateTaxableSalarySiblingByMonth(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(548.26);
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.get("/monthly/taxablesalarysibling?netSalary=500&netBrutCoefficient=0.7801&maintenanceCost=30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(548.26)))
                .andDo(print());
    }

    @Test
    void getTaxableSalarySiblingTest_whenNetBrutCoefficientIsNull_thenReturnErrorMessageAndStatusBadRequest() throws Exception {
        //GIVEN
        //WHEN
        when(taxableSalaryServiceMock.calculateTaxableSalarySiblingByMonth(anyDouble(), anyDouble(), anyDouble()))
                .thenThrow(new NetBrutCoefficientNotNullException("The coefficient of conversion net/brut of the salary can not be null!"));
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.get("/monthly/taxablesalarysibling?netSalary=500&netBrutCoefficient=0D&maintenanceCost=30"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NetBrutCoefficientNotNullException))
                .andExpect(result -> assertEquals("The coefficient of conversion net/brut of the salary can not be null!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("The coefficient of conversion net/brut of the salary can not be null!")))
                .andDo(print());
    }

    @Test
    void updateMonthlyTest_whenTaxableSalaryUpdatedTo550AndDayWorkedUpdatedTo15_thenReturnMonthlyUpdated() throws Exception {
        //GIVEN
        Monthly monthlyToUpdate = new Monthly(1, Month.JANVIER, "2021",
                550D, 20, 20, 15, 0, 1);
        //WHEN
        when(monthlyRepositoryMock.findById(anyInt())).thenReturn(java.util.Optional.ofNullable((monthlyTest)));
        when(monthlyServiceMock.updateMonthly(any(Monthly.class))).thenReturn(monthlyToUpdate);
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.put("/monthly/update")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(monthlyToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyId", is(1)))
                .andExpect(jsonPath("$.taxableSalary", is(550D)))
                .andExpect(jsonPath("$.dayWorked", is(15)))
                .andExpect(jsonPath("$.childId", is(1)))
                .andDo(print());
    }

    @Test
    void updateMonthlyTest_whenMonthlyNotExists_thenReturnErrorMessageAndStatusNotFound() throws Exception {
        //GIVEN
        Monthly monthlyToUpdateNotExist = new Monthly(250, Month.JANVIER, "2021",
                500D, 20, 20, 10, 0, 1);
        //WHEN
        when(monthlyRepositoryMock.findById(anyInt())).thenReturn(java.util.Optional.ofNullable(monthlyTest));
        when(monthlyServiceMock.updateMonthly(any(Monthly.class)))
                .thenThrow(new MonthlyNotFoundException("Monthly not found!"));
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.put("/monthly/update")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(monthlyToUpdateNotExist)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MonthlyNotFoundException))
                .andExpect(result -> assertEquals("Monthly not found!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("Monthly not found!")))
                .andDo(print());
    }

    @Test
    void updateMonthlyTest_whenMonthAndYearUpdatedAlreadyExistInDB_thenReturnErrorMessageAndStatusNotFound() throws Exception {
        //GIVEN
        Monthly monthlyToUpdateAreadyExist = new Monthly(5, Month.JANVIER, "2021",
                500D, 20, 20, 10, 0, 1);
        //WHEN
        when(monthlyRepositoryMock.findById(anyInt())).thenReturn(java.util.Optional.ofNullable(monthlyTest));
        when(monthlyServiceMock.updateMonthly(any(Monthly.class)))
                .thenThrow(new MonthlyAlreadyExistException("The month and year updated already Exists!"));
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.put("/monthly/update")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(monthlyToUpdateAreadyExist)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MonthlyAlreadyExistException))
                .andExpect(result -> assertEquals("The month and year updated already Exists!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("The month and year updated already Exists!")))
                .andDo(print());
    }

    @Test
    void updateMonthlyTest_whenYearIsNotValid_thenReturnErrorMessageAndStatusNotFound() throws Exception {
        //GIVEN
        Monthly monthlyToUpdateHadYearNotValid = new Monthly(5, Month.JANVIER, "1100",
                500D, 20, 20, 10, 0, 1);
        //WHEN
        when(monthlyRepositoryMock.findById(anyInt())).thenReturn(java.util.Optional.ofNullable(monthlyTest));
        when(monthlyServiceMock.updateMonthly(any(Monthly.class)))
                .thenThrow(new YearNotValidException("Year entry is not valid!"));
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.put("/monthly/update")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(monthlyToUpdateHadYearNotValid)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof YearNotValidException))
                .andExpect(result -> assertEquals("Year entry is not valid!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("Year entry is not valid!")))
                .andDo(print());
    }

    @Test
    void updateMonthlyTest_whenMonthlyYearIsCurrentYearAndMonthIsGreaterThanCurrentMonth_thenReturnErrorMessageAndStatusBadRequest() throws Exception {
        //GIVEN
        //WHEN
        when(monthlyServiceMock.updateMonthly(any(Monthly.class))).thenThrow(new MonthNotValidException("The month entered must be less than " + LocalDateTime.now().getMonth() + "!"));
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.put("/monthly/update")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(monthlyTest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MonthNotValidException))
                .andExpect(result -> assertEquals("The month entered must be less than " + LocalDateTime.now().getMonth() + "!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("The month entered must be less than " + LocalDateTime.now().getMonth() + "!")))
                .andDo(print());
    }


    @Test
    void deleteMonthlyByIdTest_thenReturnSuccessMessage() throws Exception {
        //GIVEN
        //WHEN
        when(monthlyServiceMock.deleteMonthlyById(anyInt())).thenReturn("La déclaration mensuelle a été supprimé!");
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.delete("/monthly/delete/1"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}