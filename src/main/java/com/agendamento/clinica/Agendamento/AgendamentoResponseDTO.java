package com.agendamento.clinica.Agendamento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AgendamentoResponseDTO(
        Long id,
        String pacienteNome,
        String nutricionistaNome,
        String servicoNome,
        LocalDate data,
        LocalTime horaInicio,
        LocalTime horaFim,
        StatusAgendamento status,
        LocalDateTime criadoEm
) {
    public static AgendamentoResponseDTO de(Agendamento agendamento) {
        return new AgendamentoResponseDTO(
                agendamento.getId(),
                agendamento.getPaciente().getNome(),
                agendamento.getNutricionista().getNome(),
                agendamento.getServico().getNome(),
                agendamento.getData(),
                agendamento.getHoraInicio(),
                agendamento.getHoraFim(),
                agendamento.getStatus(),
                agendamento.getCriadoEm()
        );
    }
}