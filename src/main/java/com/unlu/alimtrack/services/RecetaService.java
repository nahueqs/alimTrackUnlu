package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.RecetaDTO;
import com.unlu.alimtrack.repositories.RecetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class RecetaService {
    @Autowired
    RecetaRepository recetaRepository;

    //@Transactional(readOnly = true)
    public ArrayList<RecetaDTO> getAllRecetasDTOS() {
        System.out.println("Ejecutando getAllRecetas T");
        return  recetaRepository.findAll()
                .stream()
                .map(RecetaDTO::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
