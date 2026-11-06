package br.org.bandasantabarbara.application.services.setup;

import br.org.bandasantabarbara.application.dtos.setup.RegistrarAdminRequest;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.membro.Membro;
import br.org.bandasantabarbara.model.membro.MembroCredencial;
import br.org.bandasantabarbara.model.membro.MembroVinculo;
import br.org.bandasantabarbara.model.membro.Senha;
import br.org.bandasantabarbara.repositories.membro.FuncaoRepository;
import br.org.bandasantabarbara.repositories.membro.MembroCredencialRepository;
import br.org.bandasantabarbara.repositories.membro.MembroRepository;
import br.org.bandasantabarbara.repositories.membro.MembroVinculoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class SetupApplicationService {

    private final MembroRepository membroRepository;
    private final MembroCredencialRepository credencialRepository;
    private final MembroVinculoRepository vinculoRepository;
    private final FuncaoRepository funcaoRepository;
    private final PasswordEncoder passwordEncoder;

    public SetupApplicationService(
            MembroRepository membroRepository,
            MembroCredencialRepository credencialRepository,
            MembroVinculoRepository vinculoRepository,
            PasswordEncoder passwordEncoder,
            FuncaoRepository funcaoRepository
    ) {
        this.credencialRepository = credencialRepository;
        this.membroRepository = membroRepository;
        this.vinculoRepository = vinculoRepository;
        this.passwordEncoder = passwordEncoder;
        this.funcaoRepository = funcaoRepository;

    }


    @Transactional
    public void registrarAdmin(RegistrarAdminRequest dto) {

        var senha = Senha.criar(dto.senha());

        var hashPassword = passwordEncoder.encode(senha.valor());

        var membro = new Membro();
        membro.setNome(dto.nome());
        membro.setEmail(dto.email());
        membro.setSexo(dto.sexo());
        membro.setDataNascimento(dto.dataNascimento());

        var credencial = new MembroCredencial(membro);

        credencial.setNomeUsuario(dto.nomeUsuario());
        credencial.setHashSenha(hashPassword);

        var funcao = funcaoRepository.findByCode("SUPER_ADMIN").orElseThrow(
                () -> new NotFoundException("Função de administração não definida no sistema.")
        );

        var vinculo = new MembroVinculo(membro, funcao, LocalDate.now());


        membroRepository.save(membro);
        credencialRepository.save(credencial);
        vinculoRepository.save(vinculo);
    }


}
