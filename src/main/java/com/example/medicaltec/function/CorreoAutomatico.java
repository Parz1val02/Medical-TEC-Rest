package com.example.medicaltec.function;

import com.example.medicaltec.Entity.Cita;
import com.example.medicaltec.repository.CitaRepository;
import jakarta.mail.MessagingException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
@EnableAsync
@EnableScheduling
@Component
public class CorreoAutomatico {

    final CorreoConEstilos correoConEstilos;
    final CitaRepository citaRepository;
    public CorreoAutomatico(CorreoConEstilos correoConEstilos, CitaRepository citaRepository) {
        this.correoConEstilos = correoConEstilos;
        this.citaRepository = citaRepository;
    }

    @Scheduled(cron = "0 0,30 * * * *") // Run every hour at 0 minutes and 30 minutes
    public void runTask() {
        ZoneId limaZone = ZoneId.of("America/Lima");
        LocalDate currentDate = LocalDate.now(limaZone);
        LocalTime currentTime = LocalTime.now(limaZone);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HH:mm");

        String parsedTime = currentTime.format(formatter2);
        String parsedDate = currentDate.format(formatter);
        System.out.println(parsedDate);
        System.out.println(parsedTime);
        List<Cita> dataList = citaRepository.citasAutomatico(parsedDate, parsedTime);

        for (Cita arch : dataList) {
            LocalTime horaGa = arch.getHora();
            Duration duration = Duration.between(currentTime, horaGa);
            boolean isWithinOneHour = duration.abs().toMinutes() <= 60;
            if(isWithinOneHour){
                citaRepository.cancelarCita(arch.getId());
                if(arch.getEspecialidadesIdEspecialidad()!=null){
                    //correoConEstilos.sendEmailEstilos( usuarioSession.getEmail()   , "Cita cancelada" , "Su consulta médica agendada para la fecha " + citaA.getFecha() + " en la especialidad " + citaA.getEspecialidadesIdEspecialidad().getNombreEspecialidad() + " fue cancelada.");
                    try {
                        correoConEstilos.sendEmailEstilos2(arch.getPaciente().getEmail(),"Cita cancelada",
                                "<table width=\"650\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:1px solid #d8d8d8;border-collapse:collapse\">\n" +
                                        "\t\t<tbody><tr>\n" +
                                        "          <td valign=\"top\" bgcolor=\"#FFFFFF\" style=\"background:#fff;line-height:0\">\n" +
                                        "\t\t\t\t<img alt=\"x\" src=\"https://res.cloudinary.com/dtnko1xwm/image/upload/v1689559827/medical-logo-cloud_ketqww.jpg\" width=\"650\" class=\"CToWUd\" data-bit=\"iit\">\n" +
                                        "\t\t\t</td>\n" +
                                        "\t\t</tr>\n" +
                                        "\t\t<tr>\n" +
                                        "\t\t\t<td valign=\"top\" bgcolor=\"#FFFFFF\" style=\"background:#fff;line-height:0\">\n" +
                                        "\t\t\t</td>\n" +
                                        "\t\t</tr>\n" +
                                        "      \t\n" +
                                        "\t\t<tr>\n" +
                                        "\t\t\t<td valign=\"top\" bgcolor=\"#fff\" style=\"background:#fff;padding-right:30px;padding-left:30px;padding-bottom:25px;font-size:15px;font-family:Arial;color:#000;line-height:22px\">\n" +
                                        "\t\t\t\t<p><span style=\"font-weight:400\"><strong>Cita Cancelada</strong></span></p>\n" +
                                        "<p>Debido al plazo de espera de pago máximo de una hora previo al inicio de la sesion vencido</p>\n" +
                                        "<p><strong>Su cita del dia de hoy ha sido cancelada</strong>, Recuerde tomar en consideracion el horario pactado para estas sesiones para asi evitar inconvenientes. </p>\n" +

                                        "Su consulta médica agendada para la fecha " + arch.getFecha() + " en la especialidad "
                                        + arch.getEspecialidadesIdEspecialidad().getNombreEspecialidad() + " fue cancelada."+


                                        "<table border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style='background-image: linear-gradient(-45deg, #014ba7, #0183d0)'>\n" +
                                        "\t\t\t\t\t\t\t<tbody><tr>\n" +
                                        "\t\t\t\t\t\t\t\t<td colspan=\"3\" style=\"font-size:0;height:15px;line-height:1\">&nbsp;</td>\n" +
                                        "\t\t\t\t\t\t\t</tr>\n" +
                                        "\t\t\t\t\t\t\t<tr>\n" +
                                        "\t\t\t\t\t\t\t\t<td style=\"font-size:0;line-height:1\" width=\"50\">&nbsp;</td>\n" +
                                        "\t\t\t\t\t\t\t\t<td align=\"center\" style=\"text-align:center;font-family:Arial,sans-serif;line-height:1.1\">\n" +

                                        "\t\t\t\t\t\t\t\t</td>\n" +
                                        "\t\t\t\t\t\t\t\t<td style=\"font-size:0;line-height:1\" width=\"50\">&nbsp;</td>\n" +
                                        "\t\t\t\t\t\t\t</tr>\n" +
                                        "\t\t\t\t\t\t\t<tr>\n" +
                                        "\t\t\t\t\t\t\t\t<td colspan=\"3\" style=\"font-size:0;height:15px;line-height:1\">&nbsp;</td>\n" +
                                        "\t\t\t\t\t\t\t</tr>\n" +
                                        "\t\t\t\t\t\t</tbody></table>\n" +
                                        "\t\t\t\t\t\t<table border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                                        "\t\t\t\t\t\t\t<tbody><tr>\n" +
                                        "\t\t\t\t\t\t\t\t<td style=\"font-size:0;height:20px;line-height:1\">&nbsp;</td>\n" +
                                        "\t\t\t\t\t\t\t</tr>\n" +
                                        "\t\t\t\t\t\t</tbody></table>\n" +
                                        "\t\t\t\t\t\t<p><span style=\"font-weight:400\">Atentamente</span></p>\n" +
                                        "<p><strong>Clínica Medical-Tec</strong></p>\n" +
                                        "\n" +
                                        "\t        </td>\n" +
                                        "\t\t</tr>\n" +
                                        "\t\t<tr>\n" +
                                        "\t\t\t<td bgcolor=\"\" style='background-image: linear-gradient(-45deg, #014ba7, #0183d0)' styleheight=\"25\"> </td>\n" +
                                        "\t\t</tr>\n" +
                                        "\t</tbody></table>");
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }else if(arch.getExamenMedico()!=null){
                    //correoConEstilos.sendEmailEstilos( usuarioSession.getEmail()   , "Cita cancelada" , "Su examen médico agendado para la fecha " + citaA.getFecha() + " en la especialidad " + citaA.getExamenMedico().getNombre() + " fue cancelado.");
                    try {
                        correoConEstilos.sendEmailEstilos(arch.getPaciente().getEmail(),"Cita cancelada",
                                "<table width=\"650\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:1px solid #d8d8d8;border-collapse:collapse\">\n" +
                                        "\t\t<tbody><tr>\n" +
                                        "          <td valign=\"top\" bgcolor=\"#FFFFFF\" style=\"background:#fff;line-height:0\">\n" +
                                        "\t\t\t\t<img alt=\"x\" src=\"https://res.cloudinary.com/dtnko1xwm/image/upload/v1689559827/medical-logo-cloud_ketqww.jpg\" width=\"650\" class=\"CToWUd\" data-bit=\"iit\">\n" +
                                        "\t\t\t</td>\n" +
                                        "\t\t</tr>\n" +
                                        "\t\t<tr>\n" +
                                        "\t\t\t<td valign=\"top\" bgcolor=\"#FFFFFF\" style=\"background:#fff;line-height:0\">\n" +
                                        "\t\t\t</td>\n" +
                                        "\t\t</tr>\n" +
                                        "      \t\n" +
                                        "\t\t<tr>\n" +
                                        "\t\t\t<td valign=\"top\" bgcolor=\"#fff\" style=\"background:#fff;padding-right:30px;padding-left:30px;padding-bottom:25px;font-size:15px;font-family:Arial;color:#000;line-height:22px\">\n" +
                                        "\t\t\t\t<p><span style=\"font-weight:400\"><strong>Cita Cancelada</strong></span></p>\n" +
                                        "<p>Debido al plazo de espera de pago máximo de una hora previo al inicio de la sesion vencido</p>\n" +
                                        "<p><strong>Su cita del dia de hoy ha sido cancelada</strong>, Recuerde tomar en consideracion el horario pactado para estas sesiones para asi evitar inconvenientes. </p>\n" +

                                        "Su examen médico agendado para la fecha  "+arch.getFecha() +
                                        " en la especialidad "+ arch.getExamenMedico().getNombre() + ", fue cancelado \n" +


                                        "<table border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style='background-image: linear-gradient(-45deg, #014ba7, #0183d0)'>\n" +
                                        "\t\t\t\t\t\t\t<tbody><tr>\n" +
                                        "\t\t\t\t\t\t\t\t<td colspan=\"3\" style=\"font-size:0;height:15px;line-height:1\">&nbsp;</td>\n" +
                                        "\t\t\t\t\t\t\t</tr>\n" +
                                        "\t\t\t\t\t\t\t<tr>\n" +
                                        "\t\t\t\t\t\t\t\t<td style=\"font-size:0;line-height:1\" width=\"50\">&nbsp;</td>\n" +
                                        "\t\t\t\t\t\t\t\t<td align=\"center\" style=\"text-align:center;font-family:Arial,sans-serif;line-height:1.1\">\n" +

                                        "\t\t\t\t\t\t\t\t</td>\n" +
                                        "\t\t\t\t\t\t\t\t<td style=\"font-size:0;line-height:1\" width=\"50\">&nbsp;</td>\n" +
                                        "\t\t\t\t\t\t\t</tr>\n" +
                                        "\t\t\t\t\t\t\t<tr>\n" +
                                        "\t\t\t\t\t\t\t\t<td colspan=\"3\" style=\"font-size:0;height:15px;line-height:1\">&nbsp;</td>\n" +
                                        "\t\t\t\t\t\t\t</tr>\n" +
                                        "\t\t\t\t\t\t</tbody></table>\n" +
                                        "\t\t\t\t\t\t<table border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">\n" +
                                        "\t\t\t\t\t\t\t<tbody><tr>\n" +
                                        "\t\t\t\t\t\t\t\t<td style=\"font-size:0;height:20px;line-height:1\">&nbsp;</td>\n" +
                                        "\t\t\t\t\t\t\t</tr>\n" +
                                        "\t\t\t\t\t\t</tbody></table>\n" +
                                        "\t\t\t\t\t\t<p><span style=\"font-weight:400\">Atentamente</span></p>\n" +
                                        "<p><strong>Clínica Medical-Tec</strong></p>\n" +
                                        "\n" +
                                        "\t        </td>\n" +
                                        "\t\t</tr>\n" +
                                        "\t\t<tr>\n" +
                                        "\t\t\t<td bgcolor=\"\" style='background-image: linear-gradient(-45deg, #014ba7, #0183d0)' styleheight=\"25\"> </td>\n" +
                                        "\t\t</tr>\n" +
                                        "\t</tbody></table>");
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
