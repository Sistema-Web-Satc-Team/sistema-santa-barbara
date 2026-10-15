import { dataNascimentoValida, ehMenor } from '#/lib/idade'
import { z } from 'zod'
import {
    responsavelDefaultValues,
    responsavelFormSchema,
    toResponsavelPayload,
    validarResponsavel,
} from './form-schema-responsavel'

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const digitos = (v: string) => v.replace(/\D/g, '')

export const formMemberSchema = z
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
    responsavel: responsavelFormSchema,
  })
  .superRefine((data, ctx) => {
    const menor = ehMenor(data.dataNascimento)

    if (data.email) {
      if (!emailRegex.test(data.email)) {
        ctx.addIssue({ code: 'custom', path: ['email'], message: 'Insira um e-mail válido.' })
      }
    } else if (!menor) {
      ctx.addIssue({ code: 'custom', path: ['email'], message: 'O e-mail é obrigatório.' })
    }

    if (menor) validarResponsavel(data.responsavel, ctx, ['responsavel'])
  })

export type MemberFormValues = z.infer<typeof formMemberSchema>

export const memberDefaultValues: MemberFormValues = {
  nome: '',
  sexo: 'NAO_INFORMADO',
  email: '',
  telefone: '',
  dataNascimento: { dia: '', mes: '', ano: '' },
  endereco: '',
  responsavel: responsavelDefaultValues,
}

export function toMemberPayload(v: MemberFormValues) {
  const { responsavel, ...resto } = v
  return ehMenor(v.dataNascimento)
    ? { ...resto, responsavel: toResponsavelPayload(responsavel) }
    : resto
}

export type MemberPayload = ReturnType<typeof toMemberPayload>