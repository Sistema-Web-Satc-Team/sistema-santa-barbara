import { api } from "#/lib/axios";

export type StatusMatricula = "ATIVO" | "TRANSFERIDO" | "ENCERRADO" | "DESISTIDO";

export interface MatriculaResponse {
    idNovaMatricula?: number;
    idMatricula?: number;
    idAluno: string;
    idTurma: number;
    status: StatusMatricula | string;
    justificativa?: string | null;
}

export interface MatricularRequest {
    idAluno: string;
    idTurma: number;
}

export interface TransferirMatriculaRequest {
    idNovaTurma: number;
    justificativa: string;
}

const BASE = "/api/v1/matriculas";

// O backend valida lotação (RN-07), conflito de horário (RN-05) e matrícula
// duplicada (RN-03). O professor da turma também não pode ser aluno dela.
export async function matricularAluno(body: MatricularRequest): Promise<MatriculaResponse> {
    const { data } = await api.post<MatriculaResponse>(BASE, body);
    return data;
}

// Só em PLANEJAMENTO.
export async function removerMatricula(idMatricula: number): Promise<void> {
    await api.delete(`${BASE}/${idMatricula}`);
}

// Só EM_ANDAMENTO, para outra turma também EM_ANDAMENTO.
export async function transferirMatricula(
    idMatricula: number,
    body: TransferirMatriculaRequest,
): Promise<MatriculaResponse> {
    const { data } = await api.post<MatriculaResponse>(`${BASE}/${idMatricula}/transferir`, body);
    return data;
}

// Só EM_ANDAMENTO.
export async function desistirDaMatricula(
    idMatricula: number,
    justificativa: string,
): Promise<MatriculaResponse> {
    const { data } = await api.post<MatriculaResponse>(`${BASE}/${idMatricula}/desistir`, {
        justificativa,
    });
    return data;
}
