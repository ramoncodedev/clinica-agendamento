package com.agendamento.clinica.Servico;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService service;

    public ServicoController(ServicoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicoResponseDTO cadastrar(@RequestBody @Valid ServicoRequestDTO dto) {
        return service.cadastrar(dto);
    }

    @GetMapping
    public List<ServicoResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ServicoResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}