package com.example.medicaltec.function;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ClasePrueba {



    public static void main(String[] args) throws UnsupportedEncodingException {
//        String baseURL = "https://meet.jit.si/";
//        String meetingName = "salapersonal";
//        LocalTime startTime = LocalTime.of(18,30); // Fecha y hora de inicio de la reunión
//        int durationMinutes = 30; // Duración de la reunión en minutos
//
//        LocalDate fecha = LocalDate.now();
//        String jitsiMeetURL = generateJitsiMeetURL(baseURL, meetingName, startTime, durationMinutes, fecha);
//        System.out.println("Jitsi Meet URL: " + jitsiMeetURL);
    }

    public String generateJitsiMeetURL(String baseURL, String meetingName, LocalTime startTime, int durationMinutes, LocalDate fecha) throws UnsupportedEncodingException {

        UUID uuid = UUID.randomUUID();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        String encodedMeetingName = URLEncoder.encode(meetingName, StandardCharsets.UTF_8);
        LocalDateTime dateTime = fecha.atTime(startTime);
        String encodedStartTime = URLEncoder.encode(dateTime.format(formatter), StandardCharsets.UTF_8);

        LocalTime endTime = startTime.plusMinutes(durationMinutes);
        LocalDateTime dateTimeEnd = fecha.atTime(endTime);
        String encodedEndTime = URLEncoder.encode(dateTimeEnd.format(formatter), StandardCharsets.UTF_8);

        String jitsiMeetURL = baseURL + uuid + encodedMeetingName + "?config.startWithVideoMuted=true&config.startTime=" + encodedStartTime + "&config.endTime=" + encodedEndTime;

        return jitsiMeetURL;
    }


}
