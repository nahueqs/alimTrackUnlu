package com.unlu.alimtrack.services;

import com.unlu.alimtrack.repositories.VersionRecetaRepository;
import com.unlu.alimtrack.services.impl.VersionRecetaMetadataServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VersionRecetaMetadataServiceImplTest {

    @Mock
    private VersionRecetaRepository versionRecetaRepository;

    @InjectMocks
    private VersionRecetaMetadataServiceImpl versionRecetaMetadataServiceImpl;
}
