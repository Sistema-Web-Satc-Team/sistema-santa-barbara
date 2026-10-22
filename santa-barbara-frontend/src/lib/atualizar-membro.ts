import type { Member, UpdateMemberRequest } from '#/api/member'
import type { MemberUpdateValues } from '#/schemas/form-schema-member-update'

function parseData(s?: string | null) {
  const [dia = '', mes = '', ano = ''] = (s ?? '').split('/')
  return { dia, mes, ano }
}

const formatarData = ({ dia, mes, ano }: { dia: string; mes: string; ano: string }) =>
  `${dia}/${mes}/${ano}`

const soDigitos = (v?: string | null) => (v ?? '').replace(/\D/g, '')

export function memberToUpdateValues(m: Member): MemberUpdateValues {
  return {
    nome: m.nome ?? '',
    sexo: m.sexo,
    email: m.email ?? '',
    telefone: m.telefone ?? '',
    endereco: m.endereco ?? '',
    dataNascimento: parseData(m.dataNascimento),
  }
}

export function buildUpdateRequest(original: Member, v: MemberUpdateValues): UpdateMemberRequest {
  const req: UpdateMemberRequest = {}

  if (v.nome.trim() !== (original.nome ?? '').trim()) req.nome = v.nome.trim()
  if (v.sexo !== original.sexo) req.sexo = v.sexo

  const email = v.email.trim()
  if (email && email !== (original.email ?? '')) req.email = email

  const telefone = soDigitos(v.telefone)
  if (telefone && telefone !== soDigitos(original.telefone)) req.telefone = telefone

  const endereco = v.endereco.trim()
  if (endereco && endereco !== (original.endereco ?? '').trim()) req.endereco = endereco

  const data = formatarData(v.dataNascimento)
  if (data !== original.dataNascimento) req.dataNascimento = data

  return req
}