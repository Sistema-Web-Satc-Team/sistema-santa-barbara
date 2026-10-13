import { api } from '#/lib/axios'

export const INSTRUMENT_TYPES = [
  { value: 'SOPRO', label: 'Sopro' },
  { value: 'TECLAS', label: 'Teclas' },
  { value: 'CORDA', label: 'Cordas' },
  { value: 'PERCUSSAO', label: 'Percussão' },
] as const

export type InstrumentType = (typeof INSTRUMENT_TYPES)[number]['value']


export function normalizeInstrumentType(raw: string): InstrumentType | null {
  const value = raw
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toUpperCase()
    .trim()

  if (value === 'TECLA') return 'TECLAS'
  if (value === 'CORDAS') return 'CORDA'

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
  urlImagem: string | null
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

function normalizeText(value: string): string {
  return value
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
    .trim()
}

function paginateLocally(
  all: Instrumento[],
  request: InstrumentoFilterRequest,
): InstrumentosPage {
  const term = normalizeText(request.nome ?? '')
  const type = request.tipo ? normalizeInstrumentType(request.tipo) : null

  const filtered = all.filter(
    (item) =>
      (term === '' || normalizeText(item.nome).includes(term)) &&
      (type === null || normalizeInstrumentType(item.tipo) === type),
  )

  const size = request.size ?? 12
  const page = request.page ?? 0

  return {
    content: filtered.slice(page * size, (page + 1) * size),
    page,
    size,
    totalElements: filtered.length,
    totalPages: Math.max(1, Math.ceil(filtered.length / size)),
  }
}

export async function fetchInstrumentos(
  request: InstrumentoFilterRequest,
): Promise<InstrumentosPage> {
  const { data } = await api.get<Instrumento[] | InstrumentosPage>(
    'api/v1/instrumentos',
    {
      params: {
        nome: request.nome || undefined,
        tipo: request.tipo || undefined,
        page: request.page ?? 0,
        size: request.size ?? 12,
      },
    },
  )

  return Array.isArray(data) ? paginateLocally(data, request) : data
}

export async function createInstrumento(
  request: InstrumentoInput,
): Promise<void> {
  await api.post('api/v1/instrumentos', request)
}

export async function updateInstrumento(
  request: UpdateInstrumentoRequest,
): Promise<void> {
  const { id, ...body } = request
  await api.patch(`api/v1/instrumentos/${id}`, body)
}

export async function deleteInstrumento(id: number): Promise<void> {
  await api.delete(`api/v1/instrumentos/${id}`)
}
