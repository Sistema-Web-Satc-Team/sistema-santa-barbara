package br.org.bandasantabarbara.model;

import br.org.bandasantabarbara.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.Getter;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Entity
@Table(name = "genero")
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;

    @Column(name = "slug", nullable = false, unique = true)
    @Getter
    private String slug;

    @Column(name = "nome", nullable = false, unique = true)
    @Getter
    private String nome;

    /*
    *
    * Construtor
    *
     */

    protected  Genero() { }

    public Genero(String nome) {
        this();

        if (nome == null || nome.isBlank()) {
            throw new BadRequestException("Nome do gênero não pode estar vazio.");
        }

        this.nome = nome
                .toLowerCase()
                .replaceAll("\\s+", " ")
                .strip();



        String nomeComAcentosDecompostos = Normalizer.normalize(this.nome, Normalizer.Form.NFD);

        String nomeSemAcentos = DIACRITICS.matcher(nomeComAcentosDecompostos).replaceAll("");

        this.slug = nomeSemAcentos.replace(" ", "-");

    }

    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
}
