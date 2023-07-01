package com.example.medicaltec.dto;

import java.util.List;

public class HorariosMes {
    private String doctorDni;
    private List<HorariosDia> diasDelMes;

    public String getDoctorDni() {
        return doctorDni;
    }

    public void setDoctorDni(String doctorDni) {
        this.doctorDni = doctorDni;
    }

    public List<HorariosDia> getDiasDelMes() {
        return diasDelMes;
    }

    public void setDiasDelMes(List<HorariosDia> diasDelMes) {
        this.diasDelMes = diasDelMes;
    }
}
