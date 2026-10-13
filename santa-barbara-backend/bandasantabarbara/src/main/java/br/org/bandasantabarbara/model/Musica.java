package br.org.bandasantabarbara.model;

import br.org.bandasantabarbara.exception.BadRequestException;
import com.github.f4b6a3.uuid.alt.GUID;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "Musica")
public class Musica implements Persistable<UUID> {

    @Id
    @Getter
    private UUID id;

    @Transient
    private boolean isNew;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }

    /*
    *
    * Campos da Entidade
    *
     */


    @Column(name = "titulo")
    @Getter
    private String titulo;

    @Column(name = "compositor")
    @Getter
    private String compositor;

    @Column(name = "criado_em")
    @Getter
    private Instant criadoEm;

    @Column(name = "atualizado_em")
    @Getter
    private Instant atualizadoEm;

    /*
    *
    * Objetos Compostos e Relacionados
    *
     */

    @ManyToMany
    @JoinTable(
            name = "genero_musica",
            joinColumns = @JoinColumn(name = "id_musica"),
            inverseJoinColumns = @JoinColumn(name = "id_genero")
    )
    @Getter
    private Set<Genero> generos = new HashSet<>();


    /*
    *
    * Construtores
    *
     */


    protected Musica() {
        id = GUID.v7().toUUID();
        isNew = true;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public Musica(String titulo) {
        this();
        validarTitulo(titulo);
        this.titulo = normalizarTitulo(titulo);
    }

    /*
    *
    * Métodos de atribuição
    *
     */

    public void setTitulo(String titulo) {
        validarTitulo(titulo);
        this.titulo = normalizarTitulo(titulo);
    }

    public void setCompositor(String compositor) {
        validarCompositor(compositor);
        this.compositor = normalizarCompositor(compositor);
    }

    public void setGeneros(Collection<Genero> novosGeneros) {
        this.generos.clear();
        if (novosGeneros != null) {
            this.generos.addAll(novosGeneros);
        }
    }

    /*
    *
    * Métodos de validação
    *
     */

    private void validarTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new BadRequestException("Título não pode estar em branco.");
        }
    }

    private void validarCompositor(String compositor) {
        if (compositor == null || compositor.isBlank()) {
            throw new BadRequestException("Compositor não pode estar em branco.");
        }
    }

    /*
    *
    * Métodos de normalização
    *
     */

    private String normalizarTitulo(String titulo) {
        return titulo.toLowerCase().strip().replaceAll("\\s+", " ");
    }

    private String normalizarCompositor(String compositor) {
        return compositor.toLowerCase().strip().replaceAll("\\s+", " ");
    }
}
