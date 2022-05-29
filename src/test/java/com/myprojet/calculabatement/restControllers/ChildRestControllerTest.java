package com.myprojet.calculabatement.restControllers;

import com.myprojet.calculabatement.exceptions.ChildNotFoundException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.repositories.ChildRepository;
import com.myprojet.calculabatement.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChildRestController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class ChildRestControllerTest {
    @Autowired
    private MockMvc mockMvcChild;

    @MockBean
    private TaxableSalaryService taxableSalaryService;
    @MockBean
    private TotalAnnualTaxReliefsService totalAnnualTaxReliefsService;
    @MockBean
    private CalculateTaxReliefService calculateTaxReliefService;

    @MockBean
    private ChildServiceImpl childServiceImplMock;

    private Child childTest;

    @BeforeEach
    void setupPerTest() {
        childTest = new Child(15, "Sanchez", "Lea", "12/01/2020", "02/05/2020", "http://image.jpeg", "christine@email.fr");

    }

    @Test
    void getAllChildrenTest_theReturnAnIterableOfChild() throws Exception {
        //GIVEN
        List<Child> children = Arrays.asList(
                new Child(3, "Charton", "Nathan", "14/05/2021", "24/08/2021", LocalDateTime.now(), "http://image.jpeg", "christine@email.fr"),
                new Child(2, "Cacahuette", "Manon", "30/11/2017", "01/03/2020", LocalDateTime.now().minusMinutes(15), "http://image.jpeg", "christine@email.fr"),
                new Child(1, "Riboulet", "Romy", "12/01/2020", "02/05/2020", LocalDateTime.now().minusMinutes(30), "http://image.jpeg", "christine@email.fr")
        );
        //WHEN
        when(childServiceImplMock.getChildrenByUserEmailOrderByDateAddedDesc()).thenReturn(children);
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].firstname", is("Nathan")))
                .andExpect(jsonPath("$.[0].lastname", is("Charton")))
                .andExpect(jsonPath("$.[0].birthDate", is("14/05/2021")))
                .andDo(print());
    }

    @Test
    void getChildByIdTest_whenIdIs15_thenReturnAChildSanchezLea() throws Exception {
        //GIVEN
        //WHEN
        when(childServiceImplMock.getChildById(anyInt())).thenReturn(childTest);
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/find/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is("Lea")))
                .andExpect(jsonPath("$.lastname", is("Sanchez")))
                .andExpect(jsonPath("$.birthDate", is("12/01/2020")))
                .andDo(print());
    }

    @Test
    void getChildByIdTest_whenChildNotFound_thenReturnStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        when(childServiceImplMock.getChildById(anyInt())).thenThrow(new ChildNotFoundException("Child not found!"));
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/find/105"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Child not found!")))
                .andDo(print());
    }

    @Test
    void getAnnualTaxableSalaryByChildTest_() {
    }

    @Test
    void getAnnualReportableAmountsByChildTest_() {
    }

    @Test
    void getAnnualReportableAmountsByChildTest_whenChildHasNoMonthlies() {
    }

    @Test
    void getTaxReliefByChildTest_() {
    }

    @Test
    void getTaxReliefByChildTest_whenChildHasNoMonthlies() {
    }

    @Test
    void addChildTest_() {
    }

    @Test
    void updateChildTest_() {
    }

    @Test
    void deleteChildTest_() {
    }
}