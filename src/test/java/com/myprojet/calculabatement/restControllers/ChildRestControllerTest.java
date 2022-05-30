package com.myprojet.calculabatement.restControllers;

import com.myprojet.calculabatement.exceptions.ChildAlreadyExistException;
import com.myprojet.calculabatement.exceptions.ChildNotFoundException;
import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.ChildRepository;
import com.myprojet.calculabatement.services.*;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    private TaxableSalaryService taxableSalaryServiceMock;
    @MockBean
    private TotalAnnualTaxReliefsService totalAnnualTaxReliefsServiceMock;
    @MockBean
    private CalculateTaxReliefService calculateTaxReliefServiceMock;

    @MockBean
    private ChildServiceImpl childServiceImplMock;

    private Child childTest;

    @BeforeEach
    void setupPerTest() {
        childTest = new Child(
                15, "Sanchez", "Lea", "12/01/2020", "02/05/2020", "http://image.jpeg", "christine@email.fr",
                Arrays.asList(
                        new Monthly(1, Month.JANVIER, "2021", 500D, 20, 20, 10, 0, 1),
                        new Monthly(2, Month.FEVRIER, "2021", 500D, 20, 20, 10, 0, 1),
                        new Monthly(3, Month.MARS, "2021", 500D, 20, 20, 10, 0, 2)
                ));
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
    void getAnnualTaxableSalaryByChildTest_whenChildHadMonthliesSaved_thenReturnResponseEntityWithTheResultAndStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        when(childServiceImplMock.getChildById(anyInt())).thenReturn(childTest);
        when(totalAnnualTaxReliefsServiceMock.getTotalAnnualReportableAmountsByChild(
                any(Child.class),anyString())).thenReturn(637.50);
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/reportableamounts?childId=15&year=2021"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(637.5)))
                .andDo(print());
    }

    @Test
    void getAnnualReportableAmountsByChildTest_whenChildHasMonthliesSavedForYearRequested_thenReturnResponseEntityWithAnnualReportableAmounts() throws Exception {
        //GIVEN
        //WHEN
        when(childServiceImplMock.getChildById(anyInt())).thenReturn(childTest);
        when(totalAnnualTaxReliefsServiceMock.getTotalAnnualReportableAmountsByChild(
                any(Child.class),anyString())).thenReturn(637.50);
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/reportableamounts?childId=15&year=2021"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(637.5)))
                .andDo(print());
    }

    @Test
    void getAnnualReportableAmountsByChildTest_whenChildHasNoMonthliesForYearRequested_thenReturnResponseEntityWithErrorMessageAndStatus404() throws Exception {
        //GIVEN
        Child childHadNoMonthlies = new Child(
                18, "Bernard", "Shanna", "12/01/2020",
                "02/05/2020", "http://image.jpeg", "christine@email.fr");
        //WHEN
        when(childServiceImplMock.getChildById(anyInt())).thenReturn(childHadNoMonthlies);
        when(totalAnnualTaxReliefsServiceMock.getTotalAnnualReportableAmountsByChild(
                any(Child.class),anyString())).thenThrow(new MonthlyNotFoundException("Monthly not found!"));
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/reportableamounts?childId=18&year=2021"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Monthly not found!")))
                .andDo(print());
    }

    @Test
    void getTaxReliefByChildTest_whenChildHasMonthliesSavedForYearRequested_thenReturnResponseEntityWithTaxReliefCalculated() throws Exception {
        //GIVEN
        //WHEN
        when(calculateTaxReliefServiceMock.calculateTaxReliefByChild(anyString(),anyInt())).thenReturn(920D);
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/taxrelief?childId=18&year=2021"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(920D)))
                .andDo(print());
    }

    @Test
    void getTaxReliefByChildTest_whenChildHasNoMonthlies_thenReturnResponseEntityWithErrorMessageAnsStatus404() throws Exception {
        //GIVEN
         //WHEN
        when(calculateTaxReliefServiceMock.calculateTaxReliefByChild(anyString(),anyInt())).thenThrow(new MonthlyNotFoundException("Monthly not found!"));
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/taxrelief?childId=15&year=2021"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Monthly not found!")))
                .andDo(print());
    }

    @Test
    void addChildTest_whenChildIsNotExistsInDB_thenReturnChildAdded() throws Exception {
        //GIVEN
        //WHEN
        when(childServiceImplMock.addChild(any(Child.class))).thenReturn(childTest);
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.post("/child/add")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(childTest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(15)))
                .andExpect(jsonPath("$.firstname", is("Lea")))
                .andExpect(jsonPath("$.lastname", is("Sanchez")))
                .andExpect(jsonPath("$.birthDate", is("12/01/2020")))
                .andDo(print());
    }

    @Test
    void addChildTest_whenChildAlreadyExistsInDB_thenReturnResponseEntityWithErrorMessageAndStatusBadRequest() throws Exception {
        //GIVEN
        //WHEN
        when(childServiceImplMock.addChild(any(Child.class))).thenThrow(new ChildAlreadyExistException("Child already exists, unable to add!"));
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.post("/child/add")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(childTest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Child already exists, unable to add!")))
                .andDo(print());
    }

    @Test
    void updateChildTest_whenChildExists_thenReturnChildUpdated() throws Exception {
        //GIVEN
        Child childTestUpdated = new Child(
                15, "LastnameUpdated", "FirstnameUpdated", "12/01/2020", "02/05/2020", "http://image.jpeg", "christine@email.fr");
        //WHEN
        when(childServiceImplMock.updateChild(any(Child.class))).thenReturn(childTestUpdated);
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.put("/child/update")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(childTest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is("FirstnameUpdated")))
                .andExpect(jsonPath("$.lastname", is("LastnameUpdated")))
                .andDo(print());
    }

    @Test
    void updateChildTest_ChildNotFound_thenReturnResponseEntityWithErrorMessageAndStatusNotFound() throws Exception {
        //GIVEN
        //WHEN
        when(childServiceImplMock.updateChild(any(Child.class))).thenThrow(new ChildNotFoundException("Child not found, unable to update!"));
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.put("/child/update")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(childTest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Child not found, unable to update!")))
                .andDo(print());
    }

    @Test
    void deleteChildTest_thenReturnResponseEntityWithASuccessMessage() throws Exception {
        //GIVEN
        //WHEN
        when(childServiceImplMock.deleteChildById(anyInt())).thenReturn("Child deleted with success!");
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.delete("/child/delete/15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("Child deleted with success!")))
                .andDo(print());
    }
}