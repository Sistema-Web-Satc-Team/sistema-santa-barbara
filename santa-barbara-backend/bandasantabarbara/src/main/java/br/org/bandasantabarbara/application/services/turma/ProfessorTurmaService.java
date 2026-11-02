package br.org.bandasantabarbara.application.services.turma;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.turmas.MatriculaResponseDTO;
import br.org.bandasantabarbara.application.dtos.turmas.ProfessorTurmaResponseDTO;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.application.mapper.ProfessorTurmaMapper;
import br.org.bandasantabarbara.repositories.turma.ProfessorTurmaRepository;

@Service 
public class ProfessorTurmaService {

    private final ProfessorTurmaRepository professorTurmaRepository;
    private final ProfessorTurmaMapper professorTurmaMapper;
    private final PageMapper pageMapper;

    public ProfessorTurmaService(ProfessorTurmaRepository professorTurmaRepository, ProfessorTurmaMapper professorTurmaMapper, PageMapper pageMapper) {
        this.professorTurmaRepository = professorTurmaRepository;
        this.professorTurmaMapper = professorTurmaMapper;
        this.pageMapper = pageMapper;
    }
    

    @Transactional(readOnly = true)
    public PageResponse<ProfessorTurmaResponseDTO> listarDoProfessor(UUID idProfessor, Pageable pageable, String status) {
        return pageMapper.toResponse(
            professorTurmaRepository.listarPorProfessorEStatus(idProfessor, status, pageable).map(professorTurmaMapper::toResponse)
        );
    }
}
