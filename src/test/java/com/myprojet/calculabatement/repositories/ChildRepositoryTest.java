package com.myprojet.calculabatement.repositories;

import com.myprojet.calculabatement.models.Child;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@ActiveProfiles("test")
@Sql(value = {"/abatementTest.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class ChildRepositoryTest {
    @Autowired
    private ChildRepository childRepositoryTest;

    @Test
    public void findChildByUserEmailTest_ThenReturnListWithTwoChildren() {
        // GIVEN
        List<Child> children = Arrays.asList(new Child(2, "Riboulet", "Manon", "30/11/2017", "01/03/2017", "christine@email.fr"),
                new Child(3, "Thomaset", "Lubin", "14/12/2020", "15/03/2020", "christine@email.fr"),
                new Child(4, "Babar", "Elephant", "12/05/2020", "02/05/2020", "sylvie@email.fr")
        );
        // WHEN
        childRepositoryTest.saveAll(children);
        List<Child> childrenByUserEmail = (List<Child>) childRepositoryTest.findChildrenByUserEmail("christine@email.fr");
        // THEN
        assertEquals(2, childrenByUserEmail.size());
    }



}
