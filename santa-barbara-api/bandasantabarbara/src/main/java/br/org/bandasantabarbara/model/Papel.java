package br.org.bandasantabarbara.model;

import br.org.bandasantabarbara.exception.DomainException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.Normalizer;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "papel")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Papel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Integer id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "papel_permissao",
            joinColumns = @JoinColumn(name = "id_papel"),
            inverseJoinColumns = @JoinColumn(name = "id_permissao")
    )
    @Getter
    private Set<Permissao> permissoes = new HashSet<>();

    @Getter
    @Setter
    @Column(name = "nome", nullable = false, unique = true)
    private String nome;

    @Getter
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Getter
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;


    protected Papel(String nome) {

        if (nome == null || nome.trim().isBlank()) {
            throw new DomainException("O nome do papel não pode ser nulo ou vazio.");
        }

        String formatado = Normalizer.normalize(nome, java.text.Normalizer.Form.NFD)
                .trim()
                .toUpperCase()
                .replaceAll("\\p{M}", "")
                .replaceAll("[\\s\\-/]+", "_")
                .replaceAll("[^A-Z0-9_]", "")
                .replaceAll("^_+|_+$", "")
                .replaceAll("_+", "_");



        criadoEm = Instant.now();
        atualizadoEm = Instant.now();
        this.nome = formatado;
    }

    public static Papel superAdmin() {
        return new Papel("SUPER_ADMIN");
    }
}
