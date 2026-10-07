package br.org.bandasantabarbara.application.usecase.init;

import br.org.bandasantabarbara.application.dtos.setup.RegistrarAdminRequest;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroCredencial;
import br.org.bandasantabarbara.model.MembroVinculo;
import br.org.bandasantabarbara.model.Senha;
import br.org.bandasantabarbara.repositories.FuncaoRepository;
import br.org.bandasantabarbara.repositories.MembroCredencialRepository;
import br.org.bandasantabarbara.repositories.MembroRepository;
import br.org.bandasantabarbara.repositories.MembroVinculoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SetupApplicationUsecase {

    private final MembroRepository membroRepository;
    private final MembroCredencialRepository credencialRepository;
    private final MembroVinculoRepository vinculoRepository;
    private final FuncaoRepository funcaoRepository;
    private final PasswordEncoder passwordEncoder;

    public SetupApplicationUsecase(
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

        var vinculo = new MembroVinculo(membro, funcao);


        membroRepository.save(membro);
        credencialRepository.save(credencial);
        vinculoRepository.save(vinculo);
    }


}
