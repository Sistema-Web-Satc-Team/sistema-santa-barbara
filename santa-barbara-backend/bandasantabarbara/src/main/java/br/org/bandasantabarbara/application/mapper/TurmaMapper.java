package br.org.bandasantabarbara.application.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import br.org.bandasantabarbara.application.dtos.turmas.AtualizarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.CriarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.HorarioResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.IntervaloHorarioResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseDTO;
import br.org.bandasantabarbara.application.services.turma.HorarioFactory;
import br.org.bandasantabarbara.model.turma.Horario;
import br.org.bandasantabarbara.model.turma.HorarioIntervalo;
import br.org.bandasantabarbara.model.turma.Sala;
import br.org.bandasantabarbara.model.turma.Turma;

@Component 
public class TurmaMapper {

    private final SalaMapper salaMapper;

    public TurmaMapper(SalaMapper salaMapper) {
        this.salaMapper = salaMapper;
    }
    
    public Turma toEntity(CriarTurmaRequestDTO dto, Sala sala) {
        Turma turma = new Turma(dto.nome(), sala);

        turma.agendarPeriodo(dto.dataInicio(), dto.dataFim());
        turma.setHorario(HorarioFactory.criarApartirDe(dto.horarios()));
        turma.setNumeroVagas(dto.numeroVagas());
        turma.setTurno(dto.turno());

        return turma;
    }

    public void toUpdate(AtualizarTurmaRequestDTO dto, Turma turma, Sala sala) {
        if (dto.numeroVagas() != null) {
            turma.setNumeroVagas(dto.numeroVagas());
        }

        if (dto.dataInicio() != null || dto.dataFim() != null) {
            LocalDate novaDataInicio = (dto.dataInicio() != null) ? dto.dataInicio() : turma.getDataInicio();
            LocalDate novaDataFim = (dto.dataFim() != null) ? dto.dataFim() : turma.getDataFim();
            
            turma.agendarPeriodo(novaDataInicio, novaDataFim);
        }
        
        if (dto.nome() != null) {
            turma.setNome(dto.nome());
        }
        
        if (dto.turno() != null) {
            turma.setTurno(dto.turno());
        }
        
        if (dto.horarios() != null) {
            turma.setHorario(HorarioFactory.criarApartirDe(dto.horarios())); 
        }

        if (sala != null) {
            turma.setSala(sala);
        }
    }

    public TurmaResponseDTO toResponse(Turma turma) {
        return new TurmaResponseDTO(
            turma.getId(),
            turma.getNome(),
            turma.getNumeroVagas(),
            turma.getDataInicio(),
            turma.getDataFim(),
            turma.getDataTermino(),
            turma.getTurno(),
            converterHorario(turma.getHorario()),
            salaMapper.toResponse(turma.getSala())
        );
    }

    private HorarioResponseDTO converterHorario(Horario horario) {
        if (horario == null) return null;

        return new HorarioResponseDTO(
            converterIntervalos(horario.getSegunda()),
            converterIntervalos(horario.getTerca()),
            converterIntervalos(horario.getQuarta()),
            converterIntervalos(horario.getQuinta()),
            converterIntervalos(horario.getSexta()),
            converterIntervalos(horario.getSabado()),
            converterIntervalos(horario.getDomingo())
        );
    }

    private List<IntervaloHorarioResponseDTO> converterIntervalos(List<HorarioIntervalo> intervalos) {
        if (intervalos == null) return new ArrayList<IntervaloHorarioResponseDTO>();
        

        return intervalos.stream()
            .map(i -> new IntervaloHorarioResponseDTO(i.getInicio(), i.getFim()))
            .toList();
    }

}
