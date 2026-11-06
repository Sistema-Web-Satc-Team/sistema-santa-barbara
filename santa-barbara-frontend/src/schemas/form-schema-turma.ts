import { z } from "zod";
import { TURNOS } from "#/api/turmas";
import type { Sala } from "#/api/salas";

// O que o formulário guarda enquanto a pessoa digita: tudo texto, exceto os instrumentos.
export interface TurmaFormState {
    nome: string;
    codigoSala: string;
    numeroVagas: string;
    turno: string;
    idsInstrumentos: number[];
    dataInicio: string; // "yyyy-MM-dd" ou ""
    dataFim: string; // "yyyy-MM-dd" ou ""
}

export interface TurmaSchemaOpcoes {
    salas: Sala[];
    // Alunos ativos hoje: RN-08, as vagas não podem ficar abaixo disso.
    minVagas?: number;
}

const dataOpcional = z
    .string()
    .refine((valor) => valor === "" || /^\d{4}-\d{2}-\d{2}$/.test(valor), "Informe uma data válida.");

export function criarTurmaFormSchema({ salas, minVagas = 0 }: TurmaSchemaOpcoes) {
    return z
        .object({
            nome: z
                .string()
                .trim()
                .min(3, "Informe o nome da turma (mínimo 3 caracteres).")
                .max(100, "Use no máximo 100 caracteres."),
            codigoSala: z.string().min(1, "Escolha uma sala."),
            numeroVagas: z
                .string()
                .trim()
                .min(1, "Informe o número de vagas.")
                .regex(/^\d+$/, "Use apenas números inteiros.")
                .transform(Number)
                .pipe(z.number().min(1, "A turma precisa de pelo menos 1 vaga.")),
            turno: z.enum(TURNOS, { message: "Escolha o turno." }),
            idsInstrumentos: z.array(z.number()).min(1, "Escolha pelo menos um instrumento."),
            dataInicio: dataOpcional,
            dataFim: dataOpcional,
        })
        .superRefine((valores, ctx) => {
            const sala = salas.find((item) => item.code === valores.codigoSala);

            // Se o campo de vagas já falhou, o valor continua texto: só compara quando é número.
            if (sala && typeof valores.numeroVagas === "number") {
                // RN-09
                if (valores.numeroVagas > sala.capacidade) {
                    ctx.addIssue({
                        code: "custom",
                        path: ["numeroVagas"],
                        message: `A sala ${sala.code} comporta no máximo ${sala.capacidade} vagas.`,
                    });
                }
                // RN-08
                if (minVagas > 0 && valores.numeroVagas < minVagas) {
                    ctx.addIssue({
                        code: "custom",
                        path: ["numeroVagas"],
                        message: `Já há ${minVagas} alunos matriculados. As vagas não podem ser menores.`,
                    });
                }
            }

            if (valores.dataFim) {
                if (!valores.dataInicio) {
                    ctx.addIssue({
                        code: "custom",
                        path: ["dataFim"],
                        message: "Informe a data de início antes do fim estimado.",
                    });
                } else if (valores.dataFim < valores.dataInicio) {
                    ctx.addIssue({
                        code: "custom",
                        path: ["dataFim"],
                        message: "O fim estimado não pode ser anterior à data de início.",
                    });
                }
            }
        });
}

export type TurmaFormSchema = ReturnType<typeof criarTurmaFormSchema>;
export type TurmaFormValues = z.infer<TurmaFormSchema>;
