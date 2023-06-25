package com.example.medicaltec.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

public interface CitasSede {
    String getID();
    String getTitle();
    String getFormaPago();
    String getModalidad();
    String getStart();
    @DateTimeFormat(pattern = "HH:mm")
    LocalTime getHora();
    String getPaciente();
    String getDoctor();
    String getEspecialidad();
}
