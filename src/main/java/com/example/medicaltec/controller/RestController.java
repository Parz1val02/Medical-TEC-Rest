package com.example.medicaltec.controller;
import com.example.medicaltec.Entity.Cita;
import com.example.medicaltec.Entity.Usuario;
import com.example.medicaltec.dto.Citadto;
import com.example.medicaltec.repository.CitaRepository;
import com.example.medicaltec.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api")
@CrossOrigin
public class RestController {

    final UsuarioRepository usuarioRepository;
    final CitaRepository citaRepository;

    public RestController(UsuarioRepository usuarioRepository, CitaRepository citaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
    }

    @GetMapping(value = "/citas", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
    public List<Citadto> returnCitas(@RequestParam("dni")String dni){
        return citaRepository.historialCitasAgendadas(dni);
    }
}
