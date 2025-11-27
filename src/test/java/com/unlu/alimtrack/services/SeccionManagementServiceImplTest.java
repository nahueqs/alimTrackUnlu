package com.unlu.alimtrack.services;

import com.unlu.alimtrack.services.impl.SeccionManagementServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.unlu.alimtrack.repositories.SeccionRepository;

@ExtendWith(MockitoExtension.class)
public class SeccionManagementServiceImplTest {

    @Mock
    private SeccionRepository seccionRepository;

    @InjectMocks
    private SeccionManagementServiceImpl seccionManagementServiceImpl;
}
