package com.agendamento.clinica.Paciente;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }

    public PacienteResponseDTO cadastrar(PacienteRequestDTO dto) {
        if (repository.existsByCpf(dto.cpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        if (repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Paciente paciente = new Paciente();
        paciente.setNome(dto.nome());
        paciente.setCpf(dto.cpf());
        paciente.setTelefone(dto.telefone());
        paciente.setEmail(dto.email());

        return PacienteResponseDTO.de(repository.save(paciente));
    }

    public List<PacienteResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(PacienteResponseDTO::de)
                .toList();
    }

    public PacienteResponseDTO buscarPorId(Long id) {
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado"));
        return PacienteResponseDTO.de(paciente);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Paciente não encontrado");
        }
        repository.deleteById(id);
    }

}
