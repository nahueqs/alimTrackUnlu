package com.unlu.alimtrack.controllers;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.unlu.alimtrack.services.VersionRecetaService;

@ExtendWith(MockitoExtension.class)
public class VersionRecetaControllerTest {

    @Mock
    private VersionRecetaService versionRecetaService;

    @InjectMocks
    private VersionRecetaController versionRecetaController;
}
