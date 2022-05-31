package com.myprojet.calculabatement.IT;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(value = {"/abatementTest.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class MonthlyTestIT {
}
