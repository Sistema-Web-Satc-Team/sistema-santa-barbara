package br.org.bandasantabarbara.application.usecase.convites;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.convite.ConsultarConviteResponse;
import br.org.bandasantabarbara.application.dtos.membros.MembroResumidoResponse;
import br.org.bandasantabarbara.application.mapper.PageMapper;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Convite;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.repositories.ConviteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ConsultarConvite {

    private final ConviteRepository conviteRepository;

    @Value("${frontend.invitation.url}")
    private String frontEndUrl;

    private final PageMapper pageMapper;

    public ConsultarConvite(ConviteRepository conviteRepository, PageMapper pageMapper) {
        this.conviteRepository = conviteRepository;
        this.pageMapper = pageMapper;
    }


    public ConsultarConviteResponse pegarPorId(UUID idConvite) {

        Convite convite = this.conviteRepository
                .findById(idConvite)
                .orElseThrow(() -> new NotFoundException("Convite não encontrado ou inexistente."));

        String urlConvite = frontEndUrl + convite.getId();


        Membro membro = convite.getMembro();

        return new ConsultarConviteResponse(
                convite.getId(),
                urlConvite,
                convite.getDataCriacaoConvite(),
                convite.getDataExpiracaoConvite(),
                convite.getStatus(),
                MembroResumidoResponse.deEntidade(membro)
        );
    }

    public PageResponse<ConsultarConviteResponse> listar(Pageable pageable) {

        Page<Convite> convites = this.conviteRepository.listarTodosConvites(pageable);

        Page<ConsultarConviteResponse> paginaResponse = convites.map(convite -> new ConsultarConviteResponse(
                convite.getId(),
                frontEndUrl + convite.getId(),
                convite.getDataCriacaoConvite(),
                convite.getDataExpiracaoConvite(),
                convite.getStatus(),
                new MembroResumidoResponse(
                        convite.getMembro().getId(),
                        convite.getMembro().getNome(),
                        convite.getMembro().getEmail(),
                        convite.getMembro().getTelefone()
                )
        ));

        return this.pageMapper.toResponse(paginaResponse);
    }


    public boolean isConviteValido(UUID idConvite) {
        return conviteRepository
                .findById(idConvite)
                .map(Convite::podeSerAceito)
                .orElse(false);
    }
}
