package com.example.medicaltec.function;


import com.example.medicaltec.Entity.Cita;
import com.example.medicaltec.Entity.Consultorio;
import com.example.medicaltec.Entity.Usuario;
import com.example.medicaltec.dto.ConsultorioxDoctorDto;
import com.example.medicaltec.repository.ConsultorioRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class CorreoConEstilos {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ConsultorioRepository consultorioRepository;

    public void sendEmailEstilos(String toEmail,String subject,String contenido) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("telesystemclinic@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        //helper.setText(body, true);

        //mailSender.send(message);

        String prueba = "<table width=\"650\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:1px solid #d8d8d8;border-collapse:collapse\">\n" +
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
                "\t\t\t\t<p><span style=\"font-weight:400\"><strong>Medical-Tec</strong></span></p>\n" +
                "<p>Nueva cita agendada</p>\n" +
                "<p><strong></strong>" + contenido + "</p>\n" +

                "<p>Con Medical-Tec, tendrás acceso a una amplia gama de servicios médicos y especialidades. Podrás agendar citas con diferentes médicos y especialistas, así como administrar tu historial médico de manera cómoda y segura.</p>\n" +

                "<table border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style='background-image: linear-gradient(-45deg, #014ba7, #0183d0)'>\n" +
                "\t\t\t\t\t\t\t<tbody><tr>\n" +
                "\t\t\t\t\t\t\t\t<td colspan=\"3\" style=\"font-size:0;height:15px;line-height:1\">&nbsp;</td>\n" +
                "\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t<td style=\"font-size:0;line-height:1\" width=\"50\">&nbsp;</td>\n" +
                "\t\t\t\t\t\t\t\t<td align=\"center\" style=\"text-align:center;font-family:Arial,sans-serif;line-height:1.1\">\n" +
                "\t\t\t\t\t\t\t\t\t<a href=\"\" style=\"color:#fff;text-decoration:none;font-weight:bold;display:inline-block;line-height:inherit\" target=\"_blank\" data-saferedirecturl=\"\">Ir a la pagina de inicio de sesión</a>\n" +
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
                "\t\t\t\t\t\t<p><span style=\"font-weight:400\">Muchas gracias por tu preferencia.</span></p>\n" +
                "<p><strong>Clínica Medical-Tec</strong></p>\n" +
                "\n" +
                "\t        </td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td bgcolor=\"\" style='background-image: linear-gradient(-45deg, #014ba7, #0183d0)' styleheight=\"25\"> </td>\n" +
                "\t\t</tr>\n" +
                "\t</tbody></table>";


        helper.setText(prueba, true);
        mailSender.send(message);

    }

    public void sendEmailEstilos2(String toEmail,String subject,String body) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("telesystemclinic@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);

    }
    public void sendEmailNotification(String subject, Usuario usuario, Cita cita ) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("telesystemclinic@gmail.com");
        helper.setTo(usuario.getEmail());
        helper.setSubject(subject);

        ConsultorioxDoctorDto cons = consultorioRepository.infoConsultorio(cita.getId(), cita.getDoctor().getId());

        String prueba = "<table width=\"650\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:1px solid #d8d8d8;border-collapse:collapse\">\n" +
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
                "\t\t\t\t<p><span style=\"font-weight:400\"><strong>Medical-Tec</strong></span></p>\n" +
                "<p>Nueva cita agendada</p>\n" +
                "<p><strong>Dirección: </strong>" + cons.getDireccion() + "</p>\n" +
                "<p><strong>Torre: </strong>" + cons.getTorres() + "</p>\n" +
                "<p><strong>Piso: </strong>" + cons.getPisos() + "</p>\n" +
                "<p><strong>Consultorio: </strong>" + cons.getNombreConsultorio() + "</p>\n" +
                "<p><strong>Fecha: </strong>" + cita.getFecha() + "</p>\n" +
                "<p><strong>Hora: </strong>" + cita.getHora() + "</p>\n" +
                "<p>Con Medical-Tec, tendrás acceso a una amplia gama de servicios médicos y especialidades. Podrás agendar citas con diferentes médicos y especialistas, así como administrar tu historial médico de manera cómoda y segura.</p>\n" +

                "<table border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style='background-image: linear-gradient(-45deg, #014ba7, #0183d0)'>\n" +
                "\t\t\t\t\t\t\t<tbody><tr>\n" +
                "\t\t\t\t\t\t\t\t<td colspan=\"3\" style=\"font-size:0;height:15px;line-height:1\">&nbsp;</td>\n" +
                "\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t<td style=\"font-size:0;line-height:1\" width=\"50\">&nbsp;</td>\n" +
                "\t\t\t\t\t\t\t\t<td align=\"center\" style=\"text-align:center;font-family:Arial,sans-serif;line-height:1.1\">\n" +
                "\t\t\t\t\t\t\t\t\t<a href=\"\" style=\"color:#fff;text-decoration:none;font-weight:bold;display:inline-block;line-height:inherit\" target=\"_blank\" data-saferedirecturl=\"\">Ir a la pagina de inicio de sesión</a>\n" +
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
                "\t\t\t\t\t\t<p><span style=\"font-weight:400\">Muchas gracias por tu preferencia.</span></p>\n" +
                "<p><strong>Clínica Medical-Tec</strong></p>\n" +
                "\n" +
                "\t        </td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td bgcolor=\"\" style='background-image: linear-gradient(-45deg, #014ba7, #0183d0)' styleheight=\"25\"> </td>\n" +
                "\t\t</tr>\n" +
                "\t</tbody></table>";


        helper.setText(prueba, true);

        mailSender.send(message);

    }




    //para enviar notificaciones de cambio de estado
    public void sendEmailNotificationCambioEstado(String toEmail, String subject, Cita cita) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("telesystemclinic@gmail.com");
        helper.setTo(toEmail);
        helper.setSubject(subject);


        String prueba = "<table width=\"650\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:1px solid #d8d8d8;border-collapse:collapse\">\n" +
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
                "\t\t\t\t<p><span style=\"font-weight:400\"><strong>Medical-Tec</strong></span></p>\n" +
                "<p>Estado de cita: </p>\n" + cita.getEstadoscitaIdestados().getTipo() +
                "<p><strong>Especialidad: </strong>" + cita.getEspecialidadesIdEspecialidad().getNombreEspecialidad()+ "</p>\n" +
                "<p><strong>Doctor: </strong>" + cita.getDoctor().getNombre() + " " + cita.getDoctor().getApellido()+ "</p>\n" +
                "<p><strong>Fecha: </strong>" + cita.getFecha()+ "</p>\n" +
                "<p>Con Medical-Tec, tendrás acceso a una amplia gama de servicios médicos y especialidades. Podrás agendar citas con diferentes médicos y especialistas, así como administrar tu historial médico de manera cómoda y segura.</p>\n" +

                "<table border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style='background-image: linear-gradient(-45deg, #014ba7, #0183d0)'>\n" +
                "\t\t\t\t\t\t\t<tbody><tr>\n" +
                "\t\t\t\t\t\t\t\t<td colspan=\"3\" style=\"font-size:0;height:15px;line-height:1\">&nbsp;</td>\n" +
                "\t\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t\t<td style=\"font-size:0;line-height:1\" width=\"50\">&nbsp;</td>\n" +
                "\t\t\t\t\t\t\t\t<td align=\"center\" style=\"text-align:center;font-family:Arial,sans-serif;line-height:1.1\">\n" +
                "\t\t\t\t\t\t\t\t\t<a href=\"\" style=\"color:#fff;text-decoration:none;font-weight:bold;display:inline-block;line-height:inherit\" target=\"_blank\" data-saferedirecturl=\"\">Ir a la pagina de inicio de sesión</a>\n" +
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
                "\t\t\t\t\t\t<p><span style=\"font-weight:400\">Muchas gracias por tu preferencia.</span></p>\n" +
                "<p><strong>Clínica Medical-Tec</strong></p>\n" +
                "\n" +
                "\t        </td>\n" +
                "\t\t</tr>\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td bgcolor=\"\" style='background-image: linear-gradient(-45deg, #014ba7, #0183d0)' styleheight=\"25\"> </td>\n" +
                "\t\t</tr>\n" +
                "\t</tbody></table>";


        helper.setText(prueba, true);

        mailSender.send(message);

    }


}
