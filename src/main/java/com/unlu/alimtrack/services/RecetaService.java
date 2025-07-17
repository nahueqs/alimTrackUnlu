package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.RecetaDTO;
import com.unlu.alimtrack.models.RecetaModel;
import com.unlu.alimtrack.repositories.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class RecetaService {
    @Autowired
    RecetaRepository recetaRepository;

    //@Transactional(readOnly = true)
    public ArrayList<RecetaDTO> getAllRecetas() {
        System.out.println("Ejecutando getAllRecetas");
        return  recetaRepository.findAll()
                .stream()
                .map(RecetaDTO::new)
                .collect(Collectors.toCollection(ArrayList::new));

    }
}
