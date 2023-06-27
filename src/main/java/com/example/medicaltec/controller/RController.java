package com.example.medicaltec.controller;
import com.example.medicaltec.Entity.*;
import com.example.medicaltec.dto.*;
import com.example.medicaltec.function.Fechas;
import com.example.medicaltec.function.Regex;
import com.example.medicaltec.function.TimeListGenerationExample;
import com.example.medicaltec.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8")
@CrossOrigin
public class RController {

    final UsuarioRepository usuarioRepository;
    final CitaRepository citaRepository;

    final SedeRepository sedeRepository;

    final TipoCitaRepository tipoCitaRepository;
    final EspecialidadRepository especialidadRepository;
    final ExamenMedicoRepository examenMedicoRepository;
    final HorasDoctorRepository horasDoctorRepository;
    public RController(UsuarioRepository usuarioRepository, CitaRepository citaRepository, SedeRepository sedeRepository, TipoCitaRepository tipoCitaRepository, EspecialidadRepository especialidadRepository, ExamenMedicoRepository examenMedicoRepository, HorasDoctorRepository horasDoctorRepository){
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
        this.sedeRepository = sedeRepository;
        this.tipoCitaRepository = tipoCitaRepository;
        this.examenMedicoRepository = examenMedicoRepository;
        this.especialidadRepository = especialidadRepository;
        this.horasDoctorRepository = horasDoctorRepository;
    }

