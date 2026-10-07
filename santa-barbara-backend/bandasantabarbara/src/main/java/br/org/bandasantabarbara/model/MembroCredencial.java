package br.org.bandasantabarbara.model;

import br.org.bandasantabarbara.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;


//
// Entidade Membro Credencial
//
// Representa a conta e autenticação do membro
// Aqui os dados resposáveis pelo acesso são armazenados
//

@Entity
@Table(name = "membro_credencial")
public class MembroCredencial {


    public enum MembroCredencialStatus {
        ATIVO,
        INATIVO,
        BLOQUEADO
    }


    @Id
    @Column(name = "id_membro")
    private UUID idMembro;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_membro")
    @Getter
    private Membro membro;

    @Getter
    @Column(name = "hash_password", nullable = false)
    private String hashSenha;

    @Getter
    @Column(name = "nome_usuario")
    private String nomeUsuario;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "status_membro_credencial")
    private MembroCredencialStatus status;


    @Getter
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Getter
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;


    public void setNomeUsuario(String nomeUsuario) {
        if (nomeUsuario == null || nomeUsuario.isBlank()) {
            throw new BadRequestException("Nome de usuário não pode estar em branco.");
        }

        this.nomeUsuario = nomeUsuario.trim();
        this.atualizadoEm = Instant.now();
    }

    public void setHashSenha(String hashSenha) {
        this.hashSenha = hashSenha;
        this.atualizadoEm = Instant.now();
    }

    public MembroCredencial(Membro membro) {
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
        this.status = MembroCredencialStatus.ATIVO;

        this.membro = membro;
    }


    public boolean isBloqueado() {
        return this.status == MembroCredencialStatus.BLOQUEADO;
    }

    public boolean isAtivo() {
        return this.status == MembroCredencialStatus.ATIVO;
    }

}
