import { cadastrarMembroMenor, registerMember, type RegisterMemberRequest } from '#/api/member'
import { ehMenor } from '#/lib/idade'
import type { MemberFormValues } from '#/schemas/form-schema-member'
import { parentescoEnum } from '#/schemas/form-schema-responsavel'
import axios from 'axios'

const formatarData = ({ dia, mes, ano }: { dia: string; mes: string; ano: string }) =>
  `${dia}/${mes}/${ano}`

const opcional = (v: string) => (v.trim() === '' ? undefined : v.trim())
const soDigitos = (v: string) => opcional(v.replace(/\D/g, ''))

export function mensagemDeErro(err: unknown) {
  if (axios.isAxiosError(err)) {
    const data = err.response?.data as { message?: string } | undefined
    return data?.message ?? err.message
  }
  return err instanceof Error ? err.message : 'Erro inesperado.'
}

export class ResponsavelCriadoError extends Error {
  constructor(public idResponsavel: string, public causa: unknown) {
    super(mensagemDeErro(causa))
  }
}

export async function registrarMembro(
  values: MemberFormValues,
  opts?: { idResponsavelCriado?: string | null },
) {
  const base: RegisterMemberRequest = {
    nome: values.nome.trim(),
    email: opcional(values.email),
    telefone: soDigitos(values.telefone),
    endereco: opcional(values.endereco),
    sexo: values.sexo,
    dataNascimento: formatarData(values.dataNascimento),
  }

  if (!ehMenor(values.dataNascimento)) {
    await registerMember(base)
    return
  }

  const r = values.responsavel
  const grauRelacao = parentescoEnum.parse(r.parentesco)

  let idResponsavel = r.id
  let criouAgora = false

  if (r.modo === 'novo') {
    if (opts?.idResponsavelCriado) {
      idResponsavel = opts.idResponsavelCriado
    } else {
      const novo = await registerMember({
        nome: r.nome.trim(),
        email: opcional(r.email),
        telefone: soDigitos(r.telefone),
        endereco: opcional(values.endereco),
        sexo: 'NAO_INFORMADO',
        dataNascimento: formatarData(r.dataNascimento),
      })
      idResponsavel = novo.id
      criouAgora = true
    }
  }

  try {
    await cadastrarMembroMenor({ menor: base, idResponsavel, grauRelacao })
  } catch (err) {
    if (r.modo === 'novo') throw new ResponsavelCriadoError(idResponsavel, err)
    throw err
  }
}