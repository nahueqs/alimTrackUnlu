package com.unlu.alimtrack.services;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.unlu.alimtrack.repositories.ProduccionRepository;

@ExtendWith(MockitoExtension.class)
public class ProduccionServiceTest {

    @Mock
    private ProduccionRepository produccionRepository;

    @InjectMocks
    private ProduccionService produccionService;
}
