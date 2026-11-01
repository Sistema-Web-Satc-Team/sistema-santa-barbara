package br.org.bandasantabarbara.integration;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.sala.SalaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.AtualizarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.CriarTurmaRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.HorarioRequestDTO;
import br.org.bandasantabarbara.application.dtos.turmas.TurmaResponseDTO;
import br.org.bandasantabarbara.application.services.turma.SalaService;
import br.org.bandasantabarbara.application.services.turma.TurmaService;
import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.model.turma.Turma.EnumTipoTurno;

@SpringBootTest  
@ActiveProfiles ("test")
@Transactional 
public class TurmaServiceIntegrationTest {
    
    @Autowired 
    private TurmaService turmaService;

    @Autowired
    private SalaService salaService;


    private void cadastrarSalas() {
        salaService.cadastrar(
            new SalaRequestDTO(
                "1.10", 
                12
            )
        );

        salaService.cadastrar(
            new SalaRequestDTO(
                "1.11", 
                15
            )
        );

        salaService.cadastrar(
            new SalaRequestDTO(
                "1.12", 
                20
            )
        );

    }

    @Test
    public void deveCriarUmaTurmaComDataInicioEFimEHorario() {
        
        cadastrarSalas();

        //
        // Como ele é o teste de cadastro de turma
        // ele deve ser explicito cada passo.
        //

        List<HorarioRequestDTO> dtoHorarios = new ArrayList<>();

        var currentTime = LocalTime.of(10, 00);
        var currentDate = LocalDate.now();

        EnumTipoTurno tipoTurno = EnumTipoTurno.MATUTINO;


        dtoHorarios.add(new HorarioRequestDTO(
            DayOfWeek.MONDAY,
            currentTime,
            currentTime.plusMinutes(90)
        ));

        CriarTurmaRequestDTO turmaRequestDTO = new CriarTurmaRequestDTO(
            "1.10",
            12,
            currentDate.plusDays(7),
            currentDate.plusDays(14),
            "Aulas de clarinete.",
            tipoTurno,
            dtoHorarios
        );

        TurmaResponseDTO turmaResponseDTO = turmaService.criar(turmaRequestDTO);

        Assertions.assertThat(turmaResponseDTO.nome()).isEqualTo("aulas de clarinete."); 
        Assertions.assertThat(turmaResponseDTO.numeroVagas()).isEqualTo(12);
        Assertions.assertThat(turmaResponseDTO.dataInicio()).isEqualTo(currentDate.plusDays(7));
        Assertions.assertThat(turmaResponseDTO.dataFim()).isEqualTo(currentDate.plusDays(14));
        Assertions.assertThat(turmaResponseDTO.dataTermino()).isNull();
        Assertions.assertThat(turmaResponseDTO.sala().code()).isEqualTo("1.10");
        Assertions.assertThat(turmaResponseDTO.sala().capacidade()).isGreaterThanOrEqualTo(12);
        Assertions.assertThat(turmaResponseDTO.turno()).isEqualTo(tipoTurno);

        Assertions.assertThat(turmaResponseDTO.horarios().segunda()).isNotEmpty();
    }

    @Test 
    public void naoDeveCriarTurmaComVagasMaiorQueCapacidade() {
        cadastrarSalas();

        List<HorarioRequestDTO> dtoHorarios = new ArrayList<>();

        var currentTime = LocalTime.of(10, 00);
        var currentDate = LocalDate.now();

        EnumTipoTurno tipoTurno = EnumTipoTurno.MATUTINO;

        dtoHorarios.add(new HorarioRequestDTO(
            DayOfWeek.MONDAY,
            currentTime,
            currentTime.plusMinutes(90)
        ));

        CriarTurmaRequestDTO turmaRequestDTO = new CriarTurmaRequestDTO(
            "1.10",
            200,
            currentDate.plusDays(7),
            currentDate.plusDays(14),
            "Aulas de clarinete.",
            tipoTurno,
            dtoHorarios
        );

        Assertions.assertThatThrownBy(() -> {
            this.turmaService.criar(turmaRequestDTO);
        })
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("vagas não pode exceder a capacidade máxima da sala")
        .hasNoCause();
    }


