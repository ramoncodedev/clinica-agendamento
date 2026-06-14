package com.agendamento.clinica;

import com.agendamento.clinica.Agendamento.Agendamento;
import com.agendamento.clinica.Agendamento.AgendamentoRepository;
import com.agendamento.clinica.Agendamento.AgendamentoRequestDTO;
import com.agendamento.clinica.Agendamento.AgendamentoService;
import com.agendamento.clinica.Nutricionista.NutricionistaRepository;
import com.agendamento.clinica.Paciente.PacienteRepository;
import com.agendamento.clinica.Servico.ServicoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AgendamentoServiceTest {

    @InjectMocks
    private AgendamentoService agendamentoService;

    @Mock
    private AgendamentoRepository agendamentoRepository;
    @Mock
    private PacienteRepository pacienteRepository;
    @Mock
    private NutricionistaRepository nutricionistaRepository;
    @Mock
    private ServicoRepository servicoRepository;

    @Test
    void deveLancarExcecaoQuandoPacienteNaoExistir() {
        // Arrange
        AgendamentoRequestDTO dto = new AgendamentoRequestDTO(
                1L, 1L, 1L, LocalDate.of(2026, 6, 16), LocalTime.of(8, 0)
        );

        when(pacienteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            agendamentoService.agendar(dto);
        });

        assertEquals("Paciente não encontrado", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNutricionistaNaoExistir(){

        AgendamentoRequestDTO dto = new AgendamentoRequestDTO(
                1L, 1L, 1L, LocalDate.of(2026, 6, 16), LocalTime.of(8, 0)
        );

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(new com.agendamento.clinica.Paciente.Paciente()));
        when(nutricionistaRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> agendamentoService.agendar(dto));

        assertEquals("Nutricionista não encontrado", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoServicoNaoExistir(){

        AgendamentoRequestDTO dto = new AgendamentoRequestDTO(
                1L, 1L, 1L, LocalDate.of(2026, 6, 16), LocalTime.of(8,0)
        );

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(new com.agendamento.clinica.Paciente.Paciente()));
        when(nutricionistaRepository.findById(1L)).thenReturn(Optional.of(new com.agendamento.clinica.Nutricionista.Nutricionista()));
        when(servicoRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> agendamentoService.agendar(dto));

        assertEquals("Serviço não encontrado", ex.getMessage());

    }

    @Test
    void deveLancarExcecaoQuandoHorarioConflitar(){

        //arrange
        AgendamentoRequestDTO dto = new AgendamentoRequestDTO(
                1L, 1L, 1L, LocalDate.of(2026, 6, 16), LocalTime.of(8,0)
        );

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(new com.agendamento.clinica.Paciente.Paciente()));
        when(nutricionistaRepository.findById(1L)).thenReturn(Optional.of(new com.agendamento.clinica.Nutricionista.Nutricionista()));
        com.agendamento.clinica.Servico.Servico servico = new com.agendamento.clinica.Servico.Servico();
        servico.setDuracaoMinutos(60);
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));
        when(agendamentoRepository.existsByNutricionistaIdAndDataAndHoraInicioLessThanAndHoraFimGreaterThan(
                1L, LocalDate.of(2026, 6, 16), LocalTime.of(9,0), LocalTime.of(8,0)
        )).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> agendamentoService.agendar(dto));

        assertEquals("Horário já ocupado para este nutricionista", ex.getMessage());

    }

    @Test
    void deverRetornaAgendamentoCadastradoComSucesso(){

        // Arrange
        AgendamentoRequestDTO dto = new AgendamentoRequestDTO(
                1L, 1L, 1L, LocalDate.of(2026, 6, 16), LocalTime.of(8, 0)
        );

        com.agendamento.clinica.Paciente.Paciente paciente = new com.agendamento.clinica.Paciente.Paciente();
        paciente.setNome("João Silva");

        com.agendamento.clinica.Nutricionista.Nutricionista nutricionista = new com.agendamento.clinica.Nutricionista.Nutricionista();
        nutricionista.setNome("Dra. Ana");

        com.agendamento.clinica.Servico.Servico servico = new com.agendamento.clinica.Servico.Servico();
        servico.setNome("Consulta Inicial");
        servico.setDuracaoMinutos(60);

        Agendamento agendamentoSalvo = new Agendamento();
        agendamentoSalvo.setId(1L);
        agendamentoSalvo.setPaciente(paciente);
        agendamentoSalvo.setNutricionista(nutricionista);
        agendamentoSalvo.setServico(servico);
        agendamentoSalvo.setData(LocalDate.of(2026, 6, 16));
        agendamentoSalvo.setHoraInicio(LocalTime.of(8, 0));
        agendamentoSalvo.setHoraFim(LocalTime.of(9, 0));
        agendamentoSalvo.setStatus(com.agendamento.clinica.Agendamento.StatusAgendamento.AGENDADO);

        when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
        when(nutricionistaRepository.findById(1L)).thenReturn(Optional.of(nutricionista));
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico));
        when(agendamentoRepository.existsByNutricionistaIdAndDataAndHoraInicioLessThanAndHoraFimGreaterThan(
                1L, LocalDate.of(2026, 6, 16), LocalTime.of(9, 0), LocalTime.of(8, 0)
        )).thenReturn(false);
        when(agendamentoRepository.save(org.mockito.ArgumentMatchers.any())).thenReturn(agendamentoSalvo);

        // Act
        com.agendamento.clinica.Agendamento.AgendamentoResponseDTO resultado = agendamentoService.agendar(dto);

        // Assert
        assertEquals(1L, resultado.id());
        assertEquals("João Silva", resultado.pacienteNome());
        assertEquals("Dra. Ana", resultado.nutricionistaNome());
        assertEquals("Consulta Inicial", resultado.servicoNome());
        assertEquals(LocalDate.of(2026, 6, 16), resultado.data());
        assertEquals(LocalTime.of(8, 0), resultado.horaInicio());
        assertEquals(LocalTime.of(9, 0), resultado.horaFim());
        assertEquals(com.agendamento.clinica.Agendamento.StatusAgendamento.AGENDADO, resultado.status());
    }
}