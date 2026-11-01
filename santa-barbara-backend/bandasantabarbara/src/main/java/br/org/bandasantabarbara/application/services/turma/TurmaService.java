package br.org.bandasantabarbara.application.services.turma;

import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseDTO;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.application.mapper.TurmaMapper;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.turmas.AtualizarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.CriarTurmaRequestDTO;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.turma.Sala;
import br.org.bandasantabarbara.model.turma.Turma;
import br.org.bandasantabarbara.repositories.turma.TurmaRepository;
import jakarta.transaction.Transactional;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final SalaService salaService;

    private final TurmaMapper turmaMapper;
    private final PageMapper pageMapper;


    public TurmaService(
        TurmaRepository turmaRepository, 
        SalaService salaService,
        TurmaMapper turmaMapper,
        PageMapper pageMapper
    ) {
        this.turmaRepository = turmaRepository;
        this.salaService = salaService;
        this.turmaMapper = turmaMapper;
        this.pageMapper = pageMapper;
    }


    @Transactional
    public TurmaResponseDTO criar(CriarTurmaRequestDTO dto) {
        Sala sala = salaService.buscarEntidadePorCodigo(dto.codigoSala());
        
        if (dto.numeroVagas() > sala.getCapacidade()) 
            throw new BadRequestException("O número de vagas não pode exceder a capacidade máxima da sala " + sala.getCode() + ": " + sala.getCapacidade());
        
        Turma turma = this.turmaMapper.toEntity(dto, sala);

        turmaRepository.save(turma);
    
        return this.turmaMapper.toResponse(turma);
    }

   @Transactional 
    public TurmaResponseDTO atualizar(Long id, AtualizarTurmaRequestDTO dto) {
        Turma turma = buscarTurmaPorId(id);

        Sala salaDefinitiva = (dto.codigoSala() != null) 
            ? salaService.buscarEntidadePorCodigo(dto.codigoSala()) 
            : turma.getSala();

        Integer vagasDefinitivas = (dto.numeroVagas() != null) 
            ? dto.numeroVagas() 
            : turma.getNumeroVagas();

        if (salaDefinitiva != null && vagasDefinitivas > salaDefinitiva.getCapacidade()) {
            throw new BadRequestException("O número de vagas não pode exceder a capacidade máxima da sala " + salaDefinitiva.getCode() + ": " + salaDefinitiva.getCapacidade());
        }

        this.turmaMapper.toUpdate(dto, turma, salaDefinitiva);
    
        turmaRepository.save(turma); 

        return this.turmaMapper.toResponse(turma);
    }




    public PageResponse<TurmaResponseDTO> listar(Pageable pageable) {
        return this.pageMapper.toResponse(
            buscarTurmasPaginado(pageable).map(
                this.turmaMapper::toResponse
            )
        );
    }

    public Page<Turma> buscarTurmasPaginado(Pageable pageable) {
        return  this.turmaRepository.findAllWithHorarioSalaAndOptionalFilters(null, pageable);
    }

    public Turma buscarTurmaPorId(Long id) {
        return this.turmaRepository.findById(id).orElseThrow(
            () -> new NotFoundException("Turma não encontrada.")
        );
    }

    
    
}
