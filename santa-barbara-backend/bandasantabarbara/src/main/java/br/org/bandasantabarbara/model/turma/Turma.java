package br.org.bandasantabarbara.model.turma;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.model.musica.Instrumento;
import br.org.bandasantabarbara.model.turma.StatusTurma;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "turma")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column
    @Getter
    private String nome;

    @Column(name = "numero_vagas")
    @Getter 
    private Integer numeroVagas;


    public enum EnumTipoTurno {
        MATUTINO,
        VESPERTINO,
        NOTURNO,
        INTEGRAL,
        CONTRATURNO
    }


    @Column(name = "turno")
    @Getter @Setter 
    @Enumerated(EnumType.STRING)
    private EnumTipoTurno turno;

    @ManyToOne
    @JoinColumn(name = "sala_id")
    @Getter @Setter
    private Sala sala;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "horario_id")
    @Getter @Setter
    private Horario horario;

    @OneToMany(mappedBy = "turma")
    @BatchSize(size = 50)
    @Getter
    private List<MatriculaAlunoTurma> matriculas = new ArrayList<>();

    @OneToMany(mappedBy = "turma")
    @BatchSize(size = 50)
    @Getter
    private List<ProfessorTurma> professores = new ArrayList<>();

    @OneToMany(mappedBy = "turma")
    @BatchSize(size = 50)
    @Getter
    private List<InstrumentoTurma> instrumentos = new ArrayList<>();

    /*
    *
    * Data de inicio das aulas de uma turma
    * Ele é obrigatório para iniciar de fato as aulas de uma turma
    * e mudar o status de PLANEJAMENTO para EM_ANDAMENTO
    * 
    */

    @Column(name = "data_inicio")
    @Getter
    private LocalDate dataInicio;

    /*
    *
    * Data que o secretário planeja terminar as aulas da turma
    * Nesse sentido uma turma nao poderia ser INTERROMPIDA
    * Se não existe uma data fim estimada
    * 
    */

    @Column(name = "data_fim")
    @Getter
    private LocalDate dataFim;

    /*
    *
    * Gerenciado internamente pelo código
    * Através de métodos de negócio como 
    * 
    * void terminar()
    *
    * Através desse campo conseguimos determinar
    * se a turma foi ENCERRADA ou INTERROMPIDA
    * 
    */

    @Column(name = "data_termino")
    @Getter
    private LocalDate dataTermino;

    // Debito-05
    // Acoplamento da entidade com hibernate
    @Formula("""
        CASE 
            WHEN data_termino IS NOT NULL AND data_termino < data_inicio THEN 'CANCELADO'
            WHEN data_termino IS NOT NULL AND data_termino < data_fim THEN 'INTERROMPIDA'
            WHEN data_termino IS NOT NULL THEN 'ENCERRADO'
            WHEN data_inicio IS NULL OR CURRENT_DATE < data_inicio THEN 'PLANEJAMENTO'
            WHEN data_fim IS NOT NULL AND CURRENT_DATE >= data_fim THEN 'ENCERRADO'
            ELSE 'EM_ANDAMENTO'
        END
    """)
    @Enumerated(EnumType.STRING)
    private StatusTurma status;

    protected Turma() {

    }

    public Turma(String nome, Sala sala) {
        this.setNome(nome);
        this.sala = sala;
    }


    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw  new BadRequestException("Nome não pode estar vazio ou em branco.");
        }

        this.nome = nome.toLowerCase().trim();
    }

    public void agendarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio != null && inicio.isBefore(LocalDate.now())) {
            throw new BadRequestException("A data de início não pode ser no passado.");
        }

        if (inicio == null && fim != null) {
            throw new BadRequestException("Não é possível definir uma data de fim sem ter um inicio.");
        }

        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            throw new BadRequestException("A data de fim deve ocorrer após a data de inicio.");
        }

        this.dataInicio = inicio;
        this.dataFim = fim;
    }

    public void atualizarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio == null && fim != null) {
            throw new BadRequestException("Não é possível definir uma data de fim sem ter um início.");
        }
        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            throw new BadRequestException("A data de fim deve ocorrer após a data de início.");
        }
        this.dataInicio = inicio;
        this.dataFim = fim;
    }

    public void terminar() {
        if (this.dataTermino != null) {
            throw new BadRequestException("Não é possível terminar uma turma já terminou.");
        }
        this.dataTermino = LocalDate.now();
    }

    public boolean permiteNovasMatriculas() {
        return dataTermino == null && (dataFim == null || LocalDate.now().isBefore(dataFim));
    }

    public void definirInstrumentos(List<Instrumento> novosInstrumentos) {
        instrumentos.clear();
        novosInstrumentos.stream()
            .map(instrumento -> new InstrumentoTurma(this, instrumento))
            .forEach(instrumentos::add);
    }

    public void setTurno(EnumTipoTurno turno) {
        if (turno == null) {
            throw new BadRequestException("O turno da turma deve ser definido.");
        }

        validarHorarioTurno(this.horario, turno);
        this.turno = turno;
    }

    public void setHorario(Horario horario) {
        validarHorarioTurno(horario, this.turno);
        this.horario = horario;
    }

    public void configurarHorario(EnumTipoTurno turno, Horario horario) {
        validarHorarioTurno(horario, turno);
        this.turno = turno;
        this.horario = horario;
    }

    private void validarHorarioTurno(Horario horario, EnumTipoTurno turno) {
        if (horario == null || turno == null) {
            return;
        }

        for (HorarioIntervalo intervalo : horario.getIntervalos()) {
            LocalTime inicioMinimo;
            LocalTime fimMaximo;

            switch (turno) {
                case MATUTINO -> {
                    inicioMinimo = LocalTime.of(6, 0);
                    fimMaximo = LocalTime.of(12, 0);
                }
                case VESPERTINO -> {
                    inicioMinimo = LocalTime.of(13, 0);
                    fimMaximo = LocalTime.of(18, 0);
                }
                case NOTURNO -> {
                    inicioMinimo = LocalTime.of(18, 0);
                    fimMaximo = LocalTime.of(23, 0);
                }
                case INTEGRAL -> {
                    inicioMinimo = LocalTime.of(6, 0);
                    fimMaximo = LocalTime.of(18, 0);
                }
                case CONTRATURNO -> {
                    continue;
                }
                default -> throw new BadRequestException("Turno inválido.");
            }

            if (intervalo.getInicio().isBefore(inicioMinimo) || intervalo.getFim().isAfter(fimMaximo)) {
                throw new BadRequestException(
                    "O horário da turma deve estar dentro do turno " + turno + "."
                );
            }
        }
    }

    public StatusTurma getStatus() {
        LocalDate hoje = LocalDate.now();

        if (this.dataTermino != null) {
            if (this.dataTermino.isBefore(this.dataInicio)) {
                return StatusTurma.CANCELADO;
            }

            if (this.dataFim != null && this.dataTermino.isBefore(this.dataFim)) {
                return StatusTurma.INTERROMPIDA;
            }

            return StatusTurma.ENCERRADO;
        }

        if (this.dataInicio == null || hoje.isBefore(this.dataInicio)) {
            return StatusTurma.PLANEJAMENTO;
        }

        if (this.dataFim != null && !hoje.isBefore(this.dataFim)) {
            return StatusTurma.ENCERRADO;
        }

        return StatusTurma.EM_ANDAMENTO;
    }


    public void setNumeroVagas(Integer numeroVagas) {
        if (numeroVagas == null) return;

        if (numeroVagas <= 0) 
            throw new BadRequestException("O número de vagas máximo de uma turma não pode ser negativo.");
        

        this.numeroVagas = numeroVagas;
    }


}
