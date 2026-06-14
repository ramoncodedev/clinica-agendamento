package com.agendamento.clinica.Servico;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository repository;

    public ServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    public ServicoResponseDTO cadastrar(ServicoRequestDTO dto) {
        if (repository.existsByNome(dto.nome())) {
            throw new IllegalArgumentException("Serviço com este nome já cadastrado");
        }

        Servico servico = new Servico();
        servico.setNome(dto.nome());
        servico.setDescricao(dto.descricao());
        servico.setDuracaoMinutos(dto.duracaoMinutos());

        return ServicoResponseDTO.de(repository.save(servico));
    }

    public List<ServicoResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(ServicoResponseDTO::de)
                .toList();
    }

    public ServicoResponseDTO buscarPorId(Long id) {
        Servico servico = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        return ServicoResponseDTO.de(servico);
    }
}