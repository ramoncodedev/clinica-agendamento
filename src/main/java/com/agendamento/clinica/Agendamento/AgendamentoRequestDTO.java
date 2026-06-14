package com.agendamento.clinica.Agendamento;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoRequestDTO(

        @NotNull(message = "Paciente é obrigatório")
        Long pacienteId,

        @NotNull(message = "Nutricionista é obrigatório")
        Long nutricionistaId,

        @NotNull(message = "Serviço é obrigatório")
        Long servicoId,

        @NotNull(message = "Data é obrigatória")
        @Future(message = "A data deve ser no futuro")
        LocalDate data,

        @NotNull(message = "Hora de início é obrigatória")
        LocalTime horaInicio
) {}