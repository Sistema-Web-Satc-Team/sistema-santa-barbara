import type { Role } from '#/api/funcoes'
import type { Sexo } from '#/api/member'
import { api } from '#/lib/axios'

export type StatusVinculo = 'ATIVO' | 'AGENDADO' | 'ENCERRADO' | 'CANCELADO'

export interface Vinculo {
  id: number
  dataInicio: string
  dataTermino: string | null
  funcao: Role
  status: string
}

export interface MemberVinculos {
  id: string
  nome: string
  dataNascimento: string
  idade: number
  sexo: Sexo
  vinculos: {
    atuais: Vinculo[]
    encerrados: Vinculo[]
    cancelados: Vinculo[]
  }
}

export interface NovoVinculoRequest {
  dataInicio: string
  funcao: string
}

export async function fetchMemberVinculos(idMembro: string) {
  const { data } = await api.get<MemberVinculos>(`api/v1/membros/${idMembro}/vinculos`)
  return data
}

export async function criarVinculo(idMembro: string, req: NovoVinculoRequest) {
  const { data } = await api.post<Vinculo>(`api/v1/membros/${idMembro}/vinculos`, req)
  return data
}

export async function encerrarVinculo(idMembro: string, idVinculo: number) {
  await api.post(`api/v1/membros/${idMembro}/vinculos/${idVinculo}/encerrar`)
}

export async function cancelarVinculo(idMembro: string, idVinculo: number) {
  await api.delete(`api/v1/membros/${idMembro}/vinculos/${idVinculo}/cancelar`)
}