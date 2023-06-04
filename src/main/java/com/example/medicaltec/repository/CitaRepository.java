package com.example.medicaltec.repository;

import com.example.medicaltec.Entity.Cita;
import com.example.medicaltec.dto.Citadto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Integer> {

   @Query(nativeQuery = true, value = "SELECT * FROM telesystem_2.cita where citacancelada=0 and paciente_dni=?1")
   List<Cita> historialCitas(String dniPaciente);

   @Query(nativeQuery = true, value = "SELECT * FROM telesystem_2.cita where fecha < current_date() and citacancelada=0 and paciente_dni=?1")
   List<Cita> historialCitas2(String dniPaciente);

   @Query(nativeQuery = true, value = "SELECT C.idcita as `ID`, S.nombre as `Sede`, E.nombre_especialidad as `Title`, formapago as `FormaPago`, modalidad as `Modalidad`, T.tipo_cita as `TipoCita`,fecha as `Start`, hora as `Hora`, concat(D.nombre,\" \",D.apellido) as `Doctor` FROM cita C\n" +
           "           inner join sedes S on C.sedes_idsedes=S.idsedes\n" +
           "           inner join especialidades E on C.especialidades_id_especialidad=E.id_especialidad\n" +
           "           inner join tipocita T on C.tipocita_idtipocita=T.idtipocita\n" +
           "           inner join usuario D on C.doctor_dni1=D.dni\n" +
           "           where fecha >= current_date() and citacancelada=0 and paciente_dni=?1")
   List<Citadto> historialCitasAgendadas(String dniPaciente);

    @Query(value = "SELECT * FROM telesystem_2.cita WHERE doctor_dni1=\"12345678\" " +
                    "AND estadoscita_idestados=3 ORDER BY fecha DESC, hora DESC;",
                    nativeQuery = true)
    List<Cita> pacientesAtendidos();

    @Query(value = "SELECT * FROM telesystem_2.cita WHERE doctor_dni1=\"12345678\" " +
                    "AND estadoscita_idestados=1 ORDER BY fecha DESC, hora DESC;",
                    nativeQuery = true)
    List<Cita> proximasCitasAgendadas();

    @Query(value = "SELECT * FROM telesystem_2.cita WHERE paciente_dni=?1 AND fecha<now() ORDER BY fecha DESC, hora DESC;",
                    nativeQuery = true)
    List<Cita> citasPorUsuario(String id_paciente);

}