package com.unlu.alimtrack.services;

import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VersionRecetaMetadataServiceTest {

    @Mock
    private VersionRecetaRepository versionRecetaRepository;

    @InjectMocks
    private VersionRecetaMetadataService versionRecetaMetadataService;
}
