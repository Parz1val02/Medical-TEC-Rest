package com.example.medicaltec.repository;

import com.example.medicaltec.Entity.Cita;
import com.example.medicaltec.Entity.ReunionVirtual;
import com.example.medicaltec.dto.CitaDoctor;
import com.example.medicaltec.dto.Citadto;
import com.example.medicaltec.dto.CitasSede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Integer> {


   @Query(nativeQuery = true, value = "SELECT C.idcita as `Citaid`, S.nombre as `Sede`, E.nombre_especialidad as `Title`, formapago as `FormaPago`, modalidad as `Modalidad`, T.tipo_cita as `TipoCita`,fecha as `Start`, hora as `Hora`, concat(D.nombre,\" \",D.apellido) as `Doctor`, R.enlace as `Extra`, C.pagada as `Pagada`, (Z.porc_seguro*T.precio)/100 as `Precio`, S.direccion as `Direccion`, W.tipo as `Estado`\n" +
           "FROM cita C\n" +
           "inner join sedes S on C.sedes_idsedes=S.idsedes\n" +
           "inner join especialidades E on C.especialidades_id_especialidad=E.id_especialidad\n" +
           "inner join tipocita T on C.tipocita_idtipocita=T.idtipocita\n" +
           "inner join usuario D on C.doctor_dni1=D.dni\n" +
           "inner join reunion_virtual R on C.id_reunion=R.idreunion_virtual\n" +
           "inner join usuario P on C.paciente_dni=P.dni\n" +
           "inner join seguros Z on P.seguros_id_seguro=Z.id_seguro\n" +
           "inner join estadoscita W on C.estadoscita_idestados=W.idestados\n" +
           "where str_to_date(fecha, '%d-%m-%Y')>= current_date() and citacancelada=0 and estadoscita_idestados in (1,2,4,5) and paciente_dni=?1 and modalidad=\"Virtual\"")
   List<Citadto> historialCitasAgendadasVirtual(String dniPaciente);
   @Query(nativeQuery = true, value = "SELECT C.idcita as `Citaid`, S.nombre as `Sede`, E.nombre_especialidad as `Title`, formapago as `FormaPago`, modalidad as `Modalidad`, T.tipo_cita as `TipoCita`,fecha as `Start`, hora as `Hora`, concat(D.nombre,\" \",D.apellido) as `Doctor`, G.idconsultorio as `Extra`, C.pagada as `Pagada`,(Z.porc_seguro*T.precio)/100 as `Precio`, S.direccion as `Direccion`, W.tipo as `Estado`\n" +
           "FROM cita C\n" +
           "inner join sedes S on C.sedes_idsedes=S.idsedes\n" +
           "inner join especialidades E on C.especialidades_id_especialidad=E.id_especialidad\n" +
           "inner join tipocita T on C.tipocita_idtipocita=T.idtipocita\n" +
           "inner join usuario D on C.doctor_dni1=D.dni\n" +
           "inner join horasdoctor H on C.doctor_dni1=H.doctor_dni\n" +
           "inner join consultorio G on H.idhorasdoctor=G.horasdoctor_idhorasdoctor\n" +
           "inner join usuario P on C.paciente_dni=P.dni\n" +
           "inner join seguros Z on P.seguros_id_seguro=Z.id_seguro\n" +
           "inner join estadoscita W on C.estadoscita_idestados=W.idestados\n" +
           "where str_to_date(fecha, '%d-%m-%Y')>= current_date() and citacancelada=0 and estadoscita_idestados in (1,2,4,5) and paciente_dni=?1 and modalidad=\"Presencial\"")
   List<Citadto> historialCitasAgendadasPresencial(String dniPaciente);

   @Query(nativeQuery = true, value = "SELECT C.idcita as `Citaid`, S.nombre as `Sede`, E.nombre as `Title`, formapago as `FormaPago`, modalidad as `Modalidad`, T.tipo_cita as `TipoCita`,fecha as `Start`, hora as `Hora`, concat(D.nombre,\" \",D.apellido) as `Doctor`, G.idconsultorio as `Extra`, C.pagada as `Pagada`, (Z.porc_seguro*E.precio)/100 as `Precio`, S.direccion as `Direccion`, W.tipo as `Estado`\n" +
           "FROM cita C\n" +
           "inner join sedes S on C.sedes_idsedes=S.idsedes\n" +
           "inner join examen_medico E on C.examen_medico_idexamen=E.idexamen\n" +
           "inner join tipocita T on C.tipocita_idtipocita=T.idtipocita\n" +
           "inner join usuario D on C.doctor_dni1=D.dni\n" +
           "inner join horasdoctor H on C.doctor_dni1=H.doctor_dni\n" +
           "inner join consultorio G on H.idhorasdoctor=G.horasdoctor_idhorasdoctor\n" +
           "inner join usuario P on C.paciente_dni=P.dni\n" +
           "inner join seguros Z on P.seguros_id_seguro=Z.id_seguro\n" +
           "inner join estadoscita W on C.estadoscita_idestados=W.idestados\n" +
           "where str_to_date(fecha, '%d-%m-%Y')>= current_date() and citacancelada=0 and estadoscita_idestados in (1,2,4,5) and paciente_dni=?1")
   List<Citadto> historialExamenesAgendados(String dniPaciente);

   @Query(nativeQuery = true, value = "SELECT C.idcita as `ID`, S.nombre as `Sede`, E.nombre_especialidad as `Especialidad`, formapago as `FormaPago`, modalidad as `Modalidad`, T.tipo_cita as `Title`,fecha as `Start`, hora as `Hora`, concat(P.nombre,\" \",P.apellido) as `Paciente` FROM cita C\n" +
           "inner join sedes S on C.sedes_idsedes=S.idsedes\n" +
           "inner join especialidades E on C.especialidades_id_especialidad=E.id_especialidad\n" +
           "inner join tipocita T on C.tipocita_idtipocita=T.idtipocita\n" +
           "inner join usuario P on C.paciente_dni=P.dni\n" +
           "where str_to_date(fecha, '%d-%m-%Y')>= current_date() and citacancelada=0 and estadoscita_idestados=1 and doctor_dni1=?1")
   List<CitaDoctor> historialCitasDoctor(String dniDoctor);

   @Query(nativeQuery = true, value = "SELECT C.idcita as `ID`, S.nombre as `Sede`, E.nombre as `Especialidad`, formapago as `FormaPago`, modalidad as `Modalidad`, T.tipo_cita as `Title`,fecha as `Start`, hora as `Hora`, concat(P.nombre,\" \",P.apellido) as `Paciente` FROM cita C\n" +
           "inner join sedes S on C.sedes_idsedes=S.idsedes\n" +
           "inner join examen_medico E on C.examen_medico_idexamen=E.idexamen\n" +
           "inner join tipocita T on C.tipocita_idtipocita=T.idtipocita\n" +
           "inner join usuario P on C.paciente_dni=P.dni\n" +
           "where str_to_date(fecha, '%d-%m-%Y')>= current_date() and citacancelada=0 and estadoscita_idestados=1 and doctor_dni1=?1")
   List<CitaDoctor>historialExamenesDoctor(String dni);

   @Query(nativeQuery = true, value = "SELECT C.idcita as `ID`, E.nombre_especialidad as `Especialidad`, formapago as `FormaPago`, modalidad as `Modalidad`, T.tipo_cita as `Title`,fecha as `Start`, hora as `Hora`, concat(P.nombre,\" \",P.apellido) as `Paciente`, concat(D.nombre,\" \",D.apellido) as `Doctor` FROM cita C\n" +
           "inner join sedes S on C.sedes_idsedes=S.idsedes\n" +
           "inner join especialidades E on C.especialidades_id_especialidad=E.id_especialidad\n" +
           "inner join tipocita T on C.tipocita_idtipocita=T.idtipocita\n" +
           "inner join usuario P on C.paciente_dni=P.dni\n" +
           "inner join usuario D on C.doctor_dni1=D.dni\n" +
           "where str_to_date(fecha, '%d-%m-%Y')>= current_date() and citacancelada=0 and estadoscita_idestados=1 and C.sedes_idsedes=?1")
   List<CitasSede> historialCitasSede(String idSede);

   @Query(nativeQuery = true, value = "SELECT C.idcita as `ID`, E.nombre as `Especialidad`, formapago as `FormaPago`, modalidad as `Modalidad`, T.tipo_cita as `Title`,fecha as `Start`, hora as `Hora`, concat(P.nombre,\" \",P.apellido) as `Paciente`, concat(D.nombre,\" \",D.apellido) as `Doctor` FROM cita C\n" +
           "inner join sedes S on C.sedes_idsedes=S.idsedes\n" +
           "inner join examen_medico E on C.examen_medico_idexamen=E.idexamen\n" +
           "inner join tipocita T on C.tipocita_idtipocita=T.idtipocita\n" +
           "inner join usuario P on C.paciente_dni=P.dni\n" +
           "inner join usuario D on C.doctor_dni1=D.dni\n" +
           "where str_to_date(fecha, '%d-%m-%Y')>= current_date() and citacancelada=0 and estadoscita_idestados=1 and C.sedes_idsedes=?1")
   List<CitasSede> historialExamenesSede(String idSede);

   @Query(nativeQuery = true, value = "SELECT DATE_FORMAT(hora, '%H:%i') FROM cita  where fecha=?1 and doctor_dni1=?2 and citacancelada=0")
   List<String> horasCitasProgramdas(String fecha, String doctorDni);

   @Modifying
   @Transactional
   @Query(nativeQuery = true, value = "INSERT INTO cita (citacancelada, sedes_idsedes, especialidades_id_especialidad, estadoscita_idestados, receta_idreceta, formapago, modalidad, tipocita_idtipocita, fecha, hora, paciente_dni, doctor_dni1, pagada, examen_medico_idexamen)\n" +
           "values(0,?1,?2,1,null,?3,?4,?5,?6,?7,?8,?9,0,null)")
   void guardarConsultaMedicaPresencial(String idSede, String idEspecialidad, String formapago, String modalidad, String idTipoCita, String fecha, String hora, String dniPaciente, String dniDoctor);
   @Modifying
   @Transactional
   @Query(nativeQuery = true, value = "INSERT INTO cita (citacancelada, sedes_idsedes, especialidades_id_especialidad, estadoscita_idestados, receta_idreceta, formapago, modalidad, tipocita_idtipocita, fecha, hora, paciente_dni, doctor_dni1, pagada, examen_medico_idexamen, id_reunion)\n" +
           "values(0,?1,?2,1,null,?3,?4,?5,?6,?7,?8,?9,0,null,?10)")
   void guardarConsultaMedicaVirtual(String idSede, String idEspecialidad, String formapago, String modalidad, String idTipoCita, String fecha, String hora, String dniPaciente, String dniDoctor, Integer reunionVirtual);

   @Modifying
   @Transactional
   @Query(nativeQuery = true, value = "INSERT INTO cita (citacancelada, sedes_idsedes, especialidades_id_especialidad, estadoscita_idestados, receta_idreceta, formapago, modalidad, tipocita_idtipocita, fecha, hora, paciente_dni, doctor_dni1, pagada, examen_medico_idexamen)\n" +
           "values(0,?1,null,1,null,?2,?3,?4,?5,?6,?7,?8,0,?9)")
   void guardarExamenMedico(String idSede, String formapago, String modalidad, String idTipoCita, String fecha, String hora, String dniPaciente, String dniDoctor, String idExamenMedico);


}