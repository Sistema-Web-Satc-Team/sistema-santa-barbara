import type { Member } from '#/api/member'
import { api } from '#/lib/axios'
import type { RelacaoValue } from '#/lib/relacao-responsavel'


export interface ResponsavelDoMembro {
  id: string
  nome: string
  telefone: string | null
  endereco: string | null
  email: string | null
  dataNascimento: string
  grauRelacao: string
}

export interface MemberResponsaveisResponse {
  membro: Member
  responsaveis: ResponsavelDoMembro[]
}

export async function fetchMemberResponsaveis(idMembro: string) {
  const { data } = await api.get<MemberResponsaveisResponse>(`api/v1/membros/${idMembro}/responsaveis`)
  return data
}

export async function saveMemberResponsavel(idMembro: string, idResponsavel: string, grauRelacao: RelacaoValue) {
  await api.put(`api/v1/membros/${idMembro}/responsaveis/${idResponsavel}`, { grauRelacao })
}

export async function removeMemberResponsavel(idMembro: string, idResponsavel: string) {
  await api.delete(`api/v1/membros/${idMembro}/responsaveis/${idResponsavel}`)
}