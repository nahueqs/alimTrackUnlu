package com.unlu.alimtrack.services;

import com.unlu.alimtrack.repositories.SeccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeccionService {
    private final SeccionRepository seccionRepository;

    @Autowired
    public SeccionService(SeccionRepository seccionRepository) {
        this.seccionRepository = seccionRepository;
    }
}
