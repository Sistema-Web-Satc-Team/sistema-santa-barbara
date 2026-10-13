package br.org.bandasantabarbara.model;

import br.org.bandasantabarbara.exception.BadRequestException;
import com.github.f4b6a3.uuid.alt.GUID;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.domain.Persistable;

import javax.sound.midi.Instrument;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "partitura")
public class Partitura implements Persistable<UUID> {

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

    @Column(name = "arranjador")
    @Getter
    private String arranjador;

    @Column(name = "criado_em")
    @Getter
    private Instant criadoEm;

    @Column(name = "atualizado_em")
    @Getter
    private Instant atualizadoEm;

    /*
    *
    * Relações
    *
     */

    @ManyToOne
    @JoinColumn(name = "id_musica")
    @Getter
    private Musica musica;

    @OneToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "partitura_file_id")
    @Getter
    private StoredFile file;

    @ManyToMany
    @JoinTable(
            name = "partitura_instrumento",
            joinColumns = @JoinColumn(name = "id_partitura"),
            inverseJoinColumns = @JoinColumn(name = "id_instrumento")
    )
    @Getter
    private Set<Instrumento> instrumentos = new java.util.HashSet<>();

    /*
    *
    * Construtores
    *
     */

    protected Partitura() {
        id = GUID.v7().toUUID();
        isNew = true;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }

    public Partitura(String titulo, Musica musica) {
        this();
        this.setTitulo(titulo);
        this.setMusica(musica);
        this.setStoredFile(file);
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

    public void setArranjador(String arranjador) {
        validarArranjador(arranjador);
        this.arranjador = normalizarArranjador(arranjador);
    }

    public void setMusica(Musica musica) {
        if (musica == null) {
            throw new BadRequestException("Uma partitura pertence a uma música.");
        }
        this.musica = musica;
    }

    public void setStoredFile(StoredFile file) {
        this.file = file;
    }

    public void setInstrumentos(Collection<Instrumento> novosInstrumentos) {
        this.instrumentos.clear();
        if (novosInstrumentos != null) {
            this.instrumentos.addAll(novosInstrumentos);
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

    private void validarArranjador(String arranjador) {
        if (arranjador == null || arranjador.isBlank()) {
            throw new BadRequestException("Arranjador não pode estar em branco.");
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

    private String normalizarArranjador(String arranjador) {
        return arranjador.toLowerCase().strip().replaceAll("\\s+", " ");
    }

}
