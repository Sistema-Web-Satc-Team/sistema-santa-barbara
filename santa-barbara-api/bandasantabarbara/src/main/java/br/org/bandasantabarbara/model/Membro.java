package br.org.bandasantabarbara.model;

import com.github.f4b6a3.uuid.alt.GUID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "membro")
@SecondaryTable(
        name = "membro_credencial",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "membro_id")
)
public class Membro implements Persistable<UUID> {
    @Id
    private UUID id;

    @Transient
    private boolean isNovo = true;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return isNovo;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNovo = false;
    }

    @Getter @Setter
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Getter @Setter
    @Column(name = "nome_legal")
    private String nome;

    @Getter @Setter
    @Column(name = "sobrenome_legal")
    private String sobrenome;

    @Getter @Setter
    @Column(name = "nome_usuario", unique = true)
    private String nomeDeUsuario;

    @Getter @Setter
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Getter @Setter
    @Column(name = "endereco")
    private String endereco;

    @Getter @Setter
    @Column(name = "telefone")
    private String telefone;

    @Getter
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Getter
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    /*
    *
    * Filhos
    *
     */

    @Getter
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "hashSenha", column = @Column(table = "membro_credencial", name = "hash_password")),
            @AttributeOverride(name = "criadoEm", column = @Column(table = "membro_credencial", name = "criado_em")),
            @AttributeOverride(name = "atualizadoEm", column = @Column(table = "membro_credencial", name = "atualizado_em"))
    })
    private MembroCredencial credencial;

    @Getter
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "membro_papel",
            joinColumns = @JoinColumn(name = "id_membro"),
            inverseJoinColumns = @JoinColumn(name = "id_papel")
    )
    private Set<Papel> papeis = new HashSet<>();

    /*
    *
    * CONSTRUTORES
    *
     */

    public static Membro criarMembro(String email, String nome) {
        var membro = new Membro();
        membro.setEmail(email);
        membro.setNome(nome);

        return membro;
    }

    protected Membro() {
        this.id = GUID.v7().toUUID();
        criadoEm = Instant.now();
        atualizadoEm = Instant.now();
    }

    /*
    *
    * Métodos de composição
    *
     */

    public void atribuirCredencial(String hashSenha) {
        if (this.credencial == null) {
            this.credencial = new MembroCredencial(hashSenha);
        } else {
            this.credencial.alterarHashSenha(hashSenha);
        }
        this.atualizadoEm = Instant.now();
    }

    public void atribuirPapel(Papel novoPapel) {
        this.papeis.add(novoPapel);
        this.atualizadoEm = Instant.now();
    }

    public void atribuirPapeis(List<Papel> novosPapeis) {
        this.papeis.addAll(novosPapeis);
        this.atualizadoEm = Instant.now();
    }


    public static Membro criarAdministrador(
            String email,
            String username,
            String hashSenha
    ) {
        var membro = new Membro();

        membro.setEmail(email);
        membro.setNomeDeUsuario(username);
        membro.atribuirCredencial(hashSenha);
        membro.atribuirPapel(Papel.superAdmin());

        return membro;
    }

}
