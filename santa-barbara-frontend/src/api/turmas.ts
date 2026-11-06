import { api } from "#/lib/axios";
import type { Sala } from "#/api/salas";

/* ---------- Enumerações do contrato ---------- */

// O swagger só mostra MATUTINO e NOTURNO nos exemplos. VESPERTINO é a suposição
// para "tarde": confirmar o valor exato com o backend.
export const TURNOS = ["MATUTINO", "VESPERTINO", "NOTURNO"] as const;
export type Turno = (typeof TURNOS)[number];

export const STATUS_TURMA = [
    "PLANEJAMENTO",
    "EM_ANDAMENTO",
    "ENCERRADO",
    "CANCELADO",
    "INTERROMPIDA",
] as const;
export type StatusTurma = (typeof STATUS_TURMA)[number];

// As respostas trazem os horários agrupados por dia, com a chave em minúsculas.
export const CHAVES_DIA = [
    "segunda",
    "terca",
    "quarta",
    "quinta",
    "sexta",
    "sabado",
    "domingo",
] as const;
export type ChaveDia = (typeof CHAVES_DIA)[number];

// As requisições (PATCH) usam o dia em maiúsculas, em português.
export type DiaDaSemana =
    | "MONDAY"
    | "TUESDAY"
    | "WEDNESDAY"
    | "THURSDAY"
    | "FRIDAY"
    | "SATURDAY"
    | "SUNDAY";

/* ---------- Respostas ---------- */

export interface Pagina<T> {
    content: T[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
}

export interface HorarioTurma {
    horaInicio: string; // "19:00" ou "19:00:00"
    horaFim: string;
}

export type HorariosPorDia = Partial<Record<ChaveDia, HorarioTurma[]>>;

export interface AlunoDaTurma {
    idMatricula: number;
    idAluno: string;
    nome: string;
    telefone: string | null;
    email: string | null;
    ehMenor: boolean;
}

export interface ProfessorDaTurma {
    idProfessorTurma: number;
    idProfessor: string;
    nome: string;
    telefone: string | null;
    email: string | null;
}

export interface TurmaResumo {
    id: number;
    nome: string;
    numeroDeAlunos: number; // só ativos
    numeroDeProfessores: number; // só ativos
    numeroVagas: number;
    dataInicio: string | null;
    dataFim: string | null; // fim estimado
    dataTermino: string | null; // término real (cancelamento, interrupção ou encerramento)
    turno: Turno;
    status: StatusTurma;
    sala: Sala;
    instrumentos: string[]; // nomes, não ids
    horarios?: HorariosPorDia;
}

export interface Turma extends TurmaResumo {
    horarios: HorariosPorDia;
    alunos: AlunoDaTurma[]; // só ativos
    professores: ProfessorDaTurma[]; // só ativos
}

/* ---------- Requisições ---------- */

export interface HorarioRequest {
    diaDaSemana: DiaDaSemana;
    inicio: string; // "19:00"
    fim: string;
}

export interface CriarTurmaRequest {
    codigoSala: string;
    numeroVagas: number;
    nome: string;
    turno: Turno;
    idsInstrumentos: number[];
}

// PATCH: só o que mudou. dataInicio/dataFim aceitam null para limpar.
// "status" nunca é enviado: o backend ignora e o valor é calculado.
export interface AtualizarTurmaRequest {
    codigoSala?: string;
    numeroVagas?: number;
    nome?: string;
    turno?: Turno;
    idsInstrumentos?: number[];
    dataInicio?: string | null;
    dataFim?: string | null;
    horarios?: HorarioRequest[]; // usado pela Grade de horários
}

export interface ListarTurmasParams {
    page?: number;
    size?: number;
    status?: StatusTurma;
}

/* ---------- Chamadas ---------- */

const BASE = "/api/v1/turmas";

export async function listarTurmas(
    params: ListarTurmasParams = {},
): Promise<Pagina<TurmaResumo>> {
    const { data } = await api.get<Pagina<TurmaResumo>>(BASE, { params });
    return data;
}

export async function buscarTurma(idTurma: number): Promise<Turma> {
    const { data } = await api.get<Turma>(`${BASE}/${idTurma}`);
    return data;
}

export async function criarTurma(body: CriarTurmaRequest): Promise<Turma> {
    const { data } = await api.post<Turma>(BASE, body);
    return data;
}

export async function atualizarTurma(
    idTurma: number,
    body: AtualizarTurmaRequest,
): Promise<Turma> {
    const { data } = await api.patch<Turma>(`${BASE}/${idTurma}`, body);
    return data;
}

// Só turmas em PLANEJAMENTO.
export async function cancelarTurma(idTurma: number): Promise<void> {
    await api.post(`${BASE}/${idTurma}/cancelar`);
}

// Só turmas EM_ANDAMENTO. Não é reversível.
export async function interromperTurma(idTurma: number): Promise<void> {
    await api.post(`${BASE}/${idTurma}/interromper`);
}

export async function vincularProfessor(
    idTurma: number,
    idProfessor: string,
): Promise<void> {
    await api.post(`${BASE}/${idTurma}/professores/${idProfessor}`);
}

// Só em PLANEJAMENTO.
export async function removerProfessor(
    idTurma: number,
    idProfessor: string,
): Promise<void> {
    await api.delete(`${BASE}/${idTurma}/professores/${idProfessor}`);
}

// Só EM_ANDAMENTO.
export async function substituirProfessor(
    idTurma: number,
    idNovoProfessor: string,
    justificativa?: string,
): Promise<void> {
    await api.post(`${BASE}/${idTurma}/professores/substituir`, { idNovoProfessor, justificativa });
}
