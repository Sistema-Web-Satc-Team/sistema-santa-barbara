import { dataNascimentoValida, ehMenor, type DataNascimentoValue } from '#/lib/idade'
import { z } from 'zod'

export const parentescoEnum = z.enum(['PROGENITORES', 'AVO', 'TIO', 'PADRINHO', 'RESPOSAVEL_LEGAL'])

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export const responsavelFormSchema = z.object({
  modo: z.enum(['existente', 'novo']),
  parentesco: z.string(),
  id: z.string(),
  nome: z.string(),
  email: z.string(),
  telefone: z.string(),
  dataNascimento: z.object({ dia: z.string(), mes: z.string(), ano: z.string() }),
})

export type ResponsavelFormValues = z.infer<typeof responsavelFormSchema>

export const responsavelDefaultValues: ResponsavelFormValues = {
  modo: 'existente',
  parentesco: '',
  id: '',
  nome: '',
  email: '',
  telefone: '',
  dataNascimento: { dia: '', mes: '', ano: '' },
}

export function validarResponsavel(
  r: ResponsavelFormValues,
  ctx: z.RefinementCtx,
  base: (string | number)[],
) {
  const add = (campo: string, message: string) =>
    ctx.addIssue({ code: 'custom', path: [...base, campo], message })

  if (!parentescoEnum.safeParse(r.parentesco).success) add('parentesco', 'Selecione o parentesco.')

  if (r.modo === 'existente') {
    if (!r.id) add('id', 'Selecione um responsável.')
    return
  }

  

  if (r.nome.trim().length < 2) add('nome', 'Informe o nome do responsável.')
  if (!emailRegex.test(r.email.trim())) add('email', 'Insira um e-mail válido.')
  if (r.telefone && r.telefone.replace(/\D/g, '').length < 11) {
    add('telefone', 'O telefone deve ter pelo menos 11 dígitos.')
  }
  if (!dataNascimentoValida(r.dataNascimento)) {
    add('dataNascimento', 'Informe uma data de nascimento válida.')
  } else if (ehMenor(r.dataNascimento as DataNascimentoValue)) {
    add('dataNascimento', 'O responsável deve ser maior de idade.')
  }
}

export type ResponsavelPayload =
  | { modo: 'existente'; id: string; parentesco: z.infer<typeof parentescoEnum> }
  | {
      modo: 'novo'
      parentesco: z.infer<typeof parentescoEnum>
      nome: string
      email: string
      telefone: string
      dataNascimento: DataNascimentoValue
    }

export function toResponsavelPayload(r: ResponsavelFormValues): ResponsavelPayload {
  const parentesco = parentescoEnum.parse(r.parentesco)
  if (r.modo === 'existente') return { modo: 'existente', id: r.id, parentesco }
  return {
    modo: 'novo',
    parentesco,
    nome: r.nome.trim(),
    email: r.email.trim(),
    telefone: r.telefone,
    dataNascimento: r.dataNascimento,
  }
}