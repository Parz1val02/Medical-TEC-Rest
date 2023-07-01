package com.example.medicaltec.function;

public class Fechas {

    public String traducirDia(String diaDeLaSemana) {
        return switch (diaDeLaSemana) {
            case "MONDAY" -> "Lunes";
            case "TUESDAY" -> "Martes";
            case "WEDNESDAY" -> "Miercoles";
            case "THURSDAY" -> "Jueves";
            case "FRIDAY" -> "Viernes";
            case "SATURDAY" -> "Sabado";
            case "SUNDAY" -> "Domingo";
            default -> "";
        };
    }
    public String traducirMes(String mes) {
        return switch (mes) {
            case "JANUARY" -> "Enero";
            case "FEBRUARY" -> "Febrero";
            case "MARCH" -> "Marzo";
            case "APRIL" -> "Abril";
            case "MAY" -> "Mayo";
            case "JUNE" -> "Junio";
            case "JULY" -> "Julio";
            case "AUGUST" -> "Agosto";
            case "SEPTEMBER" -> "Setiembre";
            case "OCTOBER" -> "Octubre";
            case "NOVEMBER" -> "Noviembre";
            case "DECEMBER" -> "Diciembre";
            default -> "";
        };
    }
    public int traducirMesNumero(String mes) {
        return switch (mes) {
            case "JANUARY" -> 1;
            case "FEBRUARY" -> 2;
            case "MARCH" -> 3;
            case "APRIL" -> 4;
            case "MAY" -> 5;
            case "JUNE" -> 6;
            case "JULY" -> 7;
            case "AUGUST" -> 8;
            case "SEPTEMBER" -> 9;
            case "OCTOBER" -> 10;
            case "NOVEMBER" -> 11;
            case "DECEMBER" -> 12;
            default -> 13;
        };
    }
}
