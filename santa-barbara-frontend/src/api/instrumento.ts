import { api } from '#/lib/axios'


export const INSTRUMENT_TYPES = [
  { value: 'sopro', label: 'Sopro' },
  { value: 'teclas', label: 'Teclas' },
  { value: 'cordas', label: 'Cordas' },
  { value: 'percussao', label: 'Percussão' },
] as const

export type InstrumentType = (typeof INSTRUMENT_TYPES)[number]['value']


export function normalizeInstrumentType(raw: string): InstrumentType | null {
  const value = raw
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
    .trim()

  if (value === 'tecla') return 'teclas'

  const found = INSTRUMENT_TYPES.find((type) => type.value === value)
  return found ? found.value : null
}

export function getInstrumentTypeLabel(raw: string): string {
  const type = normalizeInstrumentType(raw)
  const found = INSTRUMENT_TYPES.find((item) => item.value === type)
  return found ? found.label : raw
}

export interface Instrumento {
  id: number
  nome: string
  descricao: string | null
  imagemURL: string | null
  tipo: string
}

export interface InstrumentosPage {
  content: Instrumento[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface InstrumentoFilterRequest {
  nome?: string
  tipo?: string
  page?: number
  size?: number
}

export interface InstrumentoInput {
  nome: string
  descricao: string
  tipo: InstrumentType
}

export interface UpdateInstrumentoRequest extends InstrumentoInput {
  id: number
}

export async function fetchInstrumentos(
  request: InstrumentoFilterRequest,
): Promise<InstrumentosPage> {
  const { data } = await api.get<InstrumentosPage>('api/v1/instrumentos', {
    params: {
      nome: request.nome || undefined,
      tipo: request.tipo || undefined,
      page: request.page ?? 0,
      size: request.size ?? 12,
    },
  })

  return data
}

export async function createInstrumento(request: InstrumentoInput): Promise<void> {
  await api.post('api/v1/instrumentos', request)
}

export async function updateInstrumento(
  request: UpdateInstrumentoRequest,
): Promise<void> {
  const { id, ...body } = request
  await api.put(`api/v1/instrumentos/${id}`, body)
}

export async function deleteInstrumento(id: number): Promise<void> {
  await api.delete(`api/v1/instrumentos/${id}`)
}
