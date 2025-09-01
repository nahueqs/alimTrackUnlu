package com.unlu.alimtrack.services;

import com.unlu.alimtrack.repositories.SeccionRepository;
import org.springframework.stereotype.Service;

@Service
public class SeccionService {
    private final SeccionRepository seccionRepository;

    public SeccionService(SeccionRepository seccionRepository) {
        this.seccionRepository = seccionRepository;
    }


}
