package com.example.medicaltec.dto;


import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.ArrayList;

public interface HorasDto {
    String getDni();
    @DateTimeFormat(pattern = "HH:mm")
    ArrayList<LocalTime> getHoras();
}
