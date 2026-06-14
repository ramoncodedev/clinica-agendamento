package com.agendamento.clinica.Agendamento;

import com.agendamento.clinica.Nutricionista.Nutricionista;
import com.agendamento.clinica.Nutricionista.NutricionistaRepository;
import com.agendamento.clinica.Paciente.Paciente;
import com.agendamento.clinica.Paciente.PacienteRepository;
import com.agendamento.clinica.Servico.Servico;
import com.agendamento.clinica.Servico.ServicoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final PacienteRepository pacienteRepository;
    private final NutricionistaRepository nutricionistaRepository;
    private final ServicoRepository servicoRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              PacienteRepository pacienteRepository,
                              NutricionistaRepository nutricionistaRepository,
                              ServicoRepository servicoRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.pacienteRepository = pacienteRepository;
        this.nutricionistaRepository = nutricionistaRepository;
        this.servicoRepository = servicoRepository;
    }

    public AgendamentoResponseDTO agendar(AgendamentoRequestDTO dto) {
        log.info("Iniciando agendamento: pacienteId={}, nutricionistaId={}, data={}, hora={}",
                dto.pacienteId(), dto.nutricionistaId(), dto.data(), dto.horaInicio());

        Paciente paciente = pacienteRepository.findById(dto.pacienteId())
                .orElseThrow(() -> {
                    log.warn("Paciente não encontrado: id={}", dto.pacienteId());
                    return new IllegalArgumentException("Paciente não encontrado");
                });

        Nutricionista nutricionista = nutricionistaRepository.findById(dto.nutricionistaId())
                .orElseThrow(() -> {
                    log.warn("Nutricionista não encontrado: id={}", dto.nutricionistaId());
                    return new IllegalArgumentException("Nutricionista não encontrado");
                });

        Servico servico = servicoRepository.findById(dto.servicoId())
                .orElseThrow(() -> {
                    log.warn("Serviço não encontrado: id={}", dto.servicoId());
                    return new IllegalArgumentException("Serviço não encontrado");
                });

        LocalTime horaFim = dto.horaInicio().plusMinutes(servico.getDuracaoMinutos());

        boolean conflito = agendamentoRepository
                .existsByNutricionistaIdAndDataAndHoraInicioLessThanAndHoraFimGreaterThan(
                        dto.nutricionistaId(), dto.data(), horaFim, dto.horaInicio()
                );

        if (conflito) {
            log.warn("Conflito de horário: nutricionistaId={}, data={}, hora={}",
                    dto.nutricionistaId(), dto.data(), dto.horaInicio());
            throw new IllegalArgumentException("Horário já ocupado para este nutricionista");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setPaciente(paciente);
        agendamento.setNutricionista(nutricionista);
        agendamento.setServico(servico);
        agendamento.setData(dto.data());
        agendamento.setHoraInicio(dto.horaInicio());
        agendamento.setHoraFim(horaFim);

        AgendamentoResponseDTO resultado = AgendamentoResponseDTO.de(agendamentoRepository.save(agendamento));
        log.info("Agendamento criado com sucesso: id={}, paciente={}, data={}, horario={}-{}",
                resultado.id(), paciente.getNome(), resultado.data(), resultado.horaInicio(), resultado.horaFim());
        return resultado;
    }

    public List<AgendamentoResponseDTO> listarPorNutricionistaEData(Long nutricionistaId, java.time.LocalDate data) {
        List<AgendamentoResponseDTO> lista = agendamentoRepository.findByNutricionistaIdAndData(nutricionistaId, data)
                .stream()
                .map(AgendamentoResponseDTO::de)
                .toList();
        log.info("Listagem: nutricionistaId={}, data={}, total={}", nutricionistaId, data, lista.size());
        return lista;
    }

    public void cancelar(Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Agendamento não encontrado para cancelamento: id={}", id);
                    return new IllegalArgumentException("Agendamento não encontrado");
                });

        if (agendamento.getStatus() == StatusAgendamento.CANCELADO) {
            log.warn("Tentativa de cancelar agendamento já cancelado: id={}", id);
            throw new IllegalArgumentException("Agendamento já está cancelado");
        }

        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamentoRepository.save(agendamento);
        log.info("Agendamento cancelado: id={}, paciente={}", id, agendamento.getPaciente().getNome());
    }
}