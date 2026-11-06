package br.org.bandasantabarbara.application.events;

import br.org.bandasantabarbara.model.ensino.Aluno;
import br.org.bandasantabarbara.model.ensino.Professor;
import br.org.bandasantabarbara.model.membro.Membro;
import br.org.bandasantabarbara.repositories.ensino.AlunoRepository;
import br.org.bandasantabarbara.repositories.ensino.ProfessorRepository;
import br.org.bandasantabarbara.repositories.membro.MembroRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MembroVinculoEventListener {

    private final AlunoRepository alunoRepository;
    private final MembroRepository membroRepository;
    private final ProfessorRepository professorRepository;

    public MembroVinculoEventListener(
        AlunoRepository alunoRepository, 
        ProfessorRepository professorRepository,
        MembroRepository membroRepository
    ) {
        this.alunoRepository = alunoRepository;
        this.membroRepository = membroRepository;
        this.professorRepository = professorRepository;
    }

    @EventListener
    @Transactional
    public void aoCriarMembroVinculo(MembroVinculoCriadoEvent event) {
        if ("ALUNO".equalsIgnoreCase(event.codigoFuncao())) {
            
        if (!alunoRepository.existsById(event.idMembro())) {
                Membro membro = membroRepository.getReferenceById(event.idMembro());
                
                Aluno novoAluno = new Aluno();
                novoAluno.setMembro(membro);
                
                alunoRepository.save(novoAluno);
            }
        }


         if ("PROFESSOR".equalsIgnoreCase(event.codigoFuncao())) {
            
            if (!professorRepository.existsById(event.idMembro())) {
                Membro membro = membroRepository.getReferenceById(event.idMembro());
                
                Professor novoProfessor = new Professor();
                novoProfessor.setMembro(membro);
                
                professorRepository.save(novoProfessor);
            }
        }
    }
}
