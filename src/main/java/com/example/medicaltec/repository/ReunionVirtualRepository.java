package com.example.medicaltec.repository;

import com.example.medicaltec.Entity.ReunionVirtual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReunionVirtualRepository extends JpaRepository<ReunionVirtual, Integer> {

    @Query(nativeQuery = true, value = "SELECT * FROM telesystem_2.reunion_virtual where cita_idcita=?1")
    List<ReunionVirtual> listadoPorCita(Integer idCita);
}
