package com.agendamento.clinica.Servico;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ServicoRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        String descricao,

        @NotNull(message = "Duração é obrigatória")
        @Min(value = 1, message = "Duração deve ser maior que zero")
        Integer duracaoMinutos
) {}