package com.myprojet.calculabatement.IT;

import com.myprojet.calculabatement.exceptions.ChildAlreadyExistException;
import com.myprojet.calculabatement.exceptions.ChildNotFoundException;
import com.myprojet.calculabatement.exceptions.MonthlyNotFoundException;
import com.myprojet.calculabatement.models.Child;
import com.myprojet.calculabatement.models.Month;
import com.myprojet.calculabatement.models.Monthly;
import com.myprojet.calculabatement.repositories.ChildRepository;
import com.myprojet.calculabatement.services.CalculateTaxReliefService;
import com.myprojet.calculabatement.services.ChildService;
import com.myprojet.calculabatement.services.TaxableSalaryService;
import com.myprojet.calculabatement.services.TotalAnnualTaxReliefsService;
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
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @Autowired
    private TaxableSalaryService taxableSalaryServiceTest;

    @Autowired
    private CalculateTaxReliefService calculateTaxReliefService;

    private Child childTest;

    @BeforeEach
    void setupPerTest() {
        childTest = new Child(
                1, "Sanchez", "Lea", "12/01/2020", "02/05/2020", null, 1.0, 0.5, LocalDateTime.now(), "http://image.jpeg", "christine@email.fr",
                Arrays.asList(
                        new Monthly(1, Month.JANVIER, "2021", 500D, 10, 10, 10, 0, 1),
                        new Monthly(2, Month.FEVRIER, "2021", 500D, 10, 10, 10, 0, 1),
                        new Monthly(3, Month.MARS, "2021", 500D, 10, 10, 10, 0, 1)
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
                .andExpect(result -> assertEquals("L'enfant avec ID: 105 n'a pas été trouvé!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("L'enfant avec ID: 105 n'a pas été trouvé!")))
                .andDo(print());

        ChildNotFoundException thrown = assertThrows(ChildNotFoundException.class, () ->
            childServiceTest.getChildById(105)
        );
        assertEquals("L'enfant avec ID: 105 n'a pas été trouvé!", thrown.getMessage());

        Optional<Child> childNotFoundRepository = childRepositoryTest.findById(105);
        assertFalse(childNotFoundRepository.isPresent());
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
        assertEquals(1500D, taxableSalaryChildIdOne);
    }

    @Test
    void getAnnualReportableAmountsByChildTest_whenChildHasMonthliesSavedForYearRequested_thenReturnResponseEntityWithAnnualReportableAmounts() throws Exception {
        //GIVEN
        childServiceTest.addChild(childTest);
        //WHEN
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/reportableamounts?childId=1&year=2021"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(607.5)))
                .andDo(print());

        double annualReportableAmountForChildIdOne = totalAnnualTaxReliefsServiceTest.getTotalAnnualReportableAmountsByChild(childTest, "2021");
        assertEquals(607.50, annualReportableAmountForChildIdOne);
    }

    @Test
    void getAnnualReportableAmountsByChildTest_whenChildHasNoMonthliesForYearRequested_thenReturnErrorMessageAndStatus404() throws Exception {
        //GIVEN
        Child childHasNoMonthliesIn2021 = new Child(
                1, "Bernard", "Shanna", "12/01/2020",
                "02/05/2020", "http://image.jpeg", "christine@email.fr", Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 500D, 20, 20, 10, 0, 1)
        ));
        childServiceTest.addChild(childHasNoMonthliesIn2021);
        //WHEN
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/reportableamounts?childId=1&year=2021"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MonthlyNotFoundException))
                .andExpect(result -> assertEquals("Il n'y a aucune entrée enregistré pour l'année: 2021",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("Il n'y a aucune entrée enregistré pour l'année: 2021")))
                .andDo(print());

        MonthlyNotFoundException thrown = assertThrows(MonthlyNotFoundException.class, () ->
            totalAnnualTaxReliefsServiceTest.getTotalAnnualReportableAmountsByChild(childHasNoMonthliesIn2021, "2021")
        );
        assertEquals("Il n'y a aucune entrée enregistré pour l'année: 2021", thrown.getMessage());
    }

    @Test
    void getTaxReliefByChildTest_whenChildHasMonthliesSavedForYearRequested_thenReturnResponseEntityWithTaxReliefCalculated() throws Exception {
        //GIVEN
        childServiceTest.addChild(childTest);
        //WHEN
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/taxrelief?childId=1&year=2021"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(922.50)))
                .andDo(print());

        double taxReliefByChild = calculateTaxReliefService.calculateTaxReliefByChild("2021", childTest.getId());
        assertEquals(922.5, taxReliefByChild);
    }

    @Test
    void getTaxReliefByChildTest_whenChildHasNoMonthlies_thenReturnErrorMessageAnsStatus404() throws Exception {
        //GIVEN
        Child childHasNoMonthliesIn2021 = new Child(
                1, "Bernard", "Shanna", "12/01/2020",
                "02/05/2020", "http://image.jpeg", "christine@email.fr", Arrays.asList(
                new Monthly(1, Month.JANVIER, "2022", 500D, 20, 20, 10, 0, 1)
        ));
        childServiceTest.addChild(childHasNoMonthliesIn2021);
        //WHEN
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.get("/child/taxrelief?childId=1&year=2021"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MonthlyNotFoundException))
                .andExpect(result -> assertEquals("Il n'y a aucune entrée enregistré pour l'année: 2021",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is("Il n'y a aucune entrée enregistré pour l'année: 2021")))
                .andDo(print());

        MonthlyNotFoundException thrown = assertThrows(MonthlyNotFoundException.class, () ->
            calculateTaxReliefService.calculateTaxReliefByChild("2021", 1)
        );
        assertEquals("Il n'y a aucune entrée enregistré pour l'année: 2021", thrown.getMessage());

    }

    @Test
    void addChildTest_whenChildNotExistsInDB_thenReturnChildAdded() throws Exception {
        //GIVEN
        Child childToAdd = new Child(
                1, "Bernard", "Shanna", "12/01/2020",
                "02/05/2020", "http://image.jpeg", "christine@email.fr");
        Child childToAddInService = new Child(
                2, "Dupont", "Sylvie", "15/01/2020",
                "02/05/2020", "http://image.jpeg", "christine@email.fr");
        Child childToAddInRepository = new Child(
                3, "Pais", "Jo", "12/05/2020",
                "02/05/2020", LocalDateTime.now(), "http://image.jpeg", "christine@email.fr");
        //WHEN
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.post("/child/add")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(childToAdd)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("Shanna")))
                .andExpect(jsonPath("$.lastname", is("Bernard")))
                .andExpect(jsonPath("$.birthDate", is("12/01/2020")))
                .andDo(print());


        Child childAddedService = childServiceTest.addChild(childToAddInService);
        assertEquals(2, childAddedService.getId());
        assertEquals("Sylvie", childAddedService.getFirstname());
        assertEquals("Dupont", childAddedService.getLastname());
        assertEquals("15/01/2020", childAddedService.getBirthDate());


        Child childAddedRepository = childRepositoryTest.save(childToAddInRepository);
        assertEquals(3, childAddedRepository.getId());
        assertEquals("Jo", childAddedRepository.getFirstname());
        assertEquals("Pais", childAddedRepository.getLastname());
        assertEquals("12/05/2020", childAddedRepository.getBirthDate());

    }

    @Test
    void addChildTest_whenChildAlreadyExistsInDB_thenReturnErrorMessageAndStatusBadRequest() throws Exception {
        //GIVEN
        Child childToAddAlreadyExist = new Child(
                1, "Bernard", "Shanna", "12/01/2020",
                "02/05/2020", "http://image.jpeg", "christine@email.fr");
        childServiceTest.addChild(childToAddAlreadyExist);
        //WHEN
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.post("/child/add")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(childToAddAlreadyExist)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ChildAlreadyExistException))
                .andExpect(result -> assertEquals("L'enfant "+ childToAddAlreadyExist.getFirstname().toUpperCase() + " " + childToAddAlreadyExist.getLastname().toUpperCase() + " que vous essayez d'ajouter existe déja!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is(
                        "L'enfant "+ childToAddAlreadyExist.getFirstname().toUpperCase() + " " + childToAddAlreadyExist.getLastname().toUpperCase() + " que vous essayez d'ajouter existe déja!")))
                .andDo(print());

        ChildAlreadyExistException thrown = assertThrows(ChildAlreadyExistException.class, () ->
            childServiceTest.addChild(childToAddAlreadyExist)
            );
        assertEquals(
                "L'enfant "+ childToAddAlreadyExist.getFirstname().toUpperCase() + " " + childToAddAlreadyExist.getLastname().toUpperCase() + " que vous essayez d'ajouter existe déja!", thrown.getMessage());

        Child childAlreadyExistSavedInRepository = childRepositoryTest.save(childToAddAlreadyExist);
        assertEquals(1, childAlreadyExistSavedInRepository.getId());

        List<Child> listChildrenSaved = (List<Child>) childServiceTest.getChildrenByUserEmailOrderByDateAddedDesc();
        assertEquals(1,listChildrenSaved.size());
        assertEquals(1, listChildrenSaved.get(0).getId());
        assertEquals("christine@email.fr", listChildrenSaved.get(0).getUserEmail());
    }

    @Test
    void updateChildTest_whenChildExists_thenReturnChildUpdated() throws Exception {
        //GIVEN
        Child childSaved = new Child(
                1, "Bernard", "Shanna", "12/01/2020",
                "02/05/2020", "http://image.jpeg", "christine@email.fr");
        childServiceTest.addChild(childSaved);
        Child childTestToUpdate = new Child(
                1, "LastnameUpdated", "FirstnameUpdated", "12/01/2020", "02/05/2020", "http://image.jpeg", "christine@email.fr");
        Child childTestToUpdateService = new Child(
                1, "LastnameUpdatedService", "FirstnameUpdatedService", "12/01/2020", "02/05/2020", "http://image.jpeg", "christine@email.fr");
        Child childTestToUpdateRepository = new Child(
                1, "LastnameUpdatedRepo", "FirstnameUpdatedRepo", "12/01/2020", "02/05/2020", LocalDateTime.now(), "http://image.jpeg", "christine@email.fr");

        //WHEN
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.put("/child/update")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(childTestToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstname", is("FirstnameUpdated")))
                .andExpect(jsonPath("$.lastname", is("LastnameUpdated")))
                .andDo(print());

        Child childUpdatedService = childServiceTest.updateChild(childTestToUpdateService);
        assertEquals(1, childUpdatedService.getId());
        assertEquals("FirstnameUpdatedService", childUpdatedService.getFirstname());
        assertEquals("LastnameUpdatedService", childUpdatedService.getLastname());

        Child childUpdatedRepository = childRepositoryTest.save(childTestToUpdateRepository);
        assertEquals(1, childUpdatedRepository.getId());
        assertEquals("FirstnameUpdatedRepo", childUpdatedRepository.getFirstname());
        assertEquals("LastnameUpdatedRepo", childUpdatedRepository.getLastname());
    }

    @Test
    void updateChildTest_ChildNotFound_thenReturnErrorMessageAndStatus404() throws Exception {
        //GIVEN
        //WHEN
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.put("/child/update")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(childTest)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ChildNotFoundException))
                .andExpect(result -> assertEquals(
                        "L'enfant "+ childTest.getFirstname().toUpperCase() + " " + childTest.getLastname().toUpperCase() + " que vous essayez de mettre à jour n'existe pas!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is(
                        "L'enfant "+ childTest.getFirstname().toUpperCase() + " " + childTest.getLastname().toUpperCase() + " que vous essayez de mettre à jour n'existe pas!")))
                .andDo(print());

        ChildNotFoundException thrown = assertThrows(ChildNotFoundException.class, () ->
            childServiceTest.updateChild(childTest)
        );
        assertEquals("L'enfant "+ childTest.getFirstname().toUpperCase() + " " + childTest.getLastname().toUpperCase() + " que vous essayez de mettre à jour n'existe pas!", thrown.getMessage());

        List<Child> listChildren = (List<Child>) childServiceTest.getChildrenByUserEmailOrderByDateAddedDesc();
        assertTrue(listChildren.isEmpty());
    }

    @Test
    void updateChildTest_whenChildExistsByFirstnameAndLastnameAndBirthdate_thenReturnErrorMessageAndStatus404() throws Exception {
        //GIVEN
        Child childFirstnameAndLastnameAndBirthDateAlreadyExist = new Child(
                25, "Sanchez", "Lea", "12/01/2020", "02/05/2019", "http://image.jpeg", "christine@email.fr");
        Child childToUpdate = new Child(
                2, "Sanchez", "Lilly", "12/01/2020", "02/05/2019", "http://image.jpeg", "christine@email.fr");

        Child childThatTryToUpdate = new Child(
                2, "Sanchez", "Lea", "12/01/2020", "02/05/2019", "http://image.jpeg", "christine@email.fr");
        //WHEN
        childServiceTest.addChild(childFirstnameAndLastnameAndBirthDateAlreadyExist);
        childServiceTest.addChild(childToUpdate);

        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.put("/child/update")
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                        .content(ConvertObjectToJsonString.asJsonString(childThatTryToUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ChildAlreadyExistException))
                .andExpect(result -> assertEquals( "L'enfant: "
                                + childThatTryToUpdate.getFirstname().toUpperCase() + " "
                                + childThatTryToUpdate.getLastname().toUpperCase() + " est déjà enregistré dans la base de données!",
                        result.getResolvedException().getMessage()))
                .andExpect(jsonPath("$.message", is( "L'enfant: "
                        + childThatTryToUpdate.getFirstname().toUpperCase() + " "
                        + childThatTryToUpdate.getLastname().toUpperCase() + " est déjà enregistré dans la base de données!")))
                .andDo(print());
    }


    @Test
    void deleteChildTest_thenReturnSuccessMessage() throws Exception {
        //GIVEN
        Child childToDelete = new Child(
                1, "Bernard", "Shanna", "12/01/2020",
                "02/05/2020", "http://image.jpeg", "christine@email.fr");
        childServiceTest.addChild(childToDelete);
        //WHEN
        //THEN
        mockMvcChild.perform(MockMvcRequestBuilders.delete("/child/delete/1"))
                .andExpect(status().isOk())
                .andDo(print());

        //Child childToDeleteInService = childServiceTest.addChild(childToDelete);
        String successMessage = childServiceTest.deleteChildById(1);
        assertEquals("L'enfant a été supprimé avec succés!", successMessage);
    }
}

