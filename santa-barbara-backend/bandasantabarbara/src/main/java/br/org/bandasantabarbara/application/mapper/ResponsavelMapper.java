package br.org.bandasantabarbara.application.mapper;

import br.org.bandasantabarbara.application.dtos.membros.responsaveis.ResponsavelResponse;
import br.org.bandasantabarbara.model.MembroResponsavel;
import org.springframework.stereotype.Component;

@Component
public class ResponsavelMapper {

    private final GrauRelacaoMapper grauRelacaoMapper;

    public ResponsavelMapper(GrauRelacaoMapper grauRelacaoMapper) {
        this.grauRelacaoMapper = grauRelacaoMapper;
    }


    public ResponsavelResponse toResponse(MembroResponsavel responsavel) {
        return new ResponsavelResponse(
                responsavel.getResponsavel().getId(),
                responsavel.getResponsavel().getNome(),
                responsavel.getResponsavel().getTelefone(),
                responsavel.getResponsavel().getEndereco(),
                responsavel.getResponsavel().getEmail(),
                responsavel.getResponsavel().getDataNascimento(),
                this.grauRelacaoMapper.toResponse(responsavel.getResponsavel().getSexo(), responsavel.getTipoRelacao())
        );
    }
}
