package com.example.medicaltec.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.List;

public class SuperDoctor {
    private DoctorDto doctorDto;
    @DateTimeFormat(pattern = "HH:mm")
    private List<LocalTime> horas;

    public DoctorDto getDoctorDto() {
        return doctorDto;
    }

    public void setDoctorDto(DoctorDto doctorDto) {
        this.doctorDto = doctorDto;
    }

    public List<LocalTime> getHoras() {
        return horas;
    }

    public void setHoras(List<LocalTime> horas) {
        this.horas = horas;
    }
}
