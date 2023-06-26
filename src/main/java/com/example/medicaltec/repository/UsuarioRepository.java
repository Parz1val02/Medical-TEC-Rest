package com.example.medicaltec.repository;

import com.example.medicaltec.Entity.Usuario;

import com.example.medicaltec.dto.DoctorDto;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario,String> {
    @Query(nativeQuery = true,value = "select dni as `Dni`, email as `Email`, nombre as `Nombre`, apellido as `Apellido`, sexo as `Sexo`, e.nombre_especialidad as `Especialidad` , ceduladoctor as `Cedula`\n" +
            "            from usuario u \n" +
            "            inner join especialidades e on (u.especialidades_id_especialidad=e.id_especialidad)\n" +
            "            where u.roles_idroles = 1 and u.sedes_idsedes=?1 and u.enabled=1 and u.especialidades_id_especialidad=?2")
    List<DoctorDto> obtenerDoctoresSedeEspecialidad(Integer idSede, Integer idEspecialidad);

    @Query(nativeQuery = true,value = "select dni as `Dni`, email as `Email`, nombre as `Nombre`, apellido as `Apellido`, sexo as `Sexo`, e.nombre_especialidad as `Especialidad` , ceduladoctor as `Cedula`\n" +
            "            from usuario u \n" +
            "            inner join especialidades e on (u.especialidades_id_especialidad=e.id_especialidad)\n" +
            "            where u.roles_idroles = 1 and u.sedes_idsedes=?1 and u.enabled=1")
    List<DoctorDto> obtenerDoctoresSede(Integer idSede);

    @Query(nativeQuery = true, value = "SELECT dni FROM usuario where dni=?1")
    String validarUsuario(String dni);
}
