package com.agendamento.clinica;


import com.agendamento.clinica.Paciente.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PacienteServiceTest {

    @Mock
    private  PacienteRepository repository;
    @InjectMocks
    private  PacienteService service;

    @Test
    void deveLancarExcecaoQuandoCpfJaCadastrado(){


        PacienteRequestDTO dto = new PacienteRequestDTO(
                "Maria Silva", "12345678901", "11999999999", "maria@gmail.com"
        );

        when(repository.existsByCpf("12345678901")).thenReturn(true);


        assertThrows(IllegalArgumentException.class, ()-> service.cadastrar(dto));

    }

    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado(){
        PacienteRequestDTO dto = new PacienteRequestDTO(
                "Ramon silva", "09243919520", "73981558854", "ramonwebdesigner2025@gmail.com"
        );

        when(repository.existsByCpf("09243919520")).thenReturn(false); //cpf livre
        when(repository.existsByEmail("ramonwebdesigner2025@gmail.com")).thenReturn(true); //emailocupado

        assertThrows(IllegalArgumentException.class, () -> service.cadastrar(dto));
    }

    @Test
    void deverRetornarPacienteCadastradoComSucesso(){
        PacienteRequestDTO dto = new PacienteRequestDTO(
                "Ramon santos", "09246719420", "1234098754", "ramon@gmail.com"
        );

        Paciente pacienteSalvo = new Paciente();
        pacienteSalvo.setId(1L);
        pacienteSalvo.setNome("ramon santos");
        pacienteSalvo.setCpf("09246719420");
        pacienteSalvo.setTelefone("1234098754");
        pacienteSalvo.setEmail("ramon@gmail.com");

        when(repository.existsByCpf("09246719420")).thenReturn(false);
        when(repository.existsByEmail("ramon@gmail.com")).thenReturn(false);
        when(repository.save(any(Paciente.class))).thenReturn(pacienteSalvo);

        //act chamando o metodo que esta sendo testado
        PacienteResponseDTO resultado = service.cadastrar(dto);

        assertEquals("ramon santos", resultado.nome());
        assertEquals("09246719420", resultado.cpf());
        assertEquals("1234098754", resultado.telefone());
        assertEquals("ramon@gmail.com", resultado.email());

    }

}
