import {
    CHAVES_DIA,
    type AtualizarTurmaRequest,
    type ChaveDia,
    type CriarTurmaRequest,
    type DiaDaSemana,
    type HorarioTurma,
    type HorariosPorDia,
    type StatusTurma,
    type Turma,
    type TurmaResumo,
    type Turno,
} from "#/api/turmas";
import type { InstrumentoOpcao } from "#/api/turma-opcoes";
import type { TurmaFormValues } from "#/schemas/form-schema-turma";

/* ---------- Rótulos ---------- */

export const STATUS_LABEL: Record<StatusTurma, string> = {
    PLANEJAMENTO: "Em planejamento",
    EM_ANDAMENTO: "Em andamento",
    ENCERRADO: "Encerrada",
    CANCELADO: "Cancelada",
    INTERROMPIDA: "Interrompida",
};

export const TURNO_LABEL: Record<Turno, string> = {
    MATUTINO: "Manhã",
    VESPERTINO: "Tarde",
    NOTURNO: "Noite",
};

export interface DiaDaSemanaInfo {
    chave: ChaveDia;
    valor: DiaDaSemana;
    rotulo: string;
}

export const DIAS_DA_SEMANA: DiaDaSemanaInfo[] = [
    { chave: "segunda", valor: "MONDAY", rotulo: "Segunda-feira" },
    { chave: "terca", valor: "TUESDAY", rotulo: "Terça-feira" },
    { chave: "quarta", valor: "WEDNESDAY", rotulo: "Quarta-feira" },
    { chave: "quinta", valor: "THURSDAY", rotulo: "Quinta-feira" },
    { chave: "sexta", valor: "FRIDAY", rotulo: "Sexta-feira" },
    { chave: "sabado", valor: "SATURDAY", rotulo: "Sábado" },
    { chave: "domingo", valor: "SUNDAY", rotulo: "Domingo" },
];

/* ---------- Regras temporárias ---------- */

// RN-TEMP-01: por enquanto uma turma comporta um único professor.
export const MAX_PROFESSORES_POR_TURMA = 1;

/* ---------- Formatação ---------- */

// As datas da API são "yyyy-MM-dd". Não passa por Date de propósito: new Date("2026-11-01")
// é interpretado em UTC e mostraria o dia anterior no Brasil.
export function formatarData(iso: string | null | undefined): string {
    if (!iso) return "—";
    const [ano, mes, dia] = iso.slice(0, 10).split("-");
    if (!ano || !mes || !dia) return "—";
    return `${dia}/${mes}/${ano}`;
}

export function formatarHora(hora: string): string {
    return hora.slice(0, 5);
}

export function iniciais(nome: string): string {
    return nome
        .split(" ")
        .filter(Boolean)
        .slice(0, 2)
        .map((parte) => parte[0])
        .join("")
        .toUpperCase();
}

export function horariosOrdenados(
    horarios: HorariosPorDia | undefined,
): { dia: DiaDaSemanaInfo; faixas: HorarioTurma[] }[] {
    return DIAS_DA_SEMANA.map((dia) => ({
        dia,
        faixas: [...(horarios?.[dia.chave] ?? [])].sort((a, b) =>
            a.horaInicio.localeCompare(b.horaInicio),
        ),
    })).filter((item) => item.faixas.length > 0);
}

export function temHorarios(horarios: HorariosPorDia | undefined): boolean {
    return CHAVES_DIA.some((chave) => (horarios?.[chave]?.length ?? 0) > 0);
}

/* ---------- O que cada status permite (documento de regras, "Resumo das ações") ---------- */

type BaseAcoes = Pick<
    TurmaResumo,
    "status" | "numeroDeAlunos" | "numeroVagas" | "numeroDeProfessores"
>;

export interface AcoesTurma {
    editar: boolean;
    cancelar: boolean; // só em planejamento
    interromper: boolean; // só em andamento
    matricular: boolean; // RN-07: não com a turma lotada
    removerAluno: boolean; // só em planejamento
    transferirAluno: boolean; // só em andamento
    desistirAluno: boolean; // só em andamento
    vincularProfessor: boolean; // RN-TEMP-01
    removerProfessor: boolean; // só em planejamento
    substituirProfessor: boolean; // só em andamento
}

