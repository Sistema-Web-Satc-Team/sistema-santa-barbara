import axios from "axios";

interface CorpoErroApi {
    message?: unknown;
    detail?: unknown;
    title?: unknown;
}

function primeiroTexto(...candidatos: unknown[]): string | null {
    for (const c of candidatos) {
        if (typeof c === "string" && c.trim()) return c.trim();
    }
    return null;
}

/**
 * Extrai a mensagem de erro devolvida pela API (regras de negócio como conflito de
 * horário, turma lotada ou professor na própria turma voltam como erro com "message").
 * Se já existir algo equivalente em lib/api-error.ts, dá para trocar o import.
 */
export function mensagemDeErro(err: unknown, padrao: string): string {
    if (!axios.isAxiosError(err)) return padrao;

    if (!err.response) {
        return "Sem resposta do servidor. Verifique a conexão e tente novamente.";
    }

    const corpo = err.response.data as CorpoErroApi | string | null | undefined;
    if (typeof corpo === "string") return primeiroTexto(corpo) ?? padrao;
    if (corpo && typeof corpo === "object") {
        return primeiroTexto(corpo.message, corpo.detail, corpo.title) ?? padrao;
    }
    return padrao;
}
