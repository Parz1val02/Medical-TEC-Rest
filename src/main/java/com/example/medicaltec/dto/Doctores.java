package com.example.medicaltec.dto;

import java.util.ArrayList;

public class Doctores {
    private ArrayList<DoctorNoDto> listaDocs = new ArrayList<>();

    public ArrayList<DoctorNoDto> getListaDocs() {
        return listaDocs;
    }

    public void setListaDocs(ArrayList<DoctorNoDto> listaDocs) {
        this.listaDocs = listaDocs;
    }
}
