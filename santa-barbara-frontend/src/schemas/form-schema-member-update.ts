import { dataNascimentoValida, ehMenor } from '#/lib/idade'
import { z } from 'zod'

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const digitos = (v: string) => v.replace(/\D/g, '')

export const formMemberUpdateSchema = z
  .object({
    nome: z.string().trim().min(2, 'O nome deve ter pelo menos 2 caracteres.'),
    dataNascimento: z
      .object({ dia: z.string(), mes: z.string(), ano: z.string() })
      .refine(dataNascimentoValida, 'Informe uma data de nascimento válida no passado.'),
    sexo: z.enum(['MASCULINO', 'FEMININO', 'NAO_INFORMADO'], {
      message: 'Selecione um sexo válido.',
    }),
    email: z.string().trim(),
    telefone: z
      .string()
      .refine((v) => v === '' || digitos(v).length >= 11, 'O telefone deve ter pelo menos 11 dígitos.'),
    endereco: z
      .string()
      .trim()
      .refine((v) => v === '' || v.length >= 2, 'O endereço deve ter pelo menos 2 caracteres.'),
  })
  .superRefine((data, ctx) => {
    if (data.email) {
      if (!emailRegex.test(data.email)) {
        ctx.addIssue({ code: 'custom', path: ['email'], message: 'Insira um e-mail válido.' })
      }
    } else if (!ehMenor(data.dataNascimento)) {
      ctx.addIssue({ code: 'custom', path: ['email'], message: 'O e-mail é obrigatório.' })
    }
  })

export type MemberUpdateValues = z.infer<typeof formMemberUpdateSchema>