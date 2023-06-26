package com.example.medicaltec.function;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimeListGenerationExample {
    public List<LocalTime> generateTimeList(LocalTime startHour, LocalTime endHour, LocalTime skipHour) {
        List<LocalTime> timeList = new ArrayList<>();

        LocalTime currentTime = startHour;

        // Generate the time list
        while (currentTime.isBefore(endHour)) {
            if (!currentTime.equals(skipHour) && !currentTime.equals(skipHour.plusMinutes(30))) {
                timeList.add(currentTime);
            }
            currentTime = currentTime.plusMinutes(30);
        }

        return timeList;
    }
}
