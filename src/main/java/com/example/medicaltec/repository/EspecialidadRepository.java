package com.example.medicaltec.repository;

import com.example.medicaltec.Entity.Especialidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EspecialidadRepository extends JpaRepository<Especialidade,Integer> {

    @Query(nativeQuery = true, value = "SELECT id_especialidad FROM especialidades where id_especialidad=?1")
    String verificarEspecialidad(String id);
}
