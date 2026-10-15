package br.org.bandasantabarbara.application.dtos.alunos;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import br.org.bandasantabarbara.application.dtos.alunos.AlunoResponsavelResponse;
import br.org.bandasantabarbara.application.dtos.alunos.AlunoResponse;
import br.org.bandasantabarbara.application.dtos.alunos.InstrumentoAlunoResponse;
import br.org.bandasantabarbara.model.Membro;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Dados falsos para o front não ficar travado enquanto a rota real não existe.
 * Remover quando o endpoint de alunos estiver implementado.
 *
 * Os UUIDs são aleatórios, mas gerados uma única vez quando a classe carrega.
 * Dentro da mesma execução do backend o id de cada aluno não muda entre requisições.
 */
public final class AlunoMock {

    private AlunoMock() {
    }

    private static final List<AlunoResponse> ALUNOS = List.of(
            aluno("João Pedro Silva", "joao@gmail.com", "48000000001", Membro.MembroSexoEnum.MASCULINO, 14, true,
                    List.of("Guitarra", "Violão"),
                    responsavel("Maria Silva", "maria@gmail.com", "48000000002", "mae")),

            aluno("Ana Clara Souza", null, "48911112222", Membro.MembroSexoEnum.FEMININO, 11, true,
                    List.of("Flauta"),
                    responsavel("Carlos Souza", "carlos.souza@gmail.com", "48911113333", "pai"),
                    responsavel("Marta Souza", "marta.souza@gmail.com", "48911114444", "avo")),

            aluno("Lucas Gabriel Pereira", "lucas.pereira@gmail.com", "48922223333", Membro.MembroSexoEnum.MASCULINO, 17, false,
                    List.of(),
                    responsavel("Renata Pereira", "renata.pereira@gmail.com", "48922224444", "mae")),

            aluno("Beatriz Tavares Lemos", "beatriz.tavares@gmail.com", "48933334444", Membro.MembroSexoEnum.FEMININO, 24, true,
                    List.of("Clarinete")),

            aluno("Miguel Furlan Batista", null, "48944445555", Membro.MembroSexoEnum.MASCULINO, 9, true,
                    List.of("Percussão"),
                    responsavel("Joana Furlan Batista", "joana.furlan@outlook.com", "48944446666", "mae"),
                    responsavel("Tiago Batista", null, "48944447777", "tio")),

            aluno("Sofia Menezes Duarte", "sofia.duarte@gmail.com", "48955556666", Membro.MembroSexoEnum.FEMININO, 16, true,
                    List.of("Saxofone", "Flauta"),
                    responsavel("Rafael Menezes Duarte", "rafael.menezes@gmail.com", "48955557777", "pai")),

            aluno("Rafael Cardoso Lima", "rafael.cardoso@gmail.com", "48966667777", Membro.MembroSexoEnum.MASCULINO, 31, false,
                    List.of()),

            aluno("Helena Albuquerque", null, "48977778888", Membro.MembroSexoEnum.FEMININO, 13, true,
                    List.of("Trompete"),
                    responsavel("Marina Albuquerque", "marina.albuquerque@gmail.com", "48977779999", "mae")),

            aluno("Gustavo Ramos Cardoso", "gustavo.ramos@gmail.com", "48988889999", Membro.MembroSexoEnum.MASCULINO, 19, true,
                    List.of("Bateria", "Violão")),

            aluno("Laura Fernandes Pires", null, "48999990000", Membro.MembroSexoEnum.FEMININO, 8, false,
                    List.of(),
                    responsavel("Carlos Eduardo Pires", "cadu.pires@gmail.com", "48999991111", "pai"))
    );

    public static List<AlunoResponse> todos() {
        return ALUNOS;
    }

    /**
     * Simula GET /alunos com os filtros da API. Todos são opcionais e se combinam com E.
     * <ul>
     *   <li>search: trecho do nome ou do e-mail, sem diferenciar maiúsculas nem acentos</li>
     *   <li>instrumentosAprendendo: o aluno aprende ao menos um dos instrumentos informados</li>
     *   <li>apenasMenores: somente idade abaixo de 18</li>
     * </ul>
     * Ordena por nome (A-Z). O page começa em 0, como no Spring Data, e os totais refletem o resultado filtrado.
     */
    public static PageResponse<AlunoResponse> pagina(int page, int size, String search,
                                                     List<String> instrumentosAprendendo, boolean apenasMenores) {
        String termo = normalizar(search);
        Set<String> instrumentos = instrumentosAprendendo == null ? Set.of() : instrumentosAprendendo.stream()
                .map(AlunoMock::normalizar)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        List<AlunoResponse> filtrados = ALUNOS.stream()
                .filter(a -> termo.isEmpty() || normalizar(a.nome()).contains(termo) || normalizar(a.email()).contains(termo))
                .filter(a -> instrumentos.isEmpty() || a.instrumentos().aprendendo().stream()
                        .map(AlunoMock::normalizar).anyMatch(instrumentos::contains))
                .filter(a -> !apenasMenores || a.idade() < 18)
                .sorted(Comparator.comparing((AlunoResponse a) -> normalizar(a.nome())))
                .toList();

        int safePage = Math.max(0, page);
        int safeSize = Math.max(1, size);
        int total = filtrados.size();
        int from = (int) Math.min((long) safePage * safeSize, total);
        int to = Math.min(from + safeSize, total);
        int totalPages = (int) Math.ceil(total / (double) safeSize);
        return new PageResponse<>(filtrados.subList(from, to), safePage, safeSize, total, totalPages);
    }

    /* ───────────── helpers ───────────── */

    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }

    private static AlunoResponse aluno(String nome, String email, String telefone, Membro.MembroSexoEnum sexo,
                                       int idade, boolean temTurma, List<String> aprendendo,
                                       AlunoResponsavelResponse... responsaveis) {
        return new AlunoResponse(
                UUID.randomUUID(), nome, email, telefone, sexo, idade, temTurma,
                new InstrumentoAlunoResponse(aprendendo),
                List.of(responsaveis)
        );
    }

    private static AlunoResponsavelResponse responsavel(String nome, String email, String telefone, String relacao) {
        return new AlunoResponsavelResponse(UUID.randomUUID(), nome, email, telefone, relacao);
    }
}
