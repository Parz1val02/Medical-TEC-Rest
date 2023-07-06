package com.example.medicaltec.repository;

import com.example.medicaltec.Entity.ReunionVirtual;
import org.springframework.data.annotation.QueryAnnotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReunionVirtualRepository extends JpaRepository<ReunionVirtual, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM telesystem_2.reunion_virtual where cita_idcita=?1")
    ReunionVirtual ReuPorCita(Integer idCita);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO reunion_virtual (enlace, room, cita_idcita)\n" +
            "values(?1,null,?2)")
    void guardarReunion(String token, Integer idcita);


}
