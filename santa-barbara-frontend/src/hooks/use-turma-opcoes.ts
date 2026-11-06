import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { listarSalas } from "#/api/salas";
import { listarTurmas } from "#/api/turmas";
import { buscarAlunos, buscarProfessores, listarInstrumentosOpcoes } from "#/api/turma-opcoes";

const CINCO_MINUTOS = 5 * 60 * 1000;

export function useSalas() {
    return useQuery({
        queryKey: ["salas"],
        queryFn: () => listarSalas(),
        staleTime: CINCO_MINUTOS,
    });
}

export function useInstrumentos() {
    return useQuery({
        queryKey: ["instrumentos-opcoes"],
        queryFn: listarInstrumentosOpcoes,
        staleTime: CINCO_MINUTOS,
    });
}

// Busca no servidor: a lista de alunos e professores pode ser grande.
export function useCandidatos(tipo: "aluno" | "professor", termo: string, habilitado = true) {
    return useQuery({
        queryKey: ["turma-candidatos", tipo, termo],
        queryFn: () => (tipo === "aluno" ? buscarAlunos(termo) : buscarProfessores(termo)),
        enabled: habilitado,
        staleTime: 30 * 1000,
        placeholderData: keepPreviousData,
    });
}

// Destinos possíveis de uma transferência: só turmas em andamento (documento de regras).
export function useTurmasEmAndamento(habilitado = true) {
    return useQuery({
        queryKey: ["turmas", "em-andamento-opcoes"],
        queryFn: () => listarTurmas({ status: "EM_ANDAMENTO", page: 0, size: 100 }),
        enabled: habilitado,
    });
}
