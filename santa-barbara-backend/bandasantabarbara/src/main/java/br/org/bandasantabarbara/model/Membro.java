package br.org.bandasantabarbara.model;

import br.org.bandasantabarbara.exception.BadRequestException;
import br.org.bandasantabarbara.exception.EnumBadRequestException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.f4b6a3.uuid.alt.GUID;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;



//
// Entidade Membro
//
// Representa a identidade da pessoa
// Contém informações de contato
// Contém informações pessoais
//

@Entity
@Table(name = "membro")
public class Membro implements Persistable<UUID> {

    public enum MembroSexoEnum {
        MASCULINO,
        FEMININO,
        NAO_INFORMADO;

        @JsonCreator
        public static MembroSexoEnum fromString(String value) {
            if (value == null || value.isBlank()) {
                return null;
            }

            String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .trim()
                    .replaceAll("\\s+", "_")
                    .toUpperCase();

            for (MembroSexoEnum sexo : MembroSexoEnum.values()) {
                if (sexo.name().equalsIgnoreCase(normalized)) {
                    return sexo;
                }
            }

            List<String> values = new ArrayList<>();

            values.add("Masculino");
            values.add("Feminino");
            values.add("Nao informado");

            throw new EnumBadRequestException("Sexo informado é inválido:", values);
        }
    }

    @Id
    private UUID id;

    @Override
    public UUID getId() {
        return id;
    }

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

    @Getter
    @Column(name = "email", unique = true)
    private String email;

    @Getter
    @Column(name = "nome_legal", nullable = false)
    private String nome;

    @Getter
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Getter
    @Column(name = "endereco")
    private String endereco;

    @Getter
    @Column(name = "telefone")
    private String telefone;

    @Getter
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @Getter
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", nullable = false, columnDefinition = "tipo_sexo_pessoa")
    private MembroSexoEnum sexo;


    public void setNome(String nome) {

        if (nome == null || nome.isBlank() ) {
            throw new BadRequestException("Nome não pode estar vazio ou em branco.");
        }

        this.nome = nome.toLowerCase().trim();
        this.atualizadoEm = Instant.now();
    }

    public void setEndereco(String endereco) {

        if (endereco == null || endereco.isBlank()) {
            throw new BadRequestException("Endereço não pode estar vazio ou em branco.");
        }

        this.endereco = endereco.toLowerCase().trim();
        this.atualizadoEm = Instant.now();
    }

    public void setDataNascimento(LocalDate dataNascimento) {

        if (dataNascimento == null) {
            throw new BadRequestException("Data de nascimento não pode estar vazio.");
        }

        LocalDate hojeNoBrasil = LocalDate.now();

        if (dataNascimento.isAfter(hojeNoBrasil)) {
            throw new BadRequestException("O nascimento não pode acontecer no futuro.");
        }

        this.dataNascimento = dataNascimento;
    }

    public void setEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email não pode estar vazio ou em branco.");
        }

        this.email = email.toLowerCase().trim();
        this.atualizadoEm = Instant.now();
    }

    public void setTelefone(String telefone) {

        if (telefone == null || telefone.isBlank()) {
            throw new BadRequestException("Telefone não pode estar vazio ou em branco.");
        }

        String telefoneInterno = telefone.trim();

        if (telefoneInterno.length() < 10) {
            throw new BadRequestException("O DDD do telefone é obrigatório.");
        }

        Pattern padraoTelefone = Pattern.compile("^\\d{10,11}$");


        if (!padraoTelefone.matcher(telefoneInterno).matches()) {
            throw new BadRequestException("O número fornecido de caracteres para o telefone é inválido. Certifique-se de que ele esteja entre 10 a 11 dígitos com DDD incluso.");
        }


        this.telefone = telefoneInterno;
        this.atualizadoEm = Instant.now();
    }


    /*
     *
     * CONSTRUTORES
     *
     */

    public Membro() {
        id = GUID.v7().toUUID();
        isNew = true;
        criadoEm = Instant.now();
        atualizadoEm = Instant.now();
    }

    /*
    *
    * Regras de negócio
    *
     */

    public boolean ehMenorDeIdade() {
        return dataNascimento.plusYears(18).isAfter(LocalDate.now());
    }

}
