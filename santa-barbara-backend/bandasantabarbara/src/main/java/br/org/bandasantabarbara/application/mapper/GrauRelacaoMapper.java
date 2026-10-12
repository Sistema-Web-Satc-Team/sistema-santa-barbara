package br.org.bandasantabarbara.application.mapper;

import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroResponsavel;
import org.springframework.stereotype.Component;

@Component
public class GrauRelacaoMapper {


    public String toResponse(
            Membro.MembroSexoEnum sexo,
            MembroResponsavel.TipoRelacaoResponsavel relacao
    ) {

        if (relacao == MembroResponsavel.TipoRelacaoResponsavel.RESPONSAVEL_LEGAL) {
            return "Responsável Legal";
        }

        if (sexo == Membro.MembroSexoEnum.MASCULINO) {
            return switch (relacao) {
                case PROGENITORES -> "Pai";
                case AVO -> "Avô";
                case PADRINHO -> "Padrinho";
                case TIO -> "Tio";
                default -> "Responsável Legal";
            };
        }

        if (sexo == Membro.MembroSexoEnum.FEMININO) {
            return switch (relacao) {
                case PROGENITORES -> "Mãe";
                case AVO -> "Avó";
                case PADRINHO -> "Madrinha";
                case TIO -> "Tia";
                default -> "Responsável Legal";
            };
        }

        return "Responsável Legal";
    }
}
