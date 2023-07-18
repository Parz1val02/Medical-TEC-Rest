package com.example.medicaltec.controller;
import com.example.medicaltec.Entity.*;
import com.example.medicaltec.dto.*;
import com.example.medicaltec.function.*;
import com.example.medicaltec.repository.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.time.*;
import java.util.Base64;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
    final ReunionVirtualRepository reunionVirtualRepository;

    final CorreoConEstilos correoConEstilos;

    public RController(UsuarioRepository usuarioRepository, CitaRepository citaRepository, SedeRepository sedeRepository, TipoCitaRepository tipoCitaRepository, EspecialidadRepository especialidadRepository, ExamenMedicoRepository examenMedicoRepository, HorasDoctorRepository horasDoctorRepository, ReunionVirtualRepository reunionVirtualRepository, CorreoConEstilos correoConEstilos){
        this.usuarioRepository = usuarioRepository;
        this.citaRepository = citaRepository;
        this.sedeRepository = sedeRepository;
        this.tipoCitaRepository = tipoCitaRepository;
        this.examenMedicoRepository = examenMedicoRepository;
        this.especialidadRepository = especialidadRepository;
        this.horasDoctorRepository = horasDoctorRepository;
        this.reunionVirtualRepository = reunionVirtualRepository;
        this.correoConEstilos = correoConEstilos;
    }

    @GetMapping(value = "/citas")
    public List<Citadto> returnCitas(@RequestParam("dni")String dni){
        // Get the current date and time in America/Lima timezone
        Fechas fechasFunciones = new Fechas();
        ZoneId limaZone = ZoneId.of("America/Lima");
        LocalDate currentDate = LocalDate.now(limaZone);
        String month = currentDate.getMonth().name();
        String mes = fechasFunciones.traducirMes(month);

        List<Citadto> consultasVirtuales = citaRepository.historialCitasAgendadasVirtual(dni);
        List<Citadto> consultasPresenciales = citaRepository.historialCitasAgendadasPresencial(dni, mes.toLowerCase());
        List<Citadto> examenes = citaRepository.historialExamenesAgendados(dni, mes.toLowerCase());
        List<Citadto> consultas = Stream.concat(consultasPresenciales.stream(), consultasVirtuales.stream()).toList();
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
            String sede = sedeRepository.verificaridSede(id);
            if(sede!=null){
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
            }else{
                rspta.put("msg", "Error: el id ingresado no existe");
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
            // Get the current date and time in America/Lima timezone
            ZoneId limaZone = ZoneId.of("America/Lima");
            LocalDate currentDate = LocalDate.now(limaZone);
            LocalTime currentTime = LocalTime.now(limaZone);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("HH:mm");

            String parsedTime = currentTime.format(formatter3);
            LocalDate parsedDate = LocalDate.parse(fecha, formatter);
            if(parsedDate.isBefore(currentDate)) {
                rspta.put("msg", "Ingresar una fecha a partir de hoy");
                return ResponseEntity.badRequest().body(rspta);
            }else{
                if(especialidadId!=null){
                    String idEspecialidad = especialidadRepository.verificarEspecialidad(especialidadId);
                    if(idEspecialidad!=null){
                        List<DoctorDto> doctores = usuarioRepository.obtenerDoctoresSedeEspecialidad(idSede,idEspecialidad);
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
                                        horasdoctorsAtienden.add(horasdoctors);
                                        break;
                                    }
                                }
                            }
                        }
                        ArrayList<SuperDoctor> superDoctors = new ArrayList<>();
                        for(int i=0;i<doctoresAtienden.size();i++){
                            List<String> horasOcupadas = new ArrayList<>();
                            Horasdoctor horasDoctor = horasdoctorsAtienden.get(i);
                            LocalTime start = horasDoctor.getHorainicio();
                            LocalTime end = horasDoctor.getHorafin();
                            LocalTime skip = horasDoctor.getHoralibre();
                            List<LocalTime> horasTrabajo = new ArrayList<>();
                            if(parsedDate.isEqual(currentDate)){
                                System.out.println(parsedTime + " aaa");
                                horasOcupadas = citaRepository.horasCitasProgramdasHoy(fecha, doctoresAtienden.get(i).getDni(), parsedTime);
                                LocalTime closestTime = null;
                                if(currentTime.getMinute()>30){
                                    closestTime= currentTime.plusMinutes(60-(currentTime.getMinute()))
                                            .withSecond(0)
                                            .withNano(0);
                                }else{
                                    closestTime= currentTime.plusMinutes(30-(currentTime.getMinute()))
                                            .withSecond(0)
                                            .withNano(0);
                                }
                                horasTrabajo = timeListGenerationExample.generateTimeList(closestTime.plusMinutes(60).withSecond(0).withNano(0), end, skip);
                            }else{
                                horasOcupadas = citaRepository.horasCitasProgramdas(fecha, doctoresAtienden.get(i).getDni());
                                horasTrabajo = timeListGenerationExample.generateTimeList(start, end, skip);
                            }
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
                            if(horasTrabajo.size()>0){
                                SuperDoctor superDoctor = new SuperDoctor();
                                superDoctor.setDoctorDto(doctoresAtienden.get(i));
                                superDoctor.setHoras(horasTrabajo);
                                superDoctors.add(superDoctor);
                            }
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
                        List<DoctorDto> doctores = usuarioRepository.obtenerDoctoresSede(idSede);
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
                                        horasdoctorsAtienden.add(horasdoctors);
                                        break;
                                    }
                                }
                            }
                        }
                        ArrayList<SuperDoctor> superDoctors = new ArrayList<>();
                        for(int i=0;i<doctoresAtienden.size();i++){
                            List<String> horasOcupadas = new ArrayList<>();
                            Horasdoctor horasDoctor = horasdoctorsAtienden.get(i);
                            LocalTime start = horasDoctor.getHorainicio();
                            LocalTime end = horasDoctor.getHorafin();
                            LocalTime skip = horasDoctor.getHoralibre();
                            List<LocalTime> horasTrabajo = new ArrayList<>();
                            if(parsedDate.isEqual(currentDate)){
                                System.out.println(parsedTime + " aaa");
                                horasOcupadas = citaRepository.horasCitasProgramdasHoy(fecha, doctoresAtienden.get(i).getDni(), parsedTime);
                                LocalTime closestTime = null;
                                if(currentTime.getMinute()>30){
                                    closestTime= currentTime.plusMinutes(60-(currentTime.getMinute()))
                                            .withSecond(0)
                                            .withNano(0);
                                }else{
                                    closestTime= currentTime.plusMinutes(30-(currentTime.getMinute()))
                                            .withSecond(0)
                                            .withNano(0);
                                }
                                horasTrabajo = timeListGenerationExample.generateTimeList(closestTime.plusMinutes(60).withSecond(0).withNano(0), end, skip);
                            }else{
                                horasOcupadas = citaRepository.horasCitasProgramdas(fecha, doctoresAtienden.get(i).getDni());
                                horasTrabajo = timeListGenerationExample.generateTimeList(start, end, skip);
                            }
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
                            if(horasTrabajo.size()>0){
                                SuperDoctor superDoctor = new SuperDoctor();
                                superDoctor.setDoctorDto(doctoresAtienden.get(i));
                                superDoctor.setHoras(horasTrabajo);
                                superDoctors.add(superDoctor);
                            }
                        }
                        ArrayList<String> modalidad = new ArrayList<>();
                        modalidad.add("Presencial");
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
                                                            @RequestParam("pacienteDni")String pacienteDni) throws MessagingException {
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
        if(regex.dniValid(doctorDni)){
            dniDoctor = usuarioRepository.validarUsuario(doctorDni);
        }
        if(regex.dniValid(pacienteDni)){
            dniPaciente = usuarioRepository.validarUsuario(pacienteDni);
        }
        if(idSede!=null && idTipoCita!=null  && dniDoctor!=null && dniPaciente!=null && regex.fechaValid(fecha) && regex.horaValid(hora) && (modalidad.equals("Presencial") || modalidad.equals("Virtual"))){
            // Get the current date and time in America/Lima timezone
            ZoneId limaZone = ZoneId.of("America/Lima");
            ZonedDateTime limaDateTime = ZonedDateTime.now(limaZone);

            // Extract local date and time components
            LocalDate currentDate = limaDateTime.toLocalDate();
            LocalTime currentTime = limaDateTime.toLocalTime();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            LocalDate parsedDate = LocalDate.parse(fecha, formatter);
            if(parsedDate.isBefore(currentDate)) {
                rspta.put("msg", "Ingresar una fecha a partir de hoy");
                return ResponseEntity.badRequest().body(rspta);
            }else{
                if(especialidadId!=null){
                    String idEspecialidad = especialidadRepository.verificarEspecialidad(especialidadId);
                    if(idEspecialidad!=null){
                        String formapago = null;
                        if(modalidad.equalsIgnoreCase("Presencial")){
                           formapago = "En caja";
                            citaRepository.guardarConsultaMedicaPresencial(idSede,idEspecialidad,formapago,modalidad,idTipoCita,fecha,hora,dniPaciente,dniDoctor);
                            /*correoConEstilos.sendEmailEstilos(usuarioRepository.findById(dniPaciente).get().getEmail(), "Notificacion de cita presencial","Usted agendó una cita presencial con el " + usuarioRepository.findById(dniDoctor).get().getNombre() + " "
                                    + usuarioRepository.findById(dniDoctor).get().getApellido() +  " de la especialidad de " +
                                    usuarioRepository.findById(dniDoctor).get().getEspecialidadesIdEspecialidad().getNombreEspecialidad());*/

                            //correoConEstilos.sendEmailEstilos(usuarioRepository.findById(dniPaciente).get().getEmail(), "Notificacion de cita presencial", "");
                            correoConEstilos.sendEmailNotification("Notificación de cita presencial",usuarioRepository.findById(dniPaciente).get(),citaRepository.ultimaCita());
                            rspta.put("msg", "Consulta médica agendada de manera exitosa");
                        }
                        if (modalidad.equalsIgnoreCase("Virtual") ){
                            formapago = "Tarjeta";
                            //correo
                            ClasePrueba meet = new ClasePrueba();
                            LocalTime endTime =LocalTime.parse(hora).plusMinutes(30) ; //sumarle 30 min a la hora
                            endTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                        /*String enlace = meet.generateScheduledMeetingLink(
                                "vpaas-magic-cookie-64547877cba34cdb892bd4fb58d11524","salapersonal",
                                LocalTime.parse(hora), endTime, "vpaas-magic-cookie-64547877cba34cdb892bd4fb58d11524/aae24d");*/

                            String domain = "https://meet.jit.si/";

                            String enlace1;
                            try {
                                enlace1= meet.generateJitsiMeetURL(domain,"ReunionesMedicalTec",LocalTime.parse(hora), 30, parsedDate);
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }

                            Optional<Usuario> usu = usuarioRepository.findById(pacienteDni);
                            //correoConEstilos.sendEmailEstilos(usu.get().getEmail(),"enlace",contenido);
                            reunionVirtualRepository.guardarReunion(enlace1);
                            citaRepository.guardarConsultaMedicaVirtual(idSede,idEspecialidad,formapago,modalidad,idTipoCita,fecha,hora,dniPaciente,dniDoctor,reunionVirtualRepository.ultimaReunion().getId());
                            Cita cita = citaRepository.ultimaCita();
                            rspta.put("msg", "Consulta médica agendada de manera exitosa");
                            try {
                                correoConEstilos.sendEmailEstilos(usu.get().getEmail() , "Notificacion de cita virtual " + cita.getEspecialidadesIdEspecialidad().getNombreEspecialidad(), "Usted ha reservado una cita virtual para la fecha" + cita.getFecha()+ " a las " + cita.getHora() +". El enlace de la reunión es el siguiente: " + enlace1);
                            } catch (MessagingException e) {
                                // Manejar la excepción en caso de que ocurra un error al enviar el correo
                                e.printStackTrace();
                            }
                        }
                        //reunionVirtualRepository.guardarReunion(enlace1,  citaRepository.ultimaCita().getId());
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
                        citaRepository.guardarExamenMedico(idSede,formapago,modalidad,idTipoCita,fecha,hora,dniPaciente,dniDoctor,idExamen);
                        Cita cita = citaRepository.ultimaCita();
                        correoConEstilos.sendEmailEstilos(usuarioRepository.findById(dniPaciente).get().getEmail(), "Notificación de registro de examen médico" + cita.getExamenMedico().getNombre(), "Usted agendó un examen médico para la fecha " + cita.getFecha() + " a las " + cita.getHora() + ".");
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
    /*@PostMapping("/meetingLink")
    public String enlaceReunionVirtual(String idCita){


        ReunionVirtual reunionVirtual= reunionVirtualRepository.ReuPorCita(Integer.valueOf(idCita));
        Optional<Cita> optCita = citaRepository.findById(Integer.valueOf(idCita));
        //optCita.get();
        MeetingLinkGenerator meet = new MeetingLinkGenerator();
        LocalTime endTime ; //sumarle 30 min a la hora

        String enlace = meet.generateScheduledMeetingLink(
                reunionVirtual.getRoom(),"salapersonal",
                optCita.get().getHora(), optCita.get().getHora().plusMinutes(30), "vpaas-magic-cookie-64547877cba34cdb892bd4fb58d11524/aae24d" );

        return enlace;

    }*/
    @GetMapping("/doctores")
    public ResponseEntity<HashMap<String, Object>> listaDoctores(@RequestParam("dni") String dni){
        Regex regex = new Regex();
        HashMap<String, Object> rspta = new HashMap<>();
        String dniDoctor = null;
        if(regex.dniValid(dni)){
            dniDoctor = usuarioRepository.validarUsuario(dni);
            if(dniDoctor!=null){
                // Get the current date and time in America/Lima timezone
                ZoneId limaZone = ZoneId.of("America/Lima");
                ZonedDateTime limaDateTime = ZonedDateTime.now(limaZone);

                // Extract local date and time components
                LocalDate currentDate = limaDateTime.toLocalDate();
                LocalTime currentTime = limaDateTime.toLocalTime();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("HH:mm");


                TimeListGenerationExample timeListGenerationExample = new TimeListGenerationExample();
                Fechas fechasFunciones = new Fechas();
                ArrayList<LocalDate> fechasAtienden = new ArrayList<>();
                int year = currentDate.getYear();
                String month = currentDate.getMonth().name();
                int numMonth = fechasFunciones.traducirMesNumero(month);
                String mes = fechasFunciones.traducirMes(month);
                Horasdoctor horasDoctor = horasDoctorRepository.DniMes(dni,mes.toLowerCase());
                HorariosMes horariosMes = new HorariosMes();
                if(horasDoctor!=null){
                    LocalDate startDate = LocalDate.of(year,numMonth, 1);
                    // Get the last day of the month
                    LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                    String[] values = horasDoctor.getDias().split(",");
                    // Iterate through the dates
                    LocalDate currentDateGa = currentDate;
                    while (!currentDateGa.isAfter(endDate)) {
                        String dayWeekGa = currentDateGa.getDayOfWeek().name();
                        String diaSemanaGa = fechasFunciones.traducirDia(dayWeekGa);
                        for (String value : values) {
                            if(value.equalsIgnoreCase(diaSemanaGa)){
                                fechasAtienden.add(currentDateGa);
                                break;
                            }
                        }
                        currentDateGa = currentDateGa.plusDays(1);
                    }
                    //continuar
                    List<HorariosDia> listaHorariosDia = new ArrayList<>();
                    LocalTime start = horasDoctor.getHorainicio();
                    LocalTime end = horasDoctor.getHorafin();
                    LocalTime skip = horasDoctor.getHoralibre();
                    for(int x=0;x<fechasAtienden.size();x++){
                        // Create a formatter with the desired date pattern
                        String parsedTime = currentTime.format(formatter3);
                        String dateString = fechasAtienden.get(x).format(formatter);
                        List<String> horasOcupadas = new ArrayList<>();
                        List<LocalTime> horasTrabajo = new ArrayList<>();
                        if(fechasAtienden.get(x).isEqual(currentDate)){
                            System.out.println(parsedTime + " aaa");
                            horasOcupadas = citaRepository.horasCitasProgramdasHoy(dateString, dni, parsedTime);
                            LocalTime closestTime = null;
                            if(currentTime.getMinute()>30){
                             closestTime= currentTime.plusMinutes(60-(currentTime.getMinute()))
                                        .withSecond(0)
                                        .withNano(0);
                            }else{
                                closestTime= currentTime.plusMinutes(30-(currentTime.getMinute()))
                                        .withSecond(0)
                                        .withNano(0);
                            }
                            horasTrabajo = timeListGenerationExample.generateTimeList(closestTime.plusMinutes(60).withSecond(0).withNano(0), end, skip);
                        }else{
                            horasOcupadas = citaRepository.horasCitasProgramdas(dateString, dni);
                            horasTrabajo = timeListGenerationExample.generateTimeList(start, end, skip);
                        }
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
                        //Continuar
                        if(horasTrabajo.size()>0){
                            HorariosDia horariosDia = new HorariosDia();
                            horariosDia.setDia(fechasAtienden.get(x));
                            horariosDia.setHoras(horasTrabajo);
                            listaHorariosDia.add(horariosDia);
                        }
                    }
                    //Continuar
                    horariosMes.setDiasDelMes(listaHorariosDia);
                    horariosMes.setDoctorDni(dni);
                } else {
                    rspta.put("msg", "Error en el ingreso de parámetros");
                    return ResponseEntity.badRequest().body(rspta);
                }
                rspta.put("horariosMes", horariosMes);
                return ResponseEntity.ok(rspta);
            }else{
                rspta.put("msg", "Error: el dni ingresado no existe");
                return ResponseEntity.badRequest().body(rspta);
            }
        }else{
            rspta.put("msg", "Error: el dni debe tener un formato válido");
            return ResponseEntity.badRequest().body(rspta);
        }
    }
}
