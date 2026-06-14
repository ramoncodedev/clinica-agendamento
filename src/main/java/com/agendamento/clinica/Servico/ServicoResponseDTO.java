package com.agendamento.clinica.Servico;

public record ServicoResponseDTO(
        Long id,
        String nome,
        String descricao,
        Integer duracaoMinutos
) {
    public static ServicoResponseDTO de(Servico servico) {
        return new ServicoResponseDTO(
                servico.getId(),
                servico.getNome(),
                servico.getDescricao(),
                servico.getDuracaoMinutos()
        );
    }
}