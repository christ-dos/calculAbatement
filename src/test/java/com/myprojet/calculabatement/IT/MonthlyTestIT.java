package com.myprojet.calculabatement.IT;

import com.myprojet.calculabatement.exceptions.MonthlyAlreadyExistException;
import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.exceptions.NetBrutCoefficientNotNullException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.MonthlyRepository;
import com.myprojet.calculabatement.services.ChildService;
import com.myprojet.calculabatement.services.MonthlyService;
import com.myprojet.calculabatement.services.TaxableSalaryService;
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

    @Autowired
    private TaxableSalaryService taxableSalaryService;

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
    void addMonthlyTest_whenMonthlyByMonthAndYearAlreadyExistInDB_thenReturnErrorMessageAndStatusBadRequest() throws Exception {
        //GIVEN
        monthlyServiceTest.addMonthly(monthlyTest);
        Monthly monthlyAlreadyExistByMonthAndByYear = new Monthly(250, Month.JANVIER, "2021",
                500D, 10, 10, 10, 0, 1);
        //WHEN
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.post("/monthly/add")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(monthlyAlreadyExistByMonthAndByYear)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MonthlyAlreadyExistException))
                .andExpect(result -> assertEquals("La déclaration mensuelle pour: JANVIER 2021, que vous essayez d'ajouter existe déja!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("La déclaration mensuelle pour: JANVIER 2021, que vous essayez d'ajouter existe déja!")))
                .andDo(print());

        MonthlyAlreadyExistException thrown = assertThrows(MonthlyAlreadyExistException.class, () ->
            monthlyServiceTest.addMonthly(monthlyTest)
        );
        assertEquals("La déclaration mensuelle pour: JANVIER 2021, que vous essayez d'ajouter existe déja!", thrown.getMessage());

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
        assertEquals(4,allMonthlies.size());
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
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.get("/monthly/taxablesalarysibling?netSalary=500&netBrutCoefficient=0.7801&maintenanceCost=30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(548.26)))
                .andDo(print());

        double taxableSalarySibling = taxableSalaryService.calculateTaxableSalarySiblingByMonth(500D, 0.7801, 30);
        assertEquals(548.26, taxableSalarySibling);
    }

    @Test
    void getTaxableSalarySiblingTest_whenNetBrutCoefficientIsNull_thenReturnErrorMessageAndStatusBadRequest() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.get("/monthly/taxablesalarysibling?netSalary=500&netBrutCoefficient=0D&maintenanceCost=30"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NetBrutCoefficientNotNullException))
                .andExpect(result -> assertEquals("Le coefficient de conversion de net en brut ne peut pas être equal à 0!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("Le coefficient de conversion de net en brut ne peut pas être equal à 0!")))
                .andDo(print());

        NetBrutCoefficientNotNullException thrown = assertThrows(NetBrutCoefficientNotNullException.class, () ->
            taxableSalaryService.calculateTaxableSalarySiblingByMonth(500D, 0, 30)
        );
        assertEquals("Le coefficient de conversion de net en brut ne peut pas être equal à 0!", thrown.getMessage());
    }

    @Test
    void updateMonthlyTest_whenTaxableSalaryUpdatedTo550AndDayWorkedUpdatedTo15_thenReturnMonthlyUpdated() throws Exception {
        //GIVEN
        Monthly monthlyToUpdate = new Monthly(1, Month.JANVIER, "2021",
                550D, 20, 20, 15, 0, 1);
        monthlyRepositoryTest.save(monthlyTest);

        List<Monthly> monthlies = (List<Monthly>) monthlyServiceTest.getAllMonthly();
        assertTrue(monthlies.size() == 1);
        assertEquals(1, monthlies.get(0).getMonthlyId());
        assertEquals(1, monthlies.get(0).getChildId());
        //WHEN
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

        Monthly monthlyUpdatedService = monthlyServiceTest.updateMonthly(monthlyToUpdate);
        assertNotNull(monthlyUpdatedService);
        assertEquals(1, monthlyUpdatedService.getMonthlyId());
        assertEquals(1, monthlyUpdatedService.getChildId());
        assertEquals(550D, monthlyUpdatedService.getTaxableSalary());
        assertEquals(15, monthlyUpdatedService.getDayWorked());

        Monthly monthlyUpdatedRepository = monthlyRepositoryTest.save(monthlyToUpdate);
        assertEquals(1, monthlyUpdatedService.getMonthlyId());
        assertEquals(1, monthlyUpdatedService.getChildId());
        assertEquals(550D, monthlyUpdatedService.getTaxableSalary());
        assertEquals(15, monthlyUpdatedService.getDayWorked());

        List<Monthly> monthlyListAfterUpdate = (List<Monthly>) monthlyServiceTest.getAllMonthly();
        assertTrue(monthlyListAfterUpdate.size() == 1);
        assertEquals(1, monthlyUpdatedService.getMonthlyId());
        assertEquals(1, monthlyUpdatedService.getChildId());
    }

    @Test
    void updateMonthlyTest_whenMonthlyNotExists_thenReturnErrorMessageAndStatusNotFound() throws Exception {
        //GIVEN
        boolean isMonthlyToUpdateExist = monthlyRepositoryTest.existsById(1);
        assertFalse(isMonthlyToUpdateExist);

        Monthly monthlyToUpdateNotExist = new Monthly(250, Month.JANVIER, "2021",
                500D, 20, 20, 10, 0, 1);
        //WHEN
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.put("/monthly/update")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(monthlyToUpdateNotExist)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MonthlyNotFoundException))
                .andExpect(result -> assertEquals("La déclaration mensuelle " + monthlyToUpdateNotExist.getMonth() + " " + monthlyToUpdateNotExist.getYear() + " n'existe pas!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("La déclaration mensuelle " + monthlyToUpdateNotExist.getMonth() + " " + monthlyToUpdateNotExist.getYear() + " n'existe pas!")))
                .andDo(print());

        MonthlyNotFoundException thrown = assertThrows(MonthlyNotFoundException.class, () ->
            monthlyServiceTest.updateMonthly(monthlyToUpdateNotExist)
        );
        assertEquals(
                "La déclaration mensuelle " + monthlyToUpdateNotExist.getMonth() + " " + monthlyToUpdateNotExist.getYear() + " n'existe pas!", thrown.getMessage());

        boolean isMonthlyAfterUpdateTest = monthlyRepositoryTest.existsById(1);
        assertFalse(isMonthlyAfterUpdateTest);
    }

    @Test
    void deleteMonthlyByIdTest_thenReturnSuccessMessage() throws Exception {
        //GIVEN
        monthlyRepositoryTest.save(monthlyTest);
        List<Monthly> monthliesBeforeDeletion = (List<Monthly>) monthlyServiceTest.getAllMonthly();
        assertTrue(monthliesBeforeDeletion.size() > 0);
        //WHEN
        //THEN
        mockMvcMonthly.perform(MockMvcRequestBuilders.delete("/monthly/delete/1"))
                .andExpect(status().isOk())
                .andDo(print());

        Monthly monthlyAdded = monthlyRepositoryTest.save(monthlyTest);
        String successMessage = monthlyServiceTest.deleteMonthlyById(monthlyAdded.getMonthlyId());
        assertEquals("La déclaration mensuelle a été supprimé avec succés!", successMessage);

        List<Monthly> monthliesAfterDeletion = (List<Monthly>) monthlyServiceTest.getAllMonthly();
        assertTrue(monthliesAfterDeletion.isEmpty());
    }
}
