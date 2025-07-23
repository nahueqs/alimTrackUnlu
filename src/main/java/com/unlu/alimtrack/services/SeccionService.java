package com.unlu.alimtrack.services;

import com.unlu.alimtrack.models.SeccionModel;
import com.unlu.alimtrack.repositories.SeccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeccionService {
    @Autowired
    SeccionRepository seccionRepository;


}
