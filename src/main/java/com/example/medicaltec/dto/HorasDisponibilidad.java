package com.example.medicaltec.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.List;

public class HorasDisponibilidad {
    @DateTimeFormat(pattern = "HH:mm")
    private List<LocalTime> horas;

    public List<LocalTime> getHoras() {
        return horas;
    }

    public void setHoras(List<LocalTime> horas) {
        this.horas = horas;
    }
}
