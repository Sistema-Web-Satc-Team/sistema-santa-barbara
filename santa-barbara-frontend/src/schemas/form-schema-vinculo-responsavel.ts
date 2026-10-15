import { relacaoValida } from '#/lib/relacao-responsavel'
import { z } from 'zod'


export const formVinculoResponsavelSchema = z.object({
  idResponsavel: z.string().min(1, 'Selecione um responsável.'),
  grauRelacao: z
    .string()
    .refine((v) => relacaoValida(v), 'Selecione o grau de relação.'),
})

export type VinculoResponsavelValues = z.infer<typeof formVinculoResponsavelSchema>