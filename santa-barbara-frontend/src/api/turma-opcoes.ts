import { api } from "#/lib/axios";
import type { Pagina } from "#/api/turmas";

// Listas de apoio usadas só pelos formulários e diálogos de turma.
// Se api/alunos.ts, api/instrumento.ts ou um hook de professores já fizerem
// a mesma consulta, dá para trocar e apagar este arquivo.

export interface PessoaOpcao {
    id: string;
    nome: string;
    email: string | null;
    telefone: string | null;
}

export interface InstrumentoOpcao {
    id: number;
    nome: string;
}

const TAMANHO_BUSCA = 20;

export async function buscarAlunos(search: string): Promise<PessoaOpcao[]> {
    const { data } = await api.get<Pagina<PessoaOpcao>>("/api/v1/alunos", {
        params: { search: search || undefined, page: 0, size: TAMANHO_BUSCA },
    });
    return data.content;
}

export async function buscarProfessores(search: string): Promise<PessoaOpcao[]> {
    const { data } = await api.get<Pagina<PessoaOpcao>>("/api/v1/professores", {
        params: { search: search || undefined, page: 0, size: TAMANHO_BUSCA },
    });
    return data.content;
}

export async function listarInstrumentosOpcoes(): Promise<InstrumentoOpcao[]> {
    const { data } = await api.get<InstrumentoOpcao[] | Pagina<InstrumentoOpcao>>(
        "/api/v1/instrumentos",
    );
    // O swagger não mostra o corpo de GET /instrumentos: aceita lista simples ou paginada.
    const lista = Array.isArray(data) ? data : data.content;
    return lista.map(({ id, nome }) => ({ id, nome }));
}
