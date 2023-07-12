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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
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
        List<Citadto> consultasVirtuales = citaRepository.historialCitasAgendadasVirtual(dni);
        List<Citadto> consultasPresenciales = citaRepository.historialCitasAgendadasPresencial(dni);
        List<Citadto> examenes = citaRepository.historialExamenesAgendados(dni);
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
        if(regex.dniValid(doctorDni)){
            dniDoctor = usuarioRepository.validarUsuario(doctorDni);
        }
        if(regex.dniValid(pacienteDni)){
            dniPaciente = usuarioRepository.validarUsuario(pacienteDni);
        }
        if(idSede!=null && idTipoCita!=null  && dniDoctor!=null && dniPaciente!=null && regex.fechaValid(fecha) && regex.horaValid(hora) && (modalidad.equals("Presencial") || modalidad.equals("Virtual"))){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate parsedDate = LocalDate.parse(fecha, formatter);
            LocalDate currentDate = LocalDate.now();
            LocalDate tomorrow = currentDate.plusDays(1);
            if(parsedDate.isBefore(tomorrow)) {
                rspta.put("msg", "Ingresar una fecha futura");
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
                            System.out.println(enlace1);
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }

                        Optional<Usuario> usu = usuarioRepository.findById(pacienteDni);
                        String contenido1  = "\"<!DOCTYPE html>\\n\" +\n" +
                                "<html lang=\\\"en\\\" xmlns=\\\"http://www.w3.org/1999/xhtml\\\" xmlns:o=\\\"urn:schemas-microsoft-com:office:office\\\">\\n\" +\n" +
                                "  <meta charset=\"utf-8\">\n" +
                                "  <meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">\n" +
                                "  <meta name=\"x-apple-disable-message-reformatting\">\n" +
                                "  <title></title>\n" +
                                "  <!--[if mso]>\n" +
                                "  <style>\n" +
                                "    table {border-collapse:collapse;border-spacing:0;border:none;margin:0;}\n" +
                                "    div, td {padding:0;}\n" +
                                "    div {margin:0 !important;}\n" +
                                "  </style>\n" +
                                "  <noscript>\n" +
                                "    <xml>\n" +
                                "      <o:OfficeDocumentSettings>\n" +
                                "        <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
                                "      </o:OfficeDocumentSettings>\n" +
                                "    </xml>\n" +
                                "  </noscript>\n" +
                                "  <![endif]-->\n" +
                                "  <style>\n" +
                                "    table, td, div, h1, p {\n" +
                                "      font-family: Arial, sans-serif;\n" +
                                "    }\n" +
                                "    @media screen and (max-width: 530px) {\n" +
                                "      .unsub {\n" +
                                "        display: block;\n" +
                                "        padding: 8px;\n" +
                                "        margin-top: 14px;\n" +
                                "        border-radius: 6px;\n" +
                                "        background-color: #555555;\n" +
                                "        text-decoration: none !important;\n" +
                                "        font-weight: bold;\n" +
                                "      }\n" +
                                "      .col-lge {\n" +
                                "        max-width: 100% !important;\n" +
                                "      }\n" +
                                "    }\n" +
                                "    @media screen and (min-width: 531px) {\n" +
                                "      .col-sml {\n" +
                                "        max-width: 27% !important;\n" +
                                "      }\n" +
                                "      .col-lge {\n" +
                                "        max-width: 73% !important;\n" +
                                "      }\n" +
                                "    }\n" +
                                "  </style>\n" +
                                "</head>\n" +
                                "<body style=\"margin:0;padding:0;word-spacing:normal;background-color:#939297;\">\n" +
                                "  <div role=\"article\" aria-roledescription=\"email\" lang=\"en\" style=\"text-size-adjust:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;background-color:#939297;\">\n" +
                                "    <table role=\"presentation\" style=\"width:100%;border:none;border-spacing:0;\">\n" +
                                "      <tr>\n" +
                                "        <td align=\"center\" style=\"padding:0;\">\n" +
                                "          <!--[if mso]>\n" +
                                "          <table role=\"presentation\" align=\"center\" style=\"width:600px;\">\n" +
                                "          <tr>\n" +
                                "          <td>\n" +
                                "          <![endif]-->\n" +
                                "          <table role=\"presentation\" style=\"width:94%;max-width:600px;border:none;border-spacing:0;text-align:left;font-family:Arial,sans-serif;font-size:16px;line-height:22px;color:#363636;\">\n" +
                                "            <tr>\n" +
                                "              <td style=\"padding:40px 30px 30px 30px;text-align:center;font-size:24px;font-weight:bold;\">\n" +
                                "                <a href=\"http://www.example.com/\" style=\"text-decoration:none;\"><img src=\"https://assets.codepen.io/210284/logo.png\" width=\"165\" alt=\"Logo\" style=\"width:165px;max-width:80%;height:auto;border:none;text-decoration:none;color:#ffffff;\"></a>\n" +
                                "              </td>\n" +
                                "            </tr>\n" +
                                "            <tr>\n" +
                                "              <td style=\"padding:30px;background-color:#ffffff;\">\n" +
                                "                <h1 style=\"margin-top:0;margin-bottom:16px;font-size:26px;line-height:32px;font-weight:bold;letter-spacing:-0.02em;\">Bienvenido a la plataforma de Medical-TEC!</h1>\n" +
                                "                <p style=\"margin:0;\"> TEXTO DE BIENVENIDA <a href=\"http://www.example.com/\" style=\"color:#e50d70;text-decoration:underline;\"></a></p>\n" +
                                " <p style=\"margin:0;\"> El enlace de su cita virtual es " + enlace1 + " </p> " +
                                "              </td>\n" +
                                "            </tr>\n" +
                                "            <tr>\n" +
                                "              <td style=\"padding:0;font-size:24px;line-height:28px;font-weight:bold;\">\n" +
                                "                <a href=\"http://www.example.com/\" style=\"text-decoration:none;\"><img src=\"https://assets.codepen.io/210284/1200x800-2.png\" width=\"600\" alt=\"\" style=\"width:100%;height:auto;display:block;border:none;text-decoration:none;color:#363636;\"></a>\n" +
                                "              </td>\n" +
                                "            </tr>\n" +
                                "            <tr>\n" +
                                "              <td style=\"padding:35px 30px 11px 30px;font-size:0;background-color:#ffffff;border-bottom:1px solid #f0f0f5;border-color:rgba(201,201,207,.35);\">\n" +
                                "                <!--[if mso]>\n" +
                                "                <table role=\"presentation\" width=\"100%\">\n" +
                                "                <tr>\n" +
                                "                <td style=\"width:145px;\" align=\"left\" valign=\"top\">\n" +
                                "                <![endif]-->\n" +
                                "                <div class=\"col-sml\" style=\"display:inline-block;width:100%;max-width:145px;vertical-align:top;text-align:left;font-family:Arial,sans-serif;font-size:14px;color:#363636;\">\n" +
                                "                  <img src=\"https://assets.codepen.io/210284/icon.png\" width=\"115\" alt=\"\" style=\"width:115px;max-width:80%;margin-bottom:20px;\">\n" +
                                "                </div>\n" +
                                "                <!--[if mso]>\n" +
                                "                </td>\n" +
                                "                <td style=\"width:395px;padding-bottom:20px;\" valign=\"top\">\n" +
                                "                <![endif]-->\n" +
                                "                <div class=\"col-lge\" style=\"display:inline-block;width:100%;max-width:395px;vertical-align:top;padding-bottom:20px;font-family:Arial,sans-serif;font-size:16px;line-height:22px;color:#363636;\">\n" +
                                "                  <p style=\"margin-top:0;margin-bottom:12px;\">Nullam mollis sapien vel cursus fermentum. Integer porttitor augue id ligula facilisis pharetra. In eu ex et elit ultricies ornare nec ac ex. Mauris sapien massa, placerat non venenatis et, tincidunt eget leo.</p>\n" +
                                "                  <p style=\"margin-top:0;margin-bottom:18px;\">Nam non ante risus. Vestibulum vitae eleifend nisl, quis vehicula justo. Integer viverra efficitur pharetra. Nullam eget erat nibh.</p>\n" +
                                "                  <p style=\"margin:0;\"><a href=\"https://example.com/\" style=\"background: #ff3884; text-decoration: none; padding: 10px 25px; color: #ffffff; border-radius: 4px; display:inline-block; mso-padding-alt:0;text-underline-color:#ff3884\"><!--[if mso]><i style=\"letter-spacing: 25px;mso-font-width:-100%;mso-text-raise:20pt\">&nbsp;</i><![endif]--><span style=\"mso-text-raise:10pt;font-weight:bold;\">Claim yours now</span><!--[if mso]><i style=\"letter-spacing: 25px;mso-font-width:-100%\">&nbsp;</i><![endif]--></a></p>\n" +
                                "                </div>\n" +
                                "                <!--[if mso]>\n" +
                                "                </td>\n" +
                                "                </tr>\n" +
                                "                </table>\n" +
                                "                <![endif]-->\n" +
                                "              </td>\n" +
                                "            </tr>\n" +
                                "            <tr>\n" +
                                "              <td style=\"padding:30px;font-size:24px;line-height:28px;font-weight:bold;background-color:#ffffff;border-bottom:1px solid #f0f0f5;border-color:rgba(201,201,207,.35);\">\n" +
                                "                <a href=\"http://www.example.com/\" style=\"text-decoration:none;\"><img src=\"https://assets.codepen.io/210284/1200x800-1.png\" width=\"540\" alt=\"\" style=\"width:100%;height:auto;border:none;text-decoration:none;color:#363636;\"></a>\n" +
                                "              </td>\n" +
                                "            </tr>\n" +
                                "            <tr>\n" +
                                "              <td style=\"padding:30px;background-color:#ffffff;\">\n" +
                                "                <p style=\"margin:0;\">Duis sit amet accumsan nibh, varius tincidunt lectus. Quisque commodo, nulla ac feugiat cursus, arcu orci condimentum tellus, vel placerat libero sapien et libero. Suspendisse auctor vel orci nec finibus.</p>\n" +
                                "              </td>\n" +
                                "            </tr>\n" +
                                "            <tr>\n" +
                                "              <td style=\"padding:30px;text-align:center;font-size:12px;background-color:#404040;color:#cccccc;\">\n" +
                                "                <p style=\"margin:0 0 8px 0;\"><a href=\"http://www.facebook.com/\" style=\"text-decoration:none;\"><img src=\"https://assets.codepen.io/210284/facebook_1.png\" width=\"40\" height=\"40\" alt=\"f\" style=\"display:inline-block;color:#cccccc;\"></a> <a href=\"http://www.twitter.com/\" style=\"text-decoration:none;\"><img src=\"https://assets.codepen.io/210284/twitter_1.png\" width=\"40\" height=\"40\" alt=\"t\" style=\"display:inline-block;color:#cccccc;\"></a></p>\n" +
                                "                <p style=\"margin:0;font-size:14px;line-height:20px;\">&reg; Someone, Somewhere 2021<br><a class=\"unsub\" href=\"http://www.example.com/\" style=\"color:#cccccc;text-decoration:underline;\">Unsubscribe instantly</a></p>\n" +
                                "              </td>\n" +
                                "            </tr>\n" +
                                "          </table>\n" +
                                "          <!--[if mso]>\n" +
                                "          </td>\n" +
                                "          </tr>\n" +
                                "          </table>\n" +
                                "          <![endif]-->\n" +
                                "        </td>\n" +
                                "      </tr>\n" +
                                "    </table>\n" +
                                "  </div>\n" +
                                "</body>\n" +
                                "</html>";
                        //correoConEstilos.sendEmailEstilos(usu.get().getEmail(),"enlace",contenido);
                        if (modalidad.equalsIgnoreCase("virtual") ){
                            try {
                                correoConEstilos.sendEmailEstilos(usu.get().getEmail(),"enlace",contenido1);
                            } catch (MessagingException e) {
                                // Manejar la excepción en caso de que ocurra un error al enviar el correo
                                e.printStackTrace();
                            }
                            reunionVirtualRepository.guardarReunion(enlace1,  citaRepository.ultimaCita().getId());

                        }

                        citaRepository.guardarConsultaMedica(idSede,idEspecialidad,formapago,modalidad,idTipoCita,fecha,hora,dniPaciente,dniDoctor);
                        rspta.put("msg", "Consulta médica agendada de manera exitosa");
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
                TimeListGenerationExample timeListGenerationExample = new TimeListGenerationExample();
                Fechas fechasFunciones = new Fechas();
                ArrayList<LocalDate> fechasAtienden = new ArrayList<>();
                LocalDate currentDate = LocalDate.now();
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
                    LocalDate currentDateGa = currentDate.plusDays(1);
                    while (!currentDateGa.isAfter(endDate)) {
                        String dayWeekGa = currentDateGa.getDayOfWeek().name();
                        String diaSemanaGa = fechasFunciones.traducirDia(dayWeekGa);
                        for (String value : values) {
                            if(value.equalsIgnoreCase(diaSemanaGa)){
                                fechasAtienden.add(currentDateGa);
                                System.out.println(dni + ": " + currentDateGa);
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
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        String dateString = fechasAtienden.get(x).format(formatter);
                        List<String> horasOcupadas = citaRepository.horasCitasProgramdas(dateString, dni);
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
                        //Continuar
                        HorariosDia horariosDia = new HorariosDia();
                        horariosDia.setDia(fechasAtienden.get(x));
                        horariosDia.setHoras(horasTrabajo);
                        listaHorariosDia.add(horariosDia);
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
