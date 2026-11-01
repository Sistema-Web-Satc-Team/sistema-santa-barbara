package br.org.bandasantabarbara.application.dtos.turmas;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import br.org.bandasantabarbara.model.turma.Turma.EnumTipoTurno;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(
    description = "Contrato para cadastro de turma.",
    example = """
    {
      "codigoSala": "SALA-A102",
      "numeroVagas": 35,
      "dataInicio": "2026-11-01",
      "dataFim": "2026-12-15",
      "nome": "Turma de Clarinete",
      "horarios": [
        {
          "diaSemana": "SEGUNDA",
          "horaInicio": "19:00",
          "horaFim": "22:00"
        },
        {
          "diaSemana": "QUARTA",
          "horaInicio": "19:00",
          "horaFim": "22:00"
        }
      ]
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

    @FutureOrPresent(message = "A data de inicio precisa ser a partir da data de hoje.")
    @Schema(
        description = "Data de início das aulas (pode ser hoje ou data futura).", 
        example = "2026-11-01"
    )
    LocalDate dataInicio,

    @FutureOrPresent(message = "A data de inicio precisa ser a partir da data de hoje.")
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

    @NotBlank(message = "O turno da turma deve ser definido.")
    @Valid
    @Schema(
        description = "Turno da turma."
    )
    @JsonDeserialize(using = DayOfWeekDeserializer.class)
    EnumTipoTurno turno,

    @Valid
    @Schema(
        description = "Lista com os dias e horários em que as aulas acontecerão."
    )
    @JsonDeserialize(using = DayOfWeekDeserializer.class)
    List<HorarioRequestDTO> horarios
) {
}


