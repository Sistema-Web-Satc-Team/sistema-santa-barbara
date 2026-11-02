import { z } from "zod";

// Escolha de um aluno ou professor na busca (vincular, substituir, matricular).
export const escolherPessoaSchema = z.object({
    idPessoa: z.string().min(1, "Escolha uma pessoa na lista."),
});

// Desistência: a justificativa fica no histórico da matrícula.
export const justificativaSchema = z.object({
    justificativa: z.string().trim().min(3, "Explique o motivo (mínimo 3 caracteres)."),
});

export const transferenciaSchema = z.object({
    idNovaTurma: z.string().min(1, "Escolha a turma de destino."),
    justificativa: justificativaSchema.shape.justificativa,
});

export type TransferenciaValues = z.infer<typeof transferenciaSchema>;