    @GetMapping(value = "/citas")
    public List<Citadto> returnCitas(@RequestParam("dni")String dni){
        List<Citadto> consultas = citaRepository.historialCitasAgendadas(dni);
        List<Citadto> examenes = citaRepository.historialExamenesAgendados(dni);
        return Stream.concat(consultas.stream(), examenes.stream()).toList();
    }
    @GetMapping(value = "/citasDoctor")
    public List<CitaDoctor> returnCitasDoctor(@RequestParam("dni")String dni){
        List<CitaDoctor> consultas = citaRepository.historialCitasDoctor(dni);
        List<CitaDoctor> examenes = citaRepository.historialExamenesDoctor(dni);
        return Stream.concat(consultas.stream(), examenes.stream()).toList();
    }
    @GetMapping(value = "/citasSede")
    public List<CitasSede> returnCitasSede(@RequestParam("idSede")String idSede){
        List<CitasSede> consultas = citaRepository.historialCitasSede(idSede);
        List<CitasSede> examenes = citaRepository.historialExamenesSede(idSede);
        return Stream.concat(consultas.stream(), examenes.stream()).toList();
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
    @GetMapping(value = "/agendar1")
    public ResponseEntity<HashMap<String, Object>> Agendar1(@RequestParam("sedeId")String sedeId,
                                                            @RequestParam("fecha") String fecha,
                                                            @RequestParam("tipoCitaId")String tipoCitaId,
                                                            @RequestParam(value = "especialidadId", required = false)String especialidadId,
                                                            @RequestParam(value = "examenId", required = false)String examenId){
        Regex regex = new Regex();
        Fechas fechasFunciones = new Fechas();
        TimeListGenerationExample timeListGenerationExample = new TimeListGenerationExample();
        HashMap<String, Object> rspta = new HashMap<>();
        String idSede = null;
        String idTipoCita = null;
        if(sedeId!=null){
           idSede = sedeRepository.verificaridSede(sedeId);
        }
        if(tipoCitaId!=null){
            idTipoCita = tipoCitaRepository.verificarTipoCita(tipoCitaId);
        }
        if(idSede!=null && idTipoCita!=null && regex.fechaValid(fecha)){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate parsedDate = LocalDate.parse(fecha, formatter);
            LocalDate currentDate = LocalDate.now();
            if(parsedDate.isBefore(currentDate)) {
                rspta.put("msg", "Ingresar una fecha a partir de hoy");
                return ResponseEntity.badRequest().body(rspta);
            }else{
                if(especialidadId!=null){
                    String idEspecialidad = especialidadRepository.verificarEspecialidad(especialidadId);
                    if(idEspecialidad!=null){
                        List<DoctorDto> doctores = usuarioRepository.obtenerDoctoresSedeEspecialidad(Integer.parseInt(idSede), Integer.parseInt(idEspecialidad));
                        ArrayList<DoctorDto> doctoresAtienden = new ArrayList<>();
                        ArrayList<Horasdoctor> horasdoctorsAtienden = new ArrayList<>();
                        String dayWeek = parsedDate.getDayOfWeek().name();
                        String month = parsedDate.getMonth().name();
                        String mes = fechasFunciones.traducirMes(month);
                        String diaSemana = fechasFunciones.traducirDia(dayWeek);
                        for(int i=0; i<doctores.size(); i++){
                            Horasdoctor horasdoctors = horasDoctorRepository.DniMes(doctores.get(i).getDni(),mes.toLowerCase());
                            if(horasdoctors!=null){
                                String[] values = horasdoctors.getDias().split(",");
                                for (String value : values) {
                                    if(value.equalsIgnoreCase(diaSemana)){
                                        doctoresAtienden.add(doctores.get(i));
                                        break;
                                    }
                                }
                                horasdoctorsAtienden.add(horasdoctors);
                            }
                        }
                        ArrayList<SuperDoctor> superDoctors = new ArrayList<>();
                        for(int i=0;i<doctoresAtienden.size();i++){
                            List<String> horasOcupadas = citaRepository.horasCitasProgramdas(fecha, doctoresAtienden.get(i).getDni());
                            Horasdoctor horasDoctor = horasdoctorsAtienden.get(i);
                            LocalTime start = horasDoctor.getHorainicio();
                            LocalTime end = horasDoctor.getHorafin();
                            LocalTime skip = horasDoctor.getHoralibre();
                            List<LocalTime> horasTrabajo = timeListGenerationExample.generateTimeList(start, end, skip);
                            for(int j=0;j<horasOcupadas.size();j++){
                                String timeString = horasOcupadas.get(j);
                                String formatPattern = "HH:mm";
                                // Create a formatter based on the desired format pattern
                                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(formatPattern);
                                // Parse the time string to a LocalTime object
                                LocalTime hora = LocalTime.parse(timeString, formatter2);
                                for(int k=0;k<horasTrabajo.size();k++){
                                    if(hora.equals(horasTrabajo.get(k))){
                                        LocalTime aa = horasTrabajo.remove(k);
                                        break;
                                    }
                                }
                            }
                            SuperDoctor superDoctor = new SuperDoctor();
                            superDoctor.setDoctorDto(doctoresAtienden.get(i));
                            superDoctor.setHoras(horasTrabajo);
                            superDoctors.add(superDoctor);
                        }
                        ArrayList<String> modalidad = new ArrayList<>();
                        modalidad.add("Presencial");
                        modalidad.add("Virtual");
                        rspta.put("modalidades", modalidad);
                        rspta.put("infoDoctores", superDoctors);
                        return ResponseEntity.ok(rspta);
                    }else{
                        rspta.put("msg", "Error en el ingreso de parámetros");
                        return ResponseEntity.badRequest().body(rspta);
                    }
                } else if (examenId!=null) {
                    String idExamen = examenMedicoRepository.verificarExamen(examenId);
                    if (idExamen != null) {
                        List<DoctorDto> doctores = usuarioRepository.obtenerDoctoresSede(Integer.parseInt(idSede));
                        ArrayList<DoctorDto> doctoresAtienden = new ArrayList<>();
                        ArrayList<Horasdoctor> horasdoctorsAtienden = new ArrayList<>();
                        String dayWeek = parsedDate.getDayOfWeek().name();
                        String month = parsedDate.getMonth().name();
                        String mes = fechasFunciones.traducirMes(month);
                        String diaSemana = fechasFunciones.traducirDia(dayWeek);
                        for(int i=0; i<doctores.size(); i++){
                            Horasdoctor horasdoctors = horasDoctorRepository.DniMes(doctores.get(i).getDni(),mes.toLowerCase());
                            if(horasdoctors!=null){
                                String[] values = horasdoctors.getDias().split(",");
                                for (String value : values) {
                                    if(value.equalsIgnoreCase(diaSemana)){
                                        doctoresAtienden.add(doctores.get(i));
                                        break;
                                    }
                                }
                                horasdoctorsAtienden.add(horasdoctors);
                            }
                        }
                        ArrayList<SuperDoctor> superDoctors = new ArrayList<>();
                        for(int i=0;i<doctoresAtienden.size();i++){
                            List<String> horasOcupadas = citaRepository.horasCitasProgramdas(fecha, doctoresAtienden.get(i).getDni());
                            Horasdoctor horasDoctor = horasdoctorsAtienden.get(i);
                            LocalTime start = horasDoctor.getHorainicio();
                            LocalTime end = horasDoctor.getHorafin();
                            LocalTime skip = horasDoctor.getHoralibre();
                            List<LocalTime> horasTrabajo = timeListGenerationExample.generateTimeList(start, end, skip);
                            for(int j=0;j<horasOcupadas.size();j++){
                                String timeString = horasOcupadas.get(j);
                                String formatPattern = "HH:mm";
                                // Create a formatter based on the desired format pattern
                                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(formatPattern);
                                // Parse the time string to a LocalTime object
                                LocalTime hora = LocalTime.parse(timeString, formatter2);
                                for(int k=0;k<horasTrabajo.size();k++){
                                    if(hora.equals(horasTrabajo.get(k))){
                                        LocalTime aa = horasTrabajo.remove(k);
                                        break;
                                    }
                                }
                            }
                            SuperDoctor superDoctor = new SuperDoctor();
                            superDoctor.setDoctorDto(doctoresAtienden.get(i));
                            superDoctor.setHoras(horasTrabajo);
                            superDoctors.add(superDoctor);
                        }
                        ArrayList<String> modalidad = new ArrayList<>();
                        modalidad.add("Presencial");
                        modalidad.add("Virtual");
                        rspta.put("modalidades", modalidad);
                        rspta.put("infoDoctores", superDoctors);
                        return ResponseEntity.ok(rspta);
                    } else {
                        rspta.put("msg", "Error en el ingreso de parámetros");
                        return ResponseEntity.badRequest().body(rspta);
                    }
                }
            }
        }
        rspta.put("msg", "Error en el ingreso de parámetros");
        return ResponseEntity.badRequest().body(rspta);
    }
    @GetMapping("/htmlSegundoForm")
    public ResponseEntity<String> getHTML() {
        String htmlContent = "                           <div class=\"row\">\n" +
                "                               <div class=\"col-sm-6\">\n" +
                "                                   <div class=\"form-group\">\n" +
                "                                       <label for=\"modalidad\">Modalidad:</label>\n" +
                "                                       <select  class=\"form-control\" name=\"modalidad\" id=\"modalidad\">\n" +
                "                                       </select>\n" +
                "                                   </div>\n" +
                "                               </div>\n" +
                "                               <div class=\"col-sm-6\">\n" +
                "                                   <div class=\"form-group\">\n" +
                "                                       <label for=\"doctor\">Doctor: </label>\n" +
                "                                       <select  class=\"form-control\" name=\"doctor\" id=\"doctor\">\n" +
                "                                       </select>\n" +
                "                                   </div>\n" +
                "                               </div>\n" +
                "                           </div>\n" +
                "                           <div class=\"row\" >\n" +
                "                               <div class=\"col-sm-12\">\n" +
                "                                   <div class=\"form-group\">\n" +
                "                                       <label for=\"hora\">Hora:</label>\n" +
                "                                       <select  class=\"form-control\" name=\"hora\" id=\"hora\">\n" +
                "                                       </select>\n" +
                "                                   </div>\n" +
                "                               </div>\n" +
                "                           </div>\n" +
                "                           <!-- botón de envío -->\n" +
                "                           <button class=\"btn btn-primary submit\" id=\"clash\">Agendar</button>\n" +
                "                           <a class=\"btn btn-secondary\" style=\"text-align: center;background: #b38df7; border-radius: 15px;\" href=\"/paciente/agendarCita\">Cancelar</a>\n";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
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
    @PostMapping(value = "/agendar2")
    public ResponseEntity<HashMap<String, Object>> Agendar2(@RequestParam("sedeId")String sedeId,
                                                            @RequestParam("fecha") String fecha,
                                                            @RequestParam("tipoCitaId")String tipoCitaId,
                                                            @RequestParam(value = "especialidadId", required = false)String especialidadId,
                                                            @RequestParam(value = "examenId", required = false)String examenId,
                                                            @RequestParam("modalidad")String modalidad,
                                                            @RequestParam("doctorDni")String doctorDni,
                                                            @RequestParam("hora")String hora,
                                                            @RequestParam("pacienteDni")String pacienteDni){
        Regex regex = new Regex();
        HashMap<String, Object> rspta = new HashMap<>();
        String idSede = null;
        String idTipoCita = null;
        String dniDoctor = null;
        String dniPaciente = null;
        if(sedeId!=null){
            idSede = sedeRepository.verificaridSede(sedeId);
        }
        if(tipoCitaId!=null){
            idTipoCita = tipoCitaRepository.verificarTipoCita(tipoCitaId);
        }
        if(doctorDni!=null){
            dniDoctor = usuarioRepository.validarUsuario(doctorDni);
        }
        if(pacienteDni!=null){
            dniPaciente = usuarioRepository.validarUsuario(pacienteDni);
        }
        if(idSede!=null && idTipoCita!=null  && dniDoctor!=null && dniPaciente!=null && regex.fechaValid(fecha) && regex.horaValid(hora) && (modalidad.equals("Presencial") || modalidad.equals("Virtual"))){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate parsedDate = LocalDate.parse(fecha, formatter);
            LocalDate currentDate = LocalDate.now();
            if(parsedDate.isBefore(currentDate)) {
                rspta.put("msg", "Ingresar una fecha a partir de hoy");
                return ResponseEntity.badRequest().body(rspta);
            }else{
                if(especialidadId!=null){
                    String idEspecialidad = especialidadRepository.verificarEspecialidad(especialidadId);
                    if(idEspecialidad!=null){
                        String formapago = null;
                        if(modalidad.equals("Presencial")){
                           formapago = "En caja";
                        }
                        if(modalidad.equals("Virtual")){
                            formapago = "Tarjeta";
                        }
                        citaRepository.guardarConsultaMedica(idSede,idEspecialidad,formapago,modalidad,idTipoCita,fecha,hora,dniPaciente,dniDoctor);
                        rspta.put("msg", "Consulta médica agendada de manera exitosa");
                        return ResponseEntity.ok(rspta);
                    }else{
                        rspta.put("msg", "Error en el ingreso de parámetros");
                        return ResponseEntity.badRequest().body(rspta);
                    }
                } else if (examenId!=null) {
                    String idExamen = examenMedicoRepository.verificarExamen(examenId);
                    if (idExamen != null) {
                        String formapago = null;
                        if(modalidad.equals("Presencial")){
                            formapago = "En caja";
                        }
                        if(modalidad.equals("Virtual")){
                            formapago = "Tarjeta";
                        }
                        citaRepository.guardarExamenMedico(idSede,formapago,modalidad,idTipoCita,fecha,hora,dniPaciente,dniDoctor,idExamen);
                        rspta.put("msg", "Examen médico agendado de manera exitosa");
                        return ResponseEntity.ok(rspta);
                    } else {
                        rspta.put("msg", "Error en el ingreso de parámetros");
                        return ResponseEntity.badRequest().body(rspta);
                    }
                }
            }
        }
        rspta.put("msg", "Error en el ingreso de parámetros");
        return ResponseEntity.badRequest().body(rspta);
    }
    /*@GetMapping("/meetingLink")
    public String enlaceReunionVirtual(){


        //MeetingLinkGenerator meet = new MeetingLinkGenerator();
        //meet.generateScheduledMeetingLink(,"salapersonal", );
    }*/
}
