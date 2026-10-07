package br.org.bandasantabarbara.application.dtos.convite;

import br.org.bandasantabarbara.application.services.convite.EnumTipoCanal;
import io.swagger.v3.oas.annotations.media.Schema;

public record CanalDTO(

        @Schema(
                description = "Canal utilizado para envio do convite.",
                example = "EMAIL"
        )
        EnumTipoCanal canal

) {}