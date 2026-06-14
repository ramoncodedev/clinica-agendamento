package com.agendamento.clinica.Paciente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PacienteRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        @Size(min = 11, max = 14, message = "CPF inválido")
        String cpf,

        @NotBlank(message = "Telefone é obrigatório")
        String telefone,

        @NotBlank(message = "Email é obrigatório")
        String email


) {
}
