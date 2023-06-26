package com.example.medicaltec.repository;

import com.example.medicaltec.Entity.Cita;
import com.example.medicaltec.dto.CitaDoctor;
import com.example.medicaltec.dto.Citadto;
import com.example.medicaltec.dto.CitasSede;
import lombok.extern.log4j.Log4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Integer> {


   @Query(nativeQuery = true, value = "SELECT C.idcita as `ID`, S.nombre as `Sede`, E.nombre_especialidad as `Title`, formapago as `FormaPago`, modalidad as `Modalidad`, T.tipo_cita as `TipoCita`,fecha as `Start`, hora as `Hora`, concat(D.nombre,\" \",D.apellido) as `Doctor` FROM cita C\n" +
           "inner join sedes S on C.sedes_idsedes=S.idsedes\n" +
           "inner join especialidades E on C.especialidades_id_especialidad=E.id_especialidad\n" +
           "inner join tipocita T on C.tipocita_idtipocita=T.idtipocita\n" +
           "inner join usuario D on C.doctor_dni1=D.dni\n" +
           "where str_to_date(fecha, '%d-%m-%Y')>= current_date() and citacancelada=0 and estadoscita_idestados=1 and paciente_dni=?1")
   List<Citadto> historialCitasAgendadas(String dniPaciente);

   @Query(nativeQuery = true, value = "SELECT C.idcita as `ID`, S.nombre as `Sede`, E.nombre_especialidad as `Especialidad`, formapago as `FormaPago`, modalidad as `Modalidad`, T.tipo_cita as `Title`,fecha as `Start`, hora as `Hora`, concat(P.nombre,\" \",P.apellido) as `Paciente` FROM cita C\n" +
           "inner join sedes S on C.sedes_idsedes=S.idsedes\n" +
           "inner join especialidades E on C.especialidades_id_especialidad=E.id_especialidad\n" +
           "inner join tipocita T on C.tipocita_idtipocita=T.idtipocita\n" +
           "inner join usuario P on C.paciente_dni=P.dni\n" +
           "where str_to_date(fecha, '%d-%m-%Y')>= current_date() and citacancelada=0 and estadoscita_idestados=1 and doctor_dni1=?1")
   List<CitaDoctor> historialCitasDoctor(String dniDoctor);

   @Query(nativeQuery = true, value = "SELECT C.idcita as `ID`, E.nombre_especialidad as `Especialidad`, formapago as `FormaPago`, modalidad as `Modalidad`, T.tipo_cita as `Title`,fecha as `Start`, hora as `Hora`, concat(P.nombre,\" \",P.apellido) as `Paciente`, concat(D.nombre,\" \",D.apellido) as `Doctor` FROM cita C\n" +
           "inner join sedes S on C.sedes_idsedes=S.idsedes\n" +
           "inner join especialidades E on C.especialidades_id_especialidad=E.id_especialidad\n" +
           "inner join tipocita T on C.tipocita_idtipocita=T.idtipocita\n" +
           "inner join usuario P on C.paciente_dni=P.dni\n" +
           "inner join usuario D on C.doctor_dni1=D.dni\n" +
           "where str_to_date(fecha, '%d-%m-%Y')>= current_date() and citacancelada=0 and estadoscita_idestados=1 and C.sedes_idsedes=?1")
   List<CitasSede> historialCitasSede(String idSede);


   @Query(nativeQuery = true, value = "SELECT DATE_FORMAT(hora, '%H:%i') FROM cita where fecha=?1 and doctor_dni1=?2")
   List<String> horasCitasProgramdas(String fecha, String doctorDni);
}