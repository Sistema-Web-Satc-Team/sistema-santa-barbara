package br.org.bandasantabarbara.model;

import com.github.f4b6a3.uuid.alt.GUID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "membro")
@SecondaryTable(
        name = "membro_credencial",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "membro_id")
)
public class Membro {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Getter
    private UUID id;

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

    public Membro(String email, String nomeDeUsuario) {
        this();
        this.email = email;
        this.nomeDeUsuario = nomeDeUsuario;
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


    public static Membro criarAdministrador(
            String email,
            String username,
            String hashSenha
    ) {
        var membro = new Membro(email, username);

        membro.atribuirCredencial(hashSenha);
        membro.atribuirPapel(Papel.superAdmin());

        return membro;
    }

}
