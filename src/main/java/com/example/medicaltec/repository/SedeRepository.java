package com.example.medicaltec.repository;

import com.example.medicaltec.Entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
public interface SedeRepository extends JpaRepository<Sede,Integer> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value ="update usuario set  sedes_idsedes=?1 where dni=?2" )
    void cambiarSede(Integer idSede, String dni);
    @Query(nativeQuery = true, value = "SELECT idsedes FROM sedes where idsedes=?1")
    String verificaridSede(String id);
}
