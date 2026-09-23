package br.org.bandasantabarbara.application.usecase.convites;

import br.org.bandasantabarbara.application.dtos.OffsetPaginationRequest;
import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.convite.ConsultarConviteResponse;
import br.org.bandasantabarbara.application.dtos.convite.ConvidarMembroResponse;
import br.org.bandasantabarbara.application.dtos.membros.MembroResumidoResponse;
import br.org.bandasantabarbara.exception.NaoEncontradoException;
import br.org.bandasantabarbara.model.Convite;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.repositories.ConviteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConsultarConvite {

    private final ConviteRepository conviteRepository;

    @Value("${frontend.invitation.url}")
    private String frontEndUrl;

    public ConsultarConvite(ConviteRepository conviteRepository) {
        this.conviteRepository = conviteRepository;
    }


    public ConsultarConviteResponse pegarPorId(UUID idConvite) {

        Convite convite = this.conviteRepository
                .findById(idConvite)
                .orElseThrow(() -> new NaoEncontradoException("Convite não encontrado ou inexistente."));

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

    public PageResponse<ConsultarConviteResponse> listar(OffsetPaginationRequest dto) {

        Pageable pageable = PageRequest.of(
                dto.page(),
                dto.size(),
                Sort.by("dataCriacaoConvite").descending()
        );

        List<Convite> convites = this.conviteRepository.listarTodosConvites(pageable);


        return new PageResponse<>(
                convites.stream().map(
                    (convite) ->
                    {
                        String urlConvite = frontEndUrl + convite.getId();

                        Membro membro = convite.getMembro(); // TEORICAMENTE isso estaria em memória!

                        return new ConsultarConviteResponse(
                            convite.getId(),
                            urlConvite,
                            convite.getDataCriacaoConvite(),
                            convite.getDataExpiracaoConvite(),
                            convite.getStatus(),
                            MembroResumidoResponse.deEntidade(membro)
                        );
                    }
                ).toList(),

                dto.size(),
                dto.page()
        );
    }

}
