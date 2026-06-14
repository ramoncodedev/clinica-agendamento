package com.agendamento.clinica.Nutricionista;

import java.time.LocalDateTime;

public record NutricionistaResponseDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        String crn,
        String especialidade,
        LocalDateTime criadoEm
) {
    public static NutricionistaResponseDTO de(Nutricionista nutricionista) {
        return new NutricionistaResponseDTO(
                nutricionista.getId(),
                nutricionista.getNome(),
                nutricionista.getEmail(),
                nutricionista.getTelefone(),
                nutricionista.getCrn(),
                nutricionista.getEspecialidade(),
                nutricionista.getCriadoEm()
        );
    }
}