package com.agendamento.clinica.Paciente;

import java.time.LocalDateTime;

public record PacienteResponseDTO(

        Long id,
        String nome,
        String cpf,
        String telefone,
        String email,
        LocalDateTime criadoEm


) {

    public static PacienteResponseDTO de(Paciente paciente) {
        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNome(),
                paciente.getCpf(),
                paciente.getTelefone(),
                paciente.getEmail(),
                paciente.getCriadoEm()
        );
    }

}
