package br.org.bandasantabarbara.application.usecase;

import br.org.bandasantabarbara.application.dtos.*;
import br.org.bandasantabarbara.application.dtos.membros.AtualizarMembroParcialmenteRequest;
import br.org.bandasantabarbara.application.dtos.membros.MembroResponse;
import br.org.bandasantabarbara.application.dtos.membros.RegistrarMembroRequest;
import br.org.bandasantabarbara.application.dtos.profile.AtualizarMeParcialmenteRequest;
import br.org.bandasantabarbara.application.dtos.profile.MeResponse;
import br.org.bandasantabarbara.exception.NaoEncontradoException;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.Papel;
import br.org.bandasantabarbara.repositories.MembroRepository;
import br.org.bandasantabarbara.repositories.PapelRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MembroUsecase {
    private final MembroRepository membroRepository;
    private final PapelRepository papelRepository;

    public MembroUsecase(
            MembroRepository membroRepository,
            PapelRepository papelRepository
    ) {
        this.membroRepository = membroRepository;
        this.papelRepository = papelRepository;
    }

    @Transactional
    public void registrarMembro(RegistrarMembroRequest dto) {

        Set<String> nomesPapeis = Set.of(dto.papeis());

        List<Papel> papeisEncontrados = papelRepository.findAllByNomeIn(nomesPapeis);

        if (papeisEncontrados.size() != nomesPapeis.size()) {

            Set<String> nomesEncontrados = papeisEncontrados.stream()
                    .map(Papel::getNome)
                    .collect(Collectors.toSet());

            Set<String> papeisInvalidos = nomesPapeis.stream()
                    .filter(nome -> !nomesEncontrados.contains(nome))
                    .collect(Collectors.toSet());

            throw new NaoEncontradoException("Os seguintes papéis não estão cadastrados no sistema: " + papeisInvalidos);
        }

        var membro = Membro.criarMembro(dto.email(), dto.nome());

        membro.setDataNascimento(dto.dataNascimento());

        membro.atribuirPapeis(papeisEncontrados);

        if (dto.sobrenome() != null && !dto.sobrenome().isBlank()) {
            membro.setSobrenome(dto.sobrenome());
        }

        if (dto.endereco() != null && !dto.endereco().isBlank()) {
            membro.setEndereco(dto.endereco());
        }

        if (dto.telefone() != null && !dto.telefone().isBlank()) {
            membro.setTelefone(dto.telefone());
        }

        this.membroRepository.save(membro);
    }

    @Transactional(readOnly = true)
    public PageResponse<MembroResponse> listarMembros(OffsetPaginationRequest dto)
    {
        Pageable pageable = PageRequest.of(
                dto.page(),
                dto.size(),
                Sort.by("criadoEm").descending()
        );
        List<Membro> membros = membroRepository.listar(pageable);

        List<MembroResponse> membrosResponse = membros.stream()
                .map(MembroResponse::deEntidade)
                .toList();

        return new PageResponse<>(
                membrosResponse,
                dto.page(),
                dto.size()
        );
    }

    @Transactional
    public void atualizarPerfilParcialmente(UUID id, AtualizarMeParcialmenteRequest dto) {
        Membro membro = membroRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Membro não encontrado com o ID informado"));

        membro.atualizarDadosDoPerfil(
                dto.email(),
                dto.telefone(),
                dto.endereco(),
                dto.nomeDeUsuario()
        );
    }

    @Transactional
    public void atualizarMembro(UUID id, AtualizarMembroParcialmenteRequest dto) {
        Membro membro = membroRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Membro não encontrado com o ID informado"));

        membro.atualizarInformacoes(
                dto.nome(),
                dto.sobrenome(),
                dto.email(),
                dto.telefone(),
                dto.endereco(),
                dto.dataNascimento(),
                dto.papeis(),
                dto.status()
        );
    }

    @Transactional(readOnly = true)
    public MeResponse obterPerfilDoMembroAutenticado(UUID membroId) {
        Membro membro = membroRepository.findById(membroId)
                .orElseThrow(() -> new NaoEncontradoException("Membro autenticado não encontrado no banco de dados."));

        return MeResponse.deEntidade(membro);
    }
}
