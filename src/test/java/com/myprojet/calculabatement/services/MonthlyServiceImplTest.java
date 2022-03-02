package com.myprojet.calculabatement.services;

import com.myprojet.calculabatement.repositories.MonthlyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MonthlyServiceImplTest {

    private MonthlyServiceImpl monthlyServiceTest;
    @Mock
    private MonthlyRepository monthlyRepositoryMock;

    @BeforeEach
    public void setPerTest() {
        monthlyServiceTest = new MonthlyServiceImpl(monthlyRepositoryMock);
    }

}
