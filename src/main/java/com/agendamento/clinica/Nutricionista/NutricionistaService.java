package com.agendamento.clinica.Nutricionista;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NutricionistaService {

    private final NutricionistaRepository repository;

    public NutricionistaService(NutricionistaRepository repository) {
        this.repository = repository;
    }

    public NutricionistaResponseDTO cadastrar(NutricionistaRequestDTO dto) {
        if (repository.existsByCrn(dto.crn())) {
            throw new IllegalArgumentException("CRN já cadastrado");
        }
        if (repository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Nutricionista nutricionista = new Nutricionista();
        nutricionista.setNome(dto.nome());
        nutricionista.setEmail(dto.email());
        nutricionista.setTelefone(dto.telefone());
        nutricionista.setCrn(dto.crn());
        nutricionista.setEspecialidade(dto.especialidade());

        return NutricionistaResponseDTO.de(repository.save(nutricionista));
    }

    public List<NutricionistaResponseDTO> listar() {
        return repository.findAll()
                .stream()
                .map(NutricionistaResponseDTO::de)
                .toList();
    }

    public NutricionistaResponseDTO buscarPorId(Long id) {
        Nutricionista nutricionista = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nutricionista não encontrado"));
        return NutricionistaResponseDTO.de(nutricionista);
    }
}