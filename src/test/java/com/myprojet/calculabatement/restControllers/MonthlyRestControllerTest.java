package com.myprojet.calculabatement.restControllers;

import com.myprojet.calculabatement.exceptions.MonthlyAlreadyExistException;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
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

    private Monthly monthlyTest;

    @BeforeEach
    void setupPerTest() {
        monthlyTest = new Monthly(1, Month.JANVIER, "2021",
                500D, 20, 20, 10, 0, 1);
    }

    @Test
    void addMonthlyTest_whenMonthlyNotExistsInDB_thenReturnMonthlyAdded() throws Exception {
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
    void addMonthlyTest_whenMonthlyAlreadyExistInDB_thenReturnErrorMessageAndStatusBadRequest() throws Exception {
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
                .andExpect(jsonPath("$.message", is("Monthly already exits, please process an update!")))
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
    void getMonthsTest() {
    }

    @Test
    void getTaxableSalarySiblingTest() {
    }

    @Test
    void updateMonthlyTest() {
    }

    @Test
    void updateMonthlyTest_whenMonthlyNotExists() {
    }

    @Test
    void deleteMonthlyByIdTest() {
    }
}