package br.org.bandasantabarbara.application.dtos.turmas;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.org.bandasantabarbara.model.turma.Turma.EnumTipoTurno;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(
    description = "Contrato para cadastro de turma.",
    example = """
    {
      "codigoSala": "SALA-A102",
      "numeroVagas": 35,
      "nome": "Turma de Clarinete",
      "turno": "NOTURNO",
      "idsInstrumentos": [1, 2]
    }
    """
)
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @NotBlank(message = "O nome da turma deve ser definido.")
    @Schema(
        description = "Nome descritivo da turma.", 
        example = "Turma de Java Avançado - Noturno"
    )
    String nome,

    @NotNull(message = "O turno da turma deve ser definido.")
    @Schema(
        description = "Turno: MATUTINO (06:00–12:00), VESPERTINO (13:00–18:00), NOTURNO (18:00–23:00), INTEGRAL (06:00–18:00) ou CONTRATURNO (sem janela fixa, definida pela instituição)."
    )
    @JsonDeserialize(using = EnumTipoTurnoDeserializer.class)
    EnumTipoTurno turno,

    @Schema(description = "IDs dos instrumentos ensinados na turma, quando aplicável.")
    List<Integer> idsInstrumentos
) {}
