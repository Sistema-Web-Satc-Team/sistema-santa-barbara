package br.org.bandasantabarbara.model.aula;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.model.ensino.Professor;
import br.org.bandasantabarbara.model.turma.Turma;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Entity 
@Table (name = "aula")
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "aula", orphanRemoval = true)
    @Getter @Setter 
    private List<Chamada> chamadas = new ArrayList<Chamada>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_turma", nullable = false)
    @Getter
    private Turma turma;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_professor", nullable = false)
    @Getter
    private Professor professor;

    @Getter
    @Column(name = "data_hora_inicio")
    private LocalDateTime dataHoraInicio;
    
    @Getter
    @Column(name = "data_hora_fim_prevista", nullable = false)
    private LocalDateTime dataHoraFimPrevista;

    @Getter
    @Column(name = "data_hora_fim")
    private LocalDateTime dataHoraFim;

    @Getter 
    @Column(name = "descricao")
    private String descricao;

    public enum EnumTipoEncerramento {
        MANUAL,
        TEMPO_ESGOTADO
    }



    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_encerramento")
    @Getter
    private EnumTipoEncerramento tipoEncerramento;


    protected Aula() { }
    
    // Métodos de negócio
    // No cenário atual vamos apenas considerar que uma aula só existe
    // quando ela for iniciada, então não vamos criar um construtor público para a classe Aula.
    // O objetivo desse módulo não é provar se o professor deu aula ou não.

    public static Aula iniciar(
        Turma turma,
        Professor professor,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFimPrevista
    ) {
        if (turma == null || professor == null || dataHoraInicio == null || dataHoraFimPrevista == null
            || !dataHoraFimPrevista.isAfter(dataHoraInicio)) {
            throw new BadRequestException("Os dados para iniciar a aula são inválidos.");
        }

        Aula aula = new Aula();
        aula.turma = turma;
        aula.professor = professor;
        aula.dataHoraInicio = dataHoraInicio;
        aula.dataHoraFimPrevista = dataHoraFimPrevista;
        aula.statusAula = EnumStatusAula.EM_ANDAMENTO;
        return aula;
    }

    public void finalizarManualmente(LocalDateTime dataHoraFim) {
        finalizar(dataHoraFim, EnumTipoEncerramento.MANUAL);
    }

    public void finalizarPorTempoEsgotado() {
        finalizar(dataHoraFimPrevista, EnumTipoEncerramento.TEMPO_ESGOTADO);
    }

    private void finalizar(LocalDateTime dataHoraFim, EnumTipoEncerramento tipoEncerramento) {
        if (isFinalizada()) {
            throw new BadRequestException("A aula já foi finalizada.");
        }
        if (dataHoraFim == null || dataHoraFim.isBefore(dataHoraInicio)) {
            throw new BadRequestException("O horário de encerramento da aula é inválido.");
        }

        this.statusAula = EnumStatusAula.FINALIZADA;
        this.dataHoraFim = dataHoraFim;
        this.tipoEncerramento = tipoEncerramento;
    }

    public void reabrir(LocalDateTime agora) {
        if (!isFinalizada()) {
            throw new BadRequestException("A aula não foi finalizada, portanto não pode ser reaberta.");
        }
        if (tipoEncerramento != EnumTipoEncerramento.MANUAL) {
            throw new BadRequestException("Apenas aulas finalizadas manualmente podem ser reabertas.");
        }
        if (agora == null || agora.isAfter(dataHoraFimPrevista)) {
            throw new BadRequestException("O horário previsto para a aula já foi ultrapassado.");
        }

        this.statusAula = EnumStatusAula.EM_ANDAMENTO;
        this.dataHoraFim = null;
        this.tipoEncerramento = null;
    }

    public boolean isFinalizada() {
        return this.statusAula == EnumStatusAula.FINALIZADA;
    }

    public enum EnumStatusAula {
        EM_ANDAMENTO,
        FINALIZADA
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnumStatusAula statusAula;

    public EnumStatusAula getStatusAula() {
        return this.statusAula;
    }

    // Setters

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new BadRequestException("Descrição não pode ser nula ou vazia");
        }

        if (isFinalizada()) {
            throw new BadRequestException("Não é possível alterar a descrição de uma aula finalizada");
        }

        this.descricao = descricao.trim().toLowerCase();
    }



}