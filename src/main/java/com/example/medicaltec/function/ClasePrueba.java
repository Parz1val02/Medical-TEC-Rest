package com.example.medicaltec.function;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
public class ClasePrueba {



    public static void main(String[] args) throws UnsupportedEncodingException {
        //String baseURL = "https://8x8.vc/vpaas-magic-cookie-64547877cba34cdb892bd4fb58d11524";
        //String meetingName = "salapersonal";
        //LocalTime startTime = LocalTime.of(2023, 7, 4, 23, 40); // Fecha y hora de inicio de la reunión
        int durationMinutes = 30; // Duración de la reunión en minutos

        //String jitsiMeetURL = generateJitsiMeetURL(baseURL, meetingName, startTime, durationMinutes);
        //System.out.println("Jitsi Meet URL: " + jitsiMeetURL);
    }

    public String generateJitsiMeetURL(String baseURL, String meetingName, LocalTime startTime, int durationMinutes, LocalDate fecha) throws UnsupportedEncodingException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        String encodedMeetingName = URLEncoder.encode(meetingName, StandardCharsets.UTF_8);
        LocalDateTime dateTime = fecha.atTime(startTime);
        String encodedStartTime = URLEncoder.encode(dateTime.format(formatter), StandardCharsets.UTF_8);

        LocalTime endTime = startTime.plusMinutes(durationMinutes);
        LocalDateTime dateTimeEnd = fecha.atTime(endTime);
        String encodedEndTime = URLEncoder.encode(dateTimeEnd.format(formatter), StandardCharsets.UTF_8);

        String jitsiMeetURL = baseURL + "/" + encodedMeetingName + "?config.startWithVideoMuted=true&config.startTime=" + encodedStartTime + "&config.endTime=" + encodedEndTime;

        return jitsiMeetURL;
    }


}
