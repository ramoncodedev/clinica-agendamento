package com.agendamento.clinica.Nutricionista;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nutricionistas")
public class NutricionistaController {

    private final NutricionistaService service;

    public NutricionistaController(NutricionistaService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NutricionistaResponseDTO cadastrar(@RequestBody @Valid NutricionistaRequestDTO dto) {
        return service.cadastrar(dto);
    }

    @GetMapping
    public List<NutricionistaResponseDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public NutricionistaResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}