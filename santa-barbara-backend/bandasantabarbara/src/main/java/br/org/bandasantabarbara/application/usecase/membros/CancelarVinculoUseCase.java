package br.org.bandasantabarbara.application.usecase.membros;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.ForbiddenException;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.repositories.MembroVinculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CancelarVinculoUseCase {

    private final MembroVinculoRepository membroVinculoRepository;

    public CancelarVinculoUseCase(MembroVinculoRepository membroVinculoRepository) {
        this.membroVinculoRepository = membroVinculoRepository;
    }

    @Transactional
    public void cancelar(UUID membroId, int vinculoId, String papelAtor) {
        var vinculo = this.membroVinculoRepository.findByIdAndMembroId(vinculoId, membroId).orElseThrow(() -> new NotFoundException("Vínculo ou membro inexistente."));

        String codigoFuncaoAlvo = vinculo.getFuncao().getCode();

        // DEBITO-02
        if (papelAtor.equals("SECRETARIO")) {
            if (codigoFuncaoAlvo.equals("SUPER_ADMIN") || codigoFuncaoAlvo.equals("ADMIN")) {
                throw new ForbiddenException("Secretários não têm permissão para remover vínculos de administradores.");
            }
        }

        if (papelAtor.equals("ADMIN")) {
            if (codigoFuncaoAlvo.equals("SUPER_ADMIN")) {
                throw new ForbiddenException("Administradores não podem remover vínculos de Super Administradores.");
            }
        }

        if (codigoFuncaoAlvo.equals("SUPER_ADMIN")) {
            long superAdminsAtivos = membroVinculoRepository.countByFuncaoCodeAndAtivoTrue("SUPER_ADMIN");

            if (superAdminsAtivos <= 1) {
                throw new BadRequestException("Não é permitido remover o último Super Admin ativo do sistema.");
            }
        }

        vinculo.cancelar();
    }
}
