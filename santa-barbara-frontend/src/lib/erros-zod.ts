import type { ZodError } from "zod";

export type ErrosPorCampo = Record<string, string>;

// Primeira mensagem de cada campo, indexada pelo caminho ("nome", "numeroVagas"...).
export function errosPorCampo(erro: ZodError): ErrosPorCampo {
    const resultado: ErrosPorCampo = {};
    for (const problema of erro.issues) {
        const chave = problema.path.map(String).join(".");
        if (!(chave in resultado)) resultado[chave] = problema.message;
    }
    return resultado;
}
