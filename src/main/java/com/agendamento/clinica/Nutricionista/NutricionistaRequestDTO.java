package com.agendamento.clinica.Nutricionista;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record NutricionistaRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Telefone é obrigatório")
        String telefone,

        @NotBlank(message = "CRN é obrigatório")
        String crn,

        @NotBlank(message = "Especialidade é obrigatória")
        String especialidade
) {}