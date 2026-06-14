package com.agendamento.clinica.Agendamento;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByNutricionistaIdAndData(Long nutricionistaId, LocalDate data);

    boolean existsByNutricionistaIdAndDataAndHoraInicioLessThanAndHoraFimGreaterThan(
            Long nutricionistaId,
            LocalDate data,
            LocalTime horaFim,
            LocalTime horaInicio
    );



}
