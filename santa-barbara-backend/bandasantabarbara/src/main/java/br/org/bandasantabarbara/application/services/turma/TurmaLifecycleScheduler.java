package br.org.bandasantabarbara.application.services.turma;

import br.org.bandasantabarbara.repositories.turma.MatriculaAlunoTurmaRepository;
import br.org.bandasantabarbara.repositories.turma.ProfessorTurmaRepository;
import br.org.bandasantabarbara.repositories.turma.TurmaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TurmaLifecycleScheduler {

    private final TurmaRepository turmaRepository;
    private final MatriculaAlunoTurmaRepository matriculaRepository;
    private final ProfessorTurmaRepository professorTurmaRepository;

    public TurmaLifecycleScheduler(
        TurmaRepository turmaRepository,
        MatriculaAlunoTurmaRepository matriculaRepository,
        ProfessorTurmaRepository professorTurmaRepository
    ) {
        this.turmaRepository = turmaRepository;
        this.matriculaRepository = matriculaRepository;
        this.professorTurmaRepository = professorTurmaRepository;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void encerrarTurmasVencidas() {
        matriculaRepository.encerrarMatriculasDeTurmasVencidas();
        professorTurmaRepository.encerrarVinculosDeTurmasVencidas();
        turmaRepository.encerrarTurmasVencidas();
    }
}
