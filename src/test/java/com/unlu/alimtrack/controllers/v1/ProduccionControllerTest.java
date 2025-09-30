package com.unlu.alimtrack.controllers.v1;

import com.unlu.alimtrack.services.ProduccionService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProduccionControllerTest {

  @Mock
  private ProduccionService produccionService;

  @InjectMocks
  private ProduccionController produccionController;
}