export function turmaLotada(turma: Pick<TurmaResumo, "numeroDeAlunos" | "numeroVagas">): boolean {
    return turma.numeroDeAlunos >= turma.numeroVagas;
}

export function acoesPermitidas(turma: BaseAcoes): AcoesTurma {
    const planejamento = turma.status === "PLANEJAMENTO";
    const andamento = turma.status === "EM_ANDAMENTO";
    const ativa = planejamento || andamento;

    return {
        editar: ativa,
        cancelar: planejamento,
        interromper: andamento,
        matricular: ativa && !turmaLotada(turma),
        removerAluno: planejamento,
        transferirAluno: andamento,
        desistirAluno: andamento,
        vincularProfessor: ativa && turma.numeroDeProfessores < MAX_PROFESSORES_POR_TURMA,
        removerProfessor: planejamento,
        substituirProfessor: andamento && turma.numeroDeProfessores > 0,
    };
}

/* ---------- RN-01 e RN-02: o que a turma precisa para iniciar ---------- */

export interface RequisitoInicio {
    chave: "periodo" | "professor" | "horario" | "aluno";
    rotulo: string;
    ok: boolean;
}

export function requisitosParaIniciar(
    turma: Pick<Turma, "dataInicio" | "dataFim" | "numeroDeProfessores" | "numeroDeAlunos" | "horarios">,
): RequisitoInicio[] {
    return [
        {
            chave: "periodo",
            rotulo: "Período de atividade definido (data de início e fim estimado)",
            ok: Boolean(turma.dataInicio && turma.dataFim),
        },
        { chave: "professor", rotulo: "Pelo menos um professor", ok: turma.numeroDeProfessores > 0 },
        { chave: "horario", rotulo: "Pelo menos um horário", ok: temHorarios(turma.horarios) },
        { chave: "aluno", rotulo: "Pelo menos um aluno", ok: turma.numeroDeAlunos > 0 },
    ];
}

/* ---------- Montagem das requisições ---------- */

export function idsDosInstrumentos(nomes: string[], catalogo: InstrumentoOpcao[]): number[] {
    const normalizar = (texto: string) => texto.trim().toLowerCase();
    return nomes
        .map((nome) => catalogo.find((item) => normalizar(item.nome) === normalizar(nome))?.id)
        .filter((id): id is number => id !== undefined);
}

function mesmosIds(a: number[], b: number[]): boolean {
    if (a.length !== b.length) return false;
    const ordenado = (lista: number[]) => [...lista].sort((x, y) => x - y);
    const [oa, ob] = [ordenado(a), ordenado(b)];
    return oa.every((id, i) => id === ob[i]);
}

export function montarCriacao(values: TurmaFormValues): CriarTurmaRequest {
    return {
        nome: values.nome,
        codigoSala: values.codigoSala,
        numeroVagas: values.numeroVagas,
        turno: values.turno,
        idsInstrumentos: values.idsInstrumentos,
    };
}

/**
 * PATCH só com o que mudou. Evita mandar a data de início de uma turma que já começou
 * e nunca envia "horarios": eles são definidos na Grade de horários.
 */
export function montarAtualizacao(
    turma: TurmaResumo,
    values: TurmaFormValues,
    catalogo: InstrumentoOpcao[],
): AtualizarTurmaRequest {
    const body: AtualizarTurmaRequest = {};

    if (values.nome !== turma.nome) body.nome = values.nome;
    if (values.codigoSala !== turma.sala.code) body.codigoSala = values.codigoSala;
    if (values.numeroVagas !== turma.numeroVagas) body.numeroVagas = values.numeroVagas;
    if (values.turno !== turma.turno) body.turno = values.turno;

    const idsAtuais = idsDosInstrumentos(turma.instrumentos, catalogo);
    if (!mesmosIds(idsAtuais, values.idsInstrumentos)) body.idsInstrumentos = values.idsInstrumentos;

    const dataInicio = values.dataInicio || null;
    if (dataInicio !== (turma.dataInicio ?? null)) body.dataInicio = dataInicio;

    const dataFim = values.dataFim || null;
    if (dataFim !== (turma.dataFim ?? null)) body.dataFim = dataFim;

    return body;
}
