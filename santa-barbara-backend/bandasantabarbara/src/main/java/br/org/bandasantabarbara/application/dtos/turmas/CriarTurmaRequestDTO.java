package br.org.bandasantabarbara.application.dtos.turmas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.org.bandasantabarbara.model.turma.Turma.EnumTipoTurno;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

@Schema(
    description = "Contrato para cadastro de turma.",
    example = """
    {
      "codigoSala": "SALA-A102",
      "numeroVagas": 35,
      "dataInicio": "2026-11-01",
      "dataFim": "2026-12-15",
      "nome": "Turma de Clarinete",
      "turno": "NOTURNO",
      "horarios": [
        {
          "diaDaSemana": "SEGUNDA",
          "inicio": "19:00",
          "fim": "22:00"
        },
        {
          "diaDaSemana": "QUARTA",
          "inicio": "19:00",
          "fim": "22:00"
        }
      ],
      "idsInstrumentos": [1, 2]
    }
    """
)
public record CriarTurmaRequestDTO(
    @NotBlank(message = "Código da sala física é obrigatório.")
    @Schema(
        description = "Identificador único ou código da sala física.", 
        example = "1.10"
    )
    String codigoSala,

    @NotNull(message = "A turma deve ter um número de vagas pré definido.")
    @Schema(
        description = "Quantidade máxima de alunos permitida na turma.", 
        example = "35"
    )
    Integer numeroVagas,

    @NotNull(message = "A data de início é obrigatória.")
    @FutureOrPresent(message = "A data de inicio precisa ser a partir da data de hoje.")
    @Schema(
        description = "Data de início das aulas (pode ser hoje ou data futura).", 
        example = "2026-11-01"
    )
    LocalDate dataInicio,

    @NotNull(message = "A data de fim planejada é obrigatória.")
    @FutureOrPresent(message = "A data de fim precisa ser a partir da data de hoje.")
    @Schema(
        description = "Data de encerramento das aulas.", 
        example = "2026-12-15"
    )
    LocalDate dataFim,
    
    @NotBlank(message = "O nome da turma deve ser definido.")
    @Schema(
        description = "Nome descritivo da turma.", 
        example = "Turma de Java Avançado - Noturno"
    )
    String nome,

    @NotNull(message = "O turno da turma deve ser definido.")
    @Valid
    @Schema(
        description = "Turno: MATUTINO (06:00–12:00), VESPERTINO (13:00–18:00), NOTURNO (18:00–23:00), INTEGRAL (06:00–18:00) ou CONTRATURNO (sem janela fixa, definida pela instituição)."
    )
    @JsonDeserialize(using = EnumTipoTurnoDeserializer.class)
    EnumTipoTurno turno,

    @Valid
    @NotEmpty(message = "A turma deve ter ao menos um horário.")
    @Schema(
        description = "Lista com os dias e horários em que as aulas acontecerão."
    )
    List<HorarioRequestDTO> horarios,

    @Schema(description = "IDs dos instrumentos ensinados na turma, quando aplicável.")
    List<Integer> idsInstrumentos
) {
    public CriarTurmaRequestDTO(
        String codigoSala,
        Integer numeroVagas,
        LocalDate dataInicio,
        LocalDate dataFim,
        String nome,
        EnumTipoTurno turno,
        List<HorarioRequestDTO> horarios
    ) {
        this(codigoSala, numeroVagas, dataInicio, dataFim, nome, turno, horarios, new ArrayList<>());
    }
}
