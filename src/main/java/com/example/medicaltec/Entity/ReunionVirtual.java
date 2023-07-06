package com.example.medicaltec.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;

@Getter
@Setter
@Entity
@Table(name = "reunion_virtual")
public class ReunionVirtual implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idreunion_virtual", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cita_idcita")
    private Cita cita;


    @Column(name = "enlace",  length = 500)
    private String enlace;


    @Column(name = "room",  length = 200)
    private String room;


}
