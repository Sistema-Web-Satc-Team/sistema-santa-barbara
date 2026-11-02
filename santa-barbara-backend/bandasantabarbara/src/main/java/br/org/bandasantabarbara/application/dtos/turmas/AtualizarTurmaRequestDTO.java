package br.org.bandasantabarbara.application.dtos.turmas;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.org.bandasantabarbara.model.turma.Turma.EnumTipoTurno;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;


@JsonIgnoreProperties(ignoreUnknown = true)
public record AtualizarTurmaRequestDTO (

    @Schema(
        description = "Identificador único ou código da sala física.", 
        example = "1.10"
    )
    String codigoSala,

    @Schema(
        description = "Quantidade máxima de alunos permitida na turma.", 
        example = "35"
    )
    @Positive(message = "O número de vagas deve ser maior que zero.")
    Integer numeroVagas,

    @FutureOrPresent(message = "A data de inicio precisa ser a partir da data de hoje.")
    @Schema(
        description = "Data de início das aulas (pode ser hoje ou data futura).", 
        example = "2026-11-01"
    )
    LocalDate dataInicio,

    @FutureOrPresent(message = "A data de fim precisa ser a partir da data de hoje.")
    @Schema(
        description = "Data de encerramento das aulas.", 
        example = "2026-12-15"
    )
    LocalDate dataFim,
    
    @Schema(
        description = "Nome descritivo da turma.", 
        example = "Turma de Java Avançado - Noturno"
    )
    String nome,

    @Valid
    @Schema(
        description = "Turno: MATUTINO (06:00–12:00), VESPERTINO (13:00–18:00), NOTURNO (18:00–23:00), INTEGRAL (06:00–18:00) ou CONTRATURNO (sem janela fixa, definida pela instituição)."
    )
    @JsonDeserialize(using = EnumTipoTurnoDeserializer.class)
    EnumTipoTurno turno,

    @Valid
    @Schema(
        description = "Lista com os dias e horários em que as aulas acontecerão."
    )
    List<HorarioRequestDTO> horarios,

    @Schema(description = "IDs dos instrumentos ensinados na turma. Lista vazia remove todos.")
    List<Integer> idsInstrumentos
) {
    public AtualizarTurmaRequestDTO(
        String codigoSala,
        Integer numeroVagas,
        LocalDate dataInicio,
        LocalDate dataFim,
        String nome,
        EnumTipoTurno turno,
        List<HorarioRequestDTO> horarios
    ) {
        this(codigoSala, numeroVagas, dataInicio, dataFim, nome, turno, horarios, null);
    }
}