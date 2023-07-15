package com.example.medicaltec.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

public interface CitaDoctor {
    String getID();
    String getSede();
    String getTitle();
    String getFormaPago();
    String getModalidad();
    String getStart();
    @DateTimeFormat(pattern = "HH:mm")
    LocalTime getHora();
    String getPaciente();
    String getEspecialidad();
    String getPago();
    String getEstado();
}
