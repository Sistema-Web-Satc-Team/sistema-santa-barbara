package br.org.bandasantabarbara.application.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import br.org.bandasantabarbara.application.dtos.turmas.AtualizarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.CriarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.GradeTurmaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.HorarioResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.IntervaloHorarioResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseResumidoDTO;
import br.org.bandasantabarbara.application.services.turma.HorarioFactory;
import br.org.bandasantabarbara.model.turma.Horario;
import br.org.bandasantabarbara.model.turma.HorarioIntervalo;
import br.org.bandasantabarbara.model.turma.MatriculaAlunoTurma;
import br.org.bandasantabarbara.model.turma.Sala;
import br.org.bandasantabarbara.model.turma.Turma;

@Component 
public class TurmaMapper {

    private final SalaMapper salaMapper;
    private final MatriculaMapper matriculaMapper;
    private final ProfessorTurmaMapper professorTurmaMapper;

    public TurmaMapper(
        SalaMapper salaMapper,
        MatriculaMapper matriculaMapper,
        ProfessorTurmaMapper professorTurmaMapper
    ) {
        this.salaMapper = salaMapper;
        this.matriculaMapper = matriculaMapper;
        this.professorTurmaMapper = professorTurmaMapper;
    }
    
    public Turma toEntity(CriarTurmaRequestDTO dto, Sala sala) {
        Turma turma = new Turma(dto.nome(), sala);

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
            
            turma.atualizarPeriodo(novaDataInicio, novaDataFim);
        }
        
        if (dto.nome() != null) {
            turma.setNome(dto.nome());
        }
        
        if (dto.turno() != null || dto.horarios() != null) {
            var novoTurno = dto.turno() != null ? dto.turno() : turma.getTurno();
            var novoHorario = dto.horarios() != null
                ? HorarioFactory.criarApartirDe(dto.horarios())
                : turma.getHorario();
            turma.configurarHorario(novoTurno, novoHorario);
        }

        if (sala != null) {
            turma.setSala(sala);
        }
    }

    // Temos um problema grave abaixo que será mantido temporáriamente
    // Esse método pode ter o problema N+1, ou pior N+M+1 uma vez que
    // se o método utilizado no service buscar a penas horários ou salas
    // e não buscar as matriculas e professores, o método abaixo vai disparar queries adicionais para cada turma.
    // A solução seria criar um método no repository que busque todas as informações necessárias de uma vez só, 
    // mas isso é um pouco mais complexo e não será feito agora.
    public TurmaResponseDTO toResponse(Turma turma) {
        var matriculasAtivas = turma.getMatriculas().stream()
            .filter(MatriculaAlunoTurma::estaAtiva)
            .toList();

        var professoresAtivos = turma.getProfessores().stream()
            .filter(vinculo -> "ATIVO".equals(vinculo.getStatus()))
            .toList();

        return new TurmaResponseDTO(
            turma.getId(),
            turma.getNome(),
            matriculasAtivas.size(),
            professoresAtivos.size(),
            turma.getNumeroVagas(),
            turma.getDataInicio(),
            turma.getDataFim(),
            turma.getDataTermino(),
            turma.getTurno(),
            turma.getStatus(),
            converterHorario(turma.getHorario()),
            salaMapper.toResponse(turma.getSala()),
            matriculasAtivas.stream()
                .map(matriculaMapper::toTurmaAlunoResponse)
                .toList(),
            professoresAtivos.stream()
                .map(professorTurmaMapper::toTurmaResponse)
                .toList(),
            turma.getInstrumentos().stream()
                .map(it -> it.getInstrumento().getNome())
                .toList()
        );
    }

    // método mais leve e ideal para listagem de turmas, sem trazer informações detalhadas de alunos e professores
    public TurmaResponseResumidoDTO toResponseResumido(Turma turma) {
        return new TurmaResponseResumidoDTO(
            turma.getId(),
            turma.getNome(),
            turma.getNumeroDeAlunosAtivos(),
            turma.getNumeroDeProfessoresAtivos(),
            turma.getNumeroVagas(),
            turma.getDataInicio(),
            turma.getDataFim(),
            turma.getDataTermino(),
            turma.getTurno(),
            turma.getStatus(),
            salaMapper.toResponse(turma.getSala()),
            turma.getInstrumentos().stream()
                .map(instrumentoTurma -> instrumentoTurma.getInstrumento().getNome())
                .toList()
        );
    }

    public GradeTurmaResponseDTO toGradeResponse(Turma turma) {
        return new GradeTurmaResponseDTO(
            turma.getId(),
            turma.getNome(),
            turma.getDataTermino(),
            turma.getTurno(),
            converterHorario(turma.getHorario())
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
