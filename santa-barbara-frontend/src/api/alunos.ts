import { api } from '#/lib/axios'
import type { Sexo } from './member'

export interface InstrumentoAluno {
  aprendendo: string[]
}

export interface AlunoResponsavel {
  id: string | number
  nome: string
  telefone: string | null
  email: string | null
  relacao: string
}

export interface Aluno {
  id: string
  nome: string
  email: string | null
  telefone: string | null
  sexo: Sexo | string
  idade: number
  temTurma: boolean
  instrumentos: InstrumentoAluno | null
  responsaveis: AlunoResponsavel[] | null
}

export interface AlunosFilterRequest {
  search?: string
  instrumentosAprendendo?: string[]
  apenasMenores?: boolean
  page?: number
  size?: number
}

export async function fetchAlunos(
  request: AlunosFilterRequest,
): Promise<PageDefaultResponse<Aluno>> {
  const instrumentosParam =
    request.instrumentosAprendendo && request.instrumentosAprendendo.length > 0
      ? request.instrumentosAprendendo.join(',')
      : undefined

  const { data } = await api.get<PageDefaultResponse<Aluno>>('api/v1/alunos', {
    params: {
      search: request.search?.trim() || undefined,
      instrumentosAprendendo: instrumentosParam,
      apenasMenores: request.apenasMenores ? true : undefined,
      page: request.page ?? 0,
      size: request.size ?? 10,
    },
  })

  return data
}
