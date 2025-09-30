package com.unlu.alimtrack.controllers;

import static org.mockito.Mockito.verify;

import com.unlu.alimtrack.dtos.response.UsuarioResponseDTO;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.unlu.alimtrack.services.UsuarioService;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;


    @Test
    public void testGetAllUsuarios() {
        List<UsuarioResponseDTO> usuarios = List.of(
            new UsuarioResponseDTO(
                ""

            );
        );

        usuarioController.getAllUsuarios();
        verify(usuarioService).getAllUsuarios();




    }

    @Test
    public void testSaveUsuario()
    {

    }

    @Test
    public void testUpdateUsuario() {

    }

    @Test
    public void testDeleteUsuario() {

    }

    @Test
    public void testGetUsuarioByEmail() {

    }

//    @Test
//    public void testGetUsuariosByTipoRolUsuario() {
//        usuarioController.getUsuariosByTipoRolUsuario(TipoRolUsuario.ADMIN);
//        verify(usuarioService).getUsuariosByTipoRolUsuario(TipoRolUsuario.ADMIN);
//    }


    @Test
    public void testGetUsuarioByEmailAndPassword() {

    }

}
