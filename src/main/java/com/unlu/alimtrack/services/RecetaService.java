package com.unlu.alimtrack.services;

import com.unlu.alimtrack.dtos.RecetaDTO;
import com.unlu.alimtrack.dtos.RecetaDTO2;
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
        System.out.println("Ejecutando getAllRecetas");
        return  recetaRepository.findAll()
                .stream()
                .map(RecetaDTO::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    //@Transactional(readOnly = true)
    public ArrayList<RecetaDTO> getAllRecetas() {
        System.out.println("Ejecutando getAllRecetas");
        return  recetaRepository.findAll()
                .stream()
                .map(RecetaDTO::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    //@Transactional(readOnly = true)
    public ArrayList<RecetaDTO2> getAllRecetas2() {
        System.out.println("Ejecutando getAllRecetas2");
        return  recetaRepository.findAll()
                .stream()
                .map(RecetaDTO2::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }


}
