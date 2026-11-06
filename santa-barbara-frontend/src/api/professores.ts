import { api } from '#/lib/axios'
import type { Sexo } from './member'

export interface InstrumentoProfessor {
  ensinando: string[]
}

export interface Professor {
  id: string
  nome: string
  email: string | null
  telefone: string | null
  sexo: Sexo | string
  temTurma: boolean
  instrumentos: InstrumentoProfessor | null
}

export interface ProfessoresFilterRequest {
  search?: string
  instrumentosEnsinando?: string[]
  page?: number
  size?: number
}

export async function fetchProfessores(
  request: ProfessoresFilterRequest,
): Promise<PageDefaultResponse<Professor>> {
  const instrumentosParam =
    request.instrumentosEnsinando && request.instrumentosEnsinando.length > 0
      ? request.instrumentosEnsinando.join(',')
      : undefined

  const { data } = await api.get<PageDefaultResponse<Professor>>(
    'api/v1/professores',
    {
      params: {
        search: request.search?.trim() || undefined,
        instrumentosEnsinando: instrumentosParam,
        page: request.page ?? 0,
        size: request.size ?? 10,
      },
    },
  )

  return data
}
