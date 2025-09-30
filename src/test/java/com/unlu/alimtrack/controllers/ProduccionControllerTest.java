package com.unlu.alimtrack.controllers;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.unlu.alimtrack.services.ProduccionService;

@ExtendWith(MockitoExtension.class)
public class ProduccionControllerTest {

    @Mock
    private ProduccionService produccionService;

    @InjectMocks
    private ProduccionController produccionController;
}
