package br.org.bandasantabarbara.application.dtos.aulas;

import br.org.bandasantabarbara.model.aula.Aula;
import java.time.LocalDateTime;

public record AulaResponse(
    Long id,
    LocalDateTime dataHoraInicio,
    LocalDateTime dataHoraFimPrevista,
    LocalDateTime dataHoraFim,
    Aula.EnumStatusAula status,
    Aula.EnumTipoEncerramento tipoEncerramento
) {
    public AulaResponse(Aula aula) {
        this(
            aula.getId(),
            aula.getDataHoraInicio(),
            aula.getDataHoraFimPrevista(),
            aula.getDataHoraFim(),
            aula.getStatusAula(),
            aula.getTipoEncerramento()
        );
    }
}
