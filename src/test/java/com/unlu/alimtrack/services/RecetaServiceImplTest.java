package com.unlu.alimtrack.services;

import com.unlu.alimtrack.services.impl.RecetaServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.unlu.alimtrack.repositories.RecetaRepository;

@ExtendWith(MockitoExtension.class)
public class RecetaServiceImplTest {

    @Mock
    private RecetaRepository recetaRepository;

    @InjectMocks
    private RecetaServiceImpl recetaServiceImpl;
}
