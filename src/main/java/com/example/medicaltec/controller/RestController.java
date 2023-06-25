package com.example.medicaltec.controller;
import com.example.medicaltec.Entity.Cita;
import com.example.medicaltec.Entity.Sede;
import com.example.medicaltec.Entity.Usuario;
import com.example.medicaltec.dto.CitaDoctor;
import com.example.medicaltec.dto.Citadto;
import com.example.medicaltec.dto.CitasSede;
import com.example.medicaltec.dto.SedeDto;
import com.example.medicaltec.function.Regex;
import com.example.medicaltec.repository.CitaRepository;
import com.example.medicaltec.repository.SedeRepository;
import com.example.medicaltec.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@org.springframework.web.bind.annotation.RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
@CrossOrigin
public class RestController {

    final UsuarioRepository usuarioRepository;
    final CitaRepository citaRepository;

    final SedeRepository sedeRepository;
    public RestController(UsuarioRepository usuarioRepository, CitaRepository citaRepository, SedeRepository sedeRepository) {
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
        this.sedeRepository = sedeRepository;
    }

    @GetMapping(value = "/citas")
    public List<Citadto> returnCitas(@RequestParam("dni")String dni){
        return citaRepository.historialCitasAgendadas(dni);
    }
    @GetMapping(value = "/citasDoctor")
    public List<CitaDoctor> returnCitasDoctor(@RequestParam("dni")String dni){
        return citaRepository.historialCitasDoctor(dni);
    }
    @GetMapping(value = "/citasSede")
    public List<CitasSede> returnCitasSede(@RequestParam("idSede")String idSede){
        return citaRepository.historialCitasSede(idSede);
    }
    @PostMapping(value = "/cambioSede")
    public ResponseEntity<HashMap<String, Object>> CambiarSede(@RequestParam("dni") String dni,
                                                                 @RequestParam("id")String id){
        Regex regex = new Regex();
        HashMap<String, Object> rspta = new HashMap<>();
        try{
            int idInt = Integer.parseInt(id);
            if(regex.dniValid(dni)){
                Optional<Usuario> optProduct = usuarioRepository.findById(dni);
                if(optProduct.isPresent()){
                    rspta.put("msg", "Se actualizó la sede con éxito");
                    sedeRepository.cambiarSede(idInt,dni);
                    return ResponseEntity.ok(rspta);
                }else{
                    rspta.put("msg", "Error: el dni ingresado no existe");
                    return ResponseEntity.badRequest().body(rspta);
                }
            }else{
                rspta.put("msg", "Error: el dni debe tener un formato válido");
                return ResponseEntity.badRequest().body(rspta);
            }
        }catch (NumberFormatException e){
            rspta.put("msg", "Error: el id debe ser un entero positivo");
            return ResponseEntity.badRequest().body(rspta);
        }
    }

    //Exceptionhandlerpost
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String,String>> gestionException(HttpServletRequest request){
        HashMap<String,String> responseMap = new HashMap<>();
        if(request.getMethod().equals("POST")){
            responseMap.put("msg", "Error");
        }
        return ResponseEntity.badRequest().body(responseMap);
    }
}
