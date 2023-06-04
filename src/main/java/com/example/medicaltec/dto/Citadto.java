package com.example.medicaltec.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

public interface Citadto {
    String getID();
    String getSede();
    String getTipoCita();
    String getFormaPago();
    String getModalidad();
    String getStart();
    @DateTimeFormat(pattern = "HH:mm")
    LocalTime getHora();
    String getDoctor();
    String getTitle();
}
