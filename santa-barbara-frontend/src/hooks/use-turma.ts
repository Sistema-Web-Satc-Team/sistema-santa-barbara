import { useQuery } from "@tanstack/react-query";
import { buscarTurma } from "#/api/turmas";

export function useTurma(idTurma: number | null) {
    return useQuery({
        queryKey: ["turma", idTurma],
        queryFn: () => buscarTurma(idTurma as number),
        enabled: idTurma !== null,
    });
}
