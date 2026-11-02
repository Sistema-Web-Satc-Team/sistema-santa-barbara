import { keepPreviousData, useQuery, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { listarTurmas, type StatusTurma } from "#/api/turmas";

export interface TurmasFiltros {
    page: number;
    size: number;
    status?: StatusTurma;
}

// Mesmo formato de use-members: { state, actions } com setFilter e refresh.
export function useTurmas() {
    const queryClient = useQueryClient();
    const [filtros, setFiltros] = useState<TurmasFiltros>({ page: 0, size: 10 });

    const consulta = useQuery({
        queryKey: ["turmas", "lista", filtros],
        queryFn: () => listarTurmas(filtros),
        placeholderData: keepPreviousData,
    });

    function setFilter<K extends keyof TurmasFiltros>(chave: K, valor: TurmasFiltros[K]) {
        // Mudar o tamanho da página ou o filtro volta para a primeira página.
        setFiltros((atual) => ({
            ...atual,
            [chave]: valor,
            ...(chave === "page" ? {} : { page: 0 }),
        }));
    }

    return {
        state: {
            turmas: consulta.data,
            isLoading: consulta.isLoading,
            isFetching: consulta.isFetching,
            isEmpty: !consulta.isLoading && (consulta.data?.content.length ?? 0) === 0,
            error: consulta.error,
            filters: filtros,
        },
        actions: {
            setFilter,
            refresh: () => queryClient.invalidateQueries({ queryKey: ["turmas"] }),
        },
    };
}
