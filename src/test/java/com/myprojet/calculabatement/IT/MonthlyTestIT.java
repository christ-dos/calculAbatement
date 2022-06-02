package com.myprojet.calculabatement.IT;

import com.myprojet.calculabatement.exceptions.MonthlyAlreadyExistException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import com.myprojet.calculabatement.services.ChildService;
import com.myprojet.calculabatement.services.MonthlyService;
import com.myprojet.calculabatement.utils.ConvertObjectToJsonString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(value = {"/abatementTest.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class MonthlyTestIT {
    @Autowired
    private MockMvc mockMvcMonthly;

    @Autowired
    private MonthlyService monthlyServiceTest;

    @Autowired
    private MonthlyRepository monthlyRepositoryTest;

    @Autowired
    private ChildService childServiceTest;

    private Monthly monthlyTest;

    private Child childTest;

    @BeforeEach
    void setupPerTest() {
        childTest = new Child(
                1, "Sanchez", "Lea", "12/01/2020", "02/05/2020", null, 1.0, 0.5, LocalDateTime.now(), "http://image.jpeg", "christine@email.fr");
        childServiceTest.addChild(childTest);
        Child child2 = new Child(
                2, "Dupont", "Jean", "12/05/2020", "02/05/2020", null, 1.0, 0.5, LocalDateTime.now(), "http://image.jpeg", "christine@email.fr");
        childServiceTest.addChild(child2);
        monthlyTest = new Monthly(1, Month.JANVIER, "2021",
                500D, 10, 10, 10, 0, 1);
    }

    @Test
    void addMonthlyTest_whenMonthlyNotExistsInDB_thenReturnMonthlyAdded() throws Exception {
        //GIVEN
        Monthly monthlyTestService = new Monthly(2, Month.FEVRIER, "2021",
                500D, 10, 10, 10, 0, 1);

        Monthly monthlyTestRepository = new Monthly(3, Month.MARS, "2021",
                500D, 10, 10, 10, 0, 1);
        //WHEN
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

        Monthly monthlyAddedService = monthlyServiceTest.addMonthly(monthlyTestService);
        assertEquals(2, monthlyAddedService.getMonthlyId());
        assertEquals(1, monthlyAddedService.getChildId());

        Monthly monthlyAddedRepository = monthlyRepositoryTest.save(monthlyTestRepository);
        assertEquals(3, monthlyAddedRepository.getMonthlyId());
        assertEquals(1, monthlyAddedRepository.getChildId());
    }

    @Test
    void addMonthlyTest_whenMonthlyAlreadyExistInDB_thenReturnErrorMessageAndStatusBadRequest() throws Exception {
        //GIVEN
        monthlyServiceTest.addMonthly(monthlyTest);
        //WHEN
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.post("/monthly/add")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(monthlyTest)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MonthlyAlreadyExistException))
                .andExpect(result -> assertEquals("La déclaration mensuelle que vous essayez d'ajouter existe déja!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("Monthly already exits, please process an update!")))
                .andDo(print());

        MonthlyAlreadyExistException thrown = assertThrows(MonthlyAlreadyExistException.class, () -> {
            monthlyServiceTest.addMonthly(monthlyTest);
        });
        assertEquals("La déclaration mensuelle que vous essayez d'ajouter existe déja!", thrown.getMessage());

        Monthly monthlyAlreadyExistSavedInRepository = monthlyRepositoryTest.save(monthlyTest);
        assertEquals(1, monthlyAlreadyExistSavedInRepository.getMonthlyId());
        assertEquals(1, monthlyAlreadyExistSavedInRepository.getChildId());

        List<Monthly> monthlies = (List<Monthly>) monthlyServiceTest.getAllMonthly();
        assertTrue(monthlies.size() == 1);
        assertEquals(1, monthlies.get(0).getMonthlyId());
        assertEquals(1, monthlies.get(0).getChildId());
    }

    @Test
    void getAllMonthliesByYearAndChildIdOrderByMonthDescTest_thenDisplayedListOfMonthliesByYearOrderByMonthDesc() throws Exception {
        //GIVEN
        List<Monthly> monthlies = Arrays.asList(
                new Monthly(1, Month.DECEMBRE, "2021", 650D, 10, 10, 10, 15, 1),
                new Monthly(2, Month.FEVRIER, "2022", 650D, 10, 10, 10, 10.5, 1),
                new Monthly(3, Month.AVRIL, "2022", 650D, 10, 10, 10, 15, 1),
                new Monthly(4, Month.AVRIL, "2022", 500D, 10, 10, 10, 15, 2)
        );
        monthlyRepositoryTest.saveAll(monthlies);

        List<Monthly> allMonthlies = (List<Monthly>) monthlyServiceTest.getAllMonthly();
        assertTrue(allMonthlies.size() == 4);
        //WHEN
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.get("/monthly/all/year/childid?year=2022&childId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].month", is("AVRIL")))
                .andExpect(jsonPath("$.[1].month", is("FEVRIER")))
                .andExpect(jsonPath("$.[0].year", is("2022")))
                .andExpect(jsonPath("$.[1].year", is("2022")))
                .andExpect(jsonPath("$.[0].monthlyId", is(3)))
                .andExpect(jsonPath("$.[1].monthlyId", is(2)))
                .andExpect(jsonPath("$.[0].childId", is(1)))
                .andExpect(jsonPath("$.[1].childId", is(1)))
                .andDo(print());

        List<Monthly> monthlyListByChildIdAnsByYearService = (List<Monthly>) monthlyServiceTest.getAllMonthlyByYearAndChildIdOrderByMonthDesc("2022", 1);
        assertTrue(monthlyListByChildIdAnsByYearService.size() == 2);
        assertEquals(Month.AVRIL, monthlyListByChildIdAnsByYearService.get(0).getMonth());
        assertEquals(Month.FEVRIER, monthlyListByChildIdAnsByYearService.get(1).getMonth());
        assertEquals(1, monthlyListByChildIdAnsByYearService.get(0).getChildId());
        assertEquals(1, monthlyListByChildIdAnsByYearService.get(1).getChildId());
        assertEquals("2022", monthlyListByChildIdAnsByYearService.get(0).getYear());
        assertEquals("2022", monthlyListByChildIdAnsByYearService.get(1).getYear());

        List<Monthly> monthlyListByChildIdAndByYearRepository = (List<Monthly>) monthlyRepositoryTest.findMonthlyByYearAndChildIdOrderByMonthDesc("2022", 1);
        assertTrue(monthlyListByChildIdAndByYearRepository.size() == 2);
        assertEquals(Month.AVRIL, monthlyListByChildIdAndByYearRepository.get(0).getMonth());
        assertEquals(Month.FEVRIER, monthlyListByChildIdAndByYearRepository.get(1).getMonth());
        assertEquals(1, monthlyListByChildIdAndByYearRepository.get(0).getChildId());
        assertEquals(1, monthlyListByChildIdAndByYearRepository.get(1).getChildId());
        assertEquals("2022", monthlyListByChildIdAndByYearRepository.get(0).getYear());
        assertEquals("2022", monthlyListByChildIdAndByYearRepository.get(1).getYear());
    }

    @Test
    void getMonthliesByChildIdOrderByYearDescMonthDescTest_thenReturnListOfMonthliesByChildIdOrderByYearDescMonthDesc() throws Exception {
        //GIVEN
        List<Monthly> monthlies = Arrays.asList(
                new Monthly(1, Month.DECEMBRE, "2021", 650D, 20, 20, 20, 15, 1),
                new Monthly(2, Month.AVRIL, "2022", 650D, 20, 20, 20, 15, 1),
                new Monthly(3, Month.FEVRIER, "2022", 650D, 20, 20, 20, 10.5, 1),
                new Monthly(4, Month.FEVRIER, "2022", 500D, 20, 20, 20, 10.5, 2)
        );
        monthlyRepositoryTest.saveAll(monthlies);
        List<Monthly> allMonthlies = (List<Monthly>) monthlyServiceTest.getAllMonthly();
        assertTrue(allMonthlies.size() == 4);
        //WHEN
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.get("/monthly/all/childid?childId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].month", is("AVRIL")))
                .andExpect(jsonPath("$.[2].month", is("DECEMBRE")))
                .andExpect(jsonPath("$.[0].year", is("2022")))
                .andExpect(jsonPath("$.[2].year", is("2021")))
                .andExpect(jsonPath("$.[0].monthlyId", is(2)))
                .andExpect(jsonPath("$.[2].monthlyId", is(1)))
                .andExpect(jsonPath("$.[0].childId", is(1)))
                .andExpect(jsonPath("$.[1].childId", is(1)))
                .andExpect(jsonPath("$.[2].childId", is(1)))
                .andDo(print());

        List<Monthly> monthliesByChildIdOrderByYearDescMonthDescService = (List<Monthly>) monthlyServiceTest.getMonthliesByChildIdOrderByYearDescMonthDesc(1);
        assertTrue(monthliesByChildIdOrderByYearDescMonthDescService.size() == 3);
        assertEquals(Month.AVRIL, monthliesByChildIdOrderByYearDescMonthDescService.get(0).getMonth());
        assertEquals(Month.DECEMBRE, monthliesByChildIdOrderByYearDescMonthDescService.get(2).getMonth());
        assertEquals(1, monthliesByChildIdOrderByYearDescMonthDescService.get(0).getChildId());
        assertEquals(1, monthliesByChildIdOrderByYearDescMonthDescService.get(1).getChildId());
        assertEquals(1, monthliesByChildIdOrderByYearDescMonthDescService.get(2).getChildId());
        assertEquals("2022", monthliesByChildIdOrderByYearDescMonthDescService.get(0).getYear());
        assertEquals("2021", monthliesByChildIdOrderByYearDescMonthDescService.get(2).getYear());

        List<Monthly> monthliesByChildIdOrderByYearDescMonthDescRepository = (List<Monthly>) monthlyRepositoryTest.findMonthlyByChildIdOrderByYearDescMonthDesc(1);
        assertTrue(monthliesByChildIdOrderByYearDescMonthDescRepository.size() == 3);
        assertEquals(Month.AVRIL, monthliesByChildIdOrderByYearDescMonthDescRepository.get(0).getMonth());
        assertEquals(Month.DECEMBRE, monthliesByChildIdOrderByYearDescMonthDescRepository.get(2).getMonth());
        assertEquals(1, monthliesByChildIdOrderByYearDescMonthDescRepository.get(0).getChildId());
        assertEquals(1, monthliesByChildIdOrderByYearDescMonthDescRepository.get(1).getChildId());
        assertEquals(1, monthliesByChildIdOrderByYearDescMonthDescRepository.get(2).getChildId());
        assertEquals("2022", monthliesByChildIdOrderByYearDescMonthDescRepository.get(0).getYear());
        assertEquals("2021", monthliesByChildIdOrderByYearDescMonthDescRepository.get(2).getYear());

    }
    // todo continuer avec getMonth


}