    private void criarTurmas() {
        this.cadastrarSalas();

        List<HorarioRequestDTO> dtoHorarios = new ArrayList<>();

        var currentTime = LocalTime.of(10, 00);
        var currentDate = LocalDate.now();

        EnumTipoTurno tipoTurno = EnumTipoTurno.MATUTINO;

        dtoHorarios.add(new HorarioRequestDTO(
            DayOfWeek.MONDAY,
            currentTime,
            currentTime.plusMinutes(90)
        ));

        dtoHorarios.add(new HorarioRequestDTO(
            DayOfWeek.FRIDAY,
            currentTime,
            currentTime.plusMinutes(90)
        ));

        dtoHorarios.add(new HorarioRequestDTO(
            DayOfWeek.THURSDAY,
            currentTime,
            currentTime.plusMinutes(90)
        ));

        dtoHorarios.add(new HorarioRequestDTO(
            DayOfWeek.WEDNESDAY,
            currentTime,
            currentTime.plusMinutes(90)
        ));

        dtoHorarios.add(new HorarioRequestDTO(
            DayOfWeek.TUESDAY,
            currentTime,
            currentTime.plusMinutes(90)
        ));


        turmaService.criar(new CriarTurmaRequestDTO(
            "1.10",
            12,
            currentDate.plusDays(7),
            currentDate.plusDays(14),
            "Aulas de clarinete.",
            tipoTurno,
            dtoHorarios
        ));

        turmaService.criar(new CriarTurmaRequestDTO(
            "1.11",
            15,
            currentDate.plusDays(7),
            currentDate.plusDays(14),
            "Aulas de clarinete.",
            tipoTurno,
            dtoHorarios
        ));

         turmaService.criar(new CriarTurmaRequestDTO(
            "1.12",
            20,
            currentDate.plusDays(7),
            currentDate.plusDays(14),
            "Aulas de clarinete.",
            tipoTurno,
            dtoHorarios
        ));

    }


    @Test 
    public void deveListarTodasAsTurmasComPaginacao() {
        this.criarTurmas();

        Pageable pageable = PageRequest.of(
            0,
            10,
            Sort.by("nome").ascending()
        );

        PageResponse<TurmaResponseDTO> turmasPage = this.turmaService.listar(
            pageable
        );

        Assertions.assertThat(turmasPage.content().size()).isEqualTo(3);
        Assertions.assertThat(turmasPage.totalElements()).isEqualTo(3);
        Assertions.assertThat(turmasPage.page()).isEqualTo(0);
        Assertions.assertThat(turmasPage.totalPages()).isEqualTo(1);
        Assertions.assertThat(turmasPage.size()).isEqualTo(10);
    }

    @Test 
    public void deveAtualizarDadosDeUmaTurmaComSucesso() {
        cadastrarSalas();


        var currentDate = LocalDate.now();
        List<HorarioRequestDTO> dtoHorarios = new ArrayList<>();
        dtoHorarios.add(new HorarioRequestDTO(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 30)));

        CriarTurmaRequestDTO criarDto = new CriarTurmaRequestDTO(
            "1.10", 12, currentDate.plusDays(7), currentDate.plusDays(14),
            "Turma Original", EnumTipoTurno.MATUTINO, dtoHorarios
        );
        TurmaResponseDTO turmaCriada = turmaService.criar(criarDto);

        AtualizarTurmaRequestDTO atualizarDto = new AtualizarTurmaRequestDTO(
            "1.12",                       
            18,                             
            currentDate.plusDays(10),       
            currentDate.plusDays(20),       
            "Turma atualizada",            
            EnumTipoTurno.VESPERTINO,      
            dtoHorarios                    
        );


        TurmaResponseDTO turmaAtualizada = turmaService.atualizar(turmaCriada.id(), atualizarDto);

        Assertions.assertThat(turmaAtualizada.id()).isEqualTo(turmaCriada.id());
        Assertions.assertThat(turmaAtualizada.nome()).isEqualTo("turma atualizada");
        Assertions.assertThat(turmaAtualizada.numeroVagas()).isEqualTo(18);
        Assertions.assertThat(turmaAtualizada.sala().code()).isEqualTo("1.12");
        Assertions.assertThat(turmaAtualizada.dataInicio()).isEqualTo(currentDate.plusDays(10));
        Assertions.assertThat(turmaAtualizada.dataFim()).isEqualTo(currentDate.plusDays(20));
        Assertions.assertThat(turmaAtualizada.turno()).isEqualTo(EnumTipoTurno.VESPERTINO);
    }

    @Test
    public void naoDeveAtualizarTurmaComVagasMaiorQueCapacidadeDaSala() {
        cadastrarSalas();


        var currentDate = LocalDate.now();
        List<HorarioRequestDTO> dtoHorarios = new ArrayList<>();
        dtoHorarios.add(new HorarioRequestDTO(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 30)));

        CriarTurmaRequestDTO criarDto = new CriarTurmaRequestDTO(
            "1.10", 12, currentDate.plusDays(7), currentDate.plusDays(14),
            "Turma Validação Vagas", EnumTipoTurno.MATUTINO, dtoHorarios
        );
        TurmaResponseDTO turmaCriada = turmaService.criar(criarDto);

        AtualizarTurmaRequestDTO atualizarDto = new AtualizarTurmaRequestDTO(
            "1.10",
            500,  
            null, null, null, null, null  
        );

        Assertions.assertThatThrownBy(() -> {
            this.turmaService.atualizar(turmaCriada.id(), atualizarDto);
        })
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("vagas não pode exceder a capacidade máxima da sala")
        .hasNoCause();
    }


}
