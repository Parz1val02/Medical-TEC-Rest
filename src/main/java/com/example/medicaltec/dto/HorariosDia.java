package com.example.medicaltec.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class HorariosDia {
    @DateTimeFormat(pattern = "yyyy-DD-mm")
    private LocalDate dia;
    @DateTimeFormat(pattern = "HH:mm")
    private List<LocalTime> horas;

    public LocalDate getDia() {
        return dia;
    }

    public void setDia(LocalDate dia) {
        this.dia = dia;
    }

    public List<LocalTime> getHoras() {
        return horas;
    }

    public void setHoras(List<LocalTime> horas) {
        this.horas = horas;
    }
}
