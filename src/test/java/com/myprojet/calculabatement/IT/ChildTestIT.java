package com.myprojet.calculabatement.IT;

import com.myprojet.calculabatement.exceptions.ChildNotFoundException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.ChildRepository;
import com.myprojet.calculabatement.services.ChildService;
import com.myprojet.calculabatement.services.TaxableSalaryService;
import com.myprojet.calculabatement.services.TotalAnnualTaxReliefsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(value = {"/abatementTest.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class ChildTestIT {
    @Autowired
    private MockMvc mockMvcChild;

    @Autowired
    private ChildRepository childRepositoryTest;

    @Autowired
    private ChildService childServiceTest;

    @Autowired
    private TotalAnnualTaxReliefsService totalAnnualTaxReliefsServiceTest;

    @Autowired private TaxableSalaryService taxableSalaryServiceTest;

    private Child childTest;

    @BeforeEach
    void setupPerTest() {
        childTest = new Child(
                1, "Sanchez", "Lea", "12/01/2020", "02/05/2020", null, 1.0, 0.5, LocalDateTime.now(), "http://image.jpeg", "christine@email.fr",
                Arrays.asList(
                        new Monthly(1, Month.JANVIER, "2021", 500D, 20, 20, 10, 0, 1),
                        new Monthly(2, Month.FEVRIER, "2021", 500D, 20, 20, 10, 0, 1),
                        new Monthly(3, Month.MARS, "2021", 500D, 20, 20, 10, 0, 1)
                ));
    }

    @Test
    void getAllChildrenTest_theReturnAnIterableOfChildOrderByDateAddedDesc() throws Exception {
        //GIVEN
        String userEmail = "christine@email.fr";
        List<Child> children = Arrays.asList(
                new Child(1, "Riboulet", "Romy", "12/01/2020", "02/05/2020", LocalDateTime.now().minusMinutes(30), "http://image.jpeg", "christine@email.fr"),
                new Child(2, "Cacahuette", "Manon", "30/11/2017", "01/03/2020", LocalDateTime.now().minusMinutes(15), "http://image.jpeg", "christine@email.fr"),
                new Child(3, "Charton", "Nathan", "14/05/2021", "24/08/2021", LocalDateTime.now(), "http://image.jpeg", "christine@email.fr")
        );
        childRepositoryTest.saveAll(children);
        //WHEN
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(3)))
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.[0].firstname", is("Nathan")))
                .andExpect(jsonPath("$.[0].lastname", is("Charton")))
                .andExpect(jsonPath("$.[0].birthDate", is("14/05/2021")))
                .andDo(print());

        List<Child> childrenListService = (List<Child>) childServiceTest.getChildrenByUserEmailOrderByDateAddedDesc();
        assertEquals(3, childrenListService.size());
        assertEquals(3, childrenListService.get(0).getId());
        assertEquals(1, childrenListService.get(2).getId());

        List<Child> childrenListRepository = (List<Child>) childRepositoryTest.findChildrenByUserEmailOrderByDateAddedDesc(userEmail);
        assertEquals(3, childrenListRepository.size());
        assertEquals(3, childrenListService.get(0).getId());
        assertEquals(1, childrenListService.get(2).getId());
    }

    @Test
    void getChildByIdTest_whenIdIs1_thenReturnAChildSanchezLea() throws Exception {
        //GIVEN
        childServiceTest.addChild(childTest);
        //WHEN
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname", is("Lea")))
                .andExpect(jsonPath("$.lastname", is("Sanchez")))
                .andExpect(jsonPath("$.birthDate", is("12/01/2020")))
                .andExpect(jsonPath("$.monthlies.length()", is(3)))
                .andDo(print());

        Child leaSanchezChildService = childServiceTest.getChildById(1);
        assertEquals(1, leaSanchezChildService.getId());
        assertEquals("Lea", leaSanchezChildService.getFirstname());
        assertEquals("Sanchez", leaSanchezChildService.getLastname());
        assertEquals(3, leaSanchezChildService.getMonthlies().size());

        Optional<Child> leaSanchezChildRepository = childRepositoryTest.findById(1);
        assertTrue(leaSanchezChildRepository.isPresent());
        assertEquals(1, leaSanchezChildRepository.get().getId());
        assertEquals("Lea", leaSanchezChildRepository.get().getFirstname());
        assertEquals("Sanchez", leaSanchezChildRepository.get().getLastname());
        assertEquals(3, leaSanchezChildRepository.get().getMonthlies().size());
    }

    @Test
    void getChildByIdTest_whenChildNotFound_thenReturnStatusNotFoundAndErrorMessage() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/find/105"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ChildNotFoundException))
                .andExpect(result -> assertEquals("L'enfant n'a pas été trouvé!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("Child not found, please try again!")))
                .andDo(print());

        ChildNotFoundException thrown = assertThrows(ChildNotFoundException.class, () -> {
            childServiceTest.getChildById(105);
        }, "Child not found, please try again!");
        assertEquals("L'enfant n'a pas été trouvé!", thrown.getMessage());

        Optional<Child> childNotFoundRepository = childRepositoryTest.findById(105);
        assertTrue(!childNotFoundRepository.isPresent());
    }

    @Test
    void getAnnualTaxableSalaryByChildTest_whenChildHasMonthliesSaved_thenReturnResponseEntityWithTheResult() throws Exception {
        //GIVEN
        childServiceTest.addChild(childTest);
        //WHEN
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/taxablesalary?childId=1&year=2021"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1500D)))
                .andDo(print());

        double taxableSalaryChildIdOne = taxableSalaryServiceTest.getSumTaxableSalaryByChildAndByYear("2021", 1);
        assertEquals(1500D,taxableSalaryChildIdOne );
    }

    @Test
    void getAnnualReportableAmountsByChildTest_whenChildHasMonthliesSavedForYearRequested_thenReturnResponseEntityWithAnnualReportableAmounts() throws Exception {
        //GIVEN
        childServiceTest.addChild(childTest);
        //WHEN
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/reportableamounts?childId=1&year=2021"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(637.5)))
                .andDo(print());

        double annualReportableAmountForChildIdOne = totalAnnualTaxReliefsServiceTest.getTotalAnnualReportableAmountsByChild(childTest,"2021");
        assertEquals(637.50, annualReportableAmountForChildIdOne);
    }
    ///todo continu implement annulReportableAmouts gerer lexception


}

