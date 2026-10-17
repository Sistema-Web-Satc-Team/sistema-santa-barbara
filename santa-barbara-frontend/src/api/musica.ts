import { api } from '#/lib/axios'
import { normalizeText } from '#/lib/text'

export interface Musica {
  id: string
  titulo: string
  compositor: string
  generos: string[]
}

export interface MusicasPage {
  content: Musica[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface MusicaFilterRequest {
  search?: string
  page?: number
  size?: number
}

export interface MusicaInput {
  titulo: string
  compositor: string
  generos: string[]
}

export interface UpdateMusicaRequest extends MusicaInput {
  id: string
}

export async function fetchMusicas(request: MusicaFilterRequest): Promise<MusicasPage> {
  const { data } = await api.get<MusicasPage | Musica[]>('api/v1/musicas', {
    params: {
      search: request.search || undefined,
      page: request.page ?? 0,
      size: request.size ?? 20,
    },
  })


  if (Array.isArray(data)) {
    return {
      content: data,
      page: 0,
      size: data.length,
      totalElements: data.length,
      totalPages: 1,
    }
  }

  return data
}

export async function updateMusica(request: UpdateMusicaRequest): Promise<void> {
  const { id, ...body } = request
  await api.put(`api/v1/musicas/${id}`, body)
}

export async function deleteMusica(id: string): Promise<void> {
  await api.delete(`api/v1/musicas/${id}`)
}

export async function fetchGeneroSugestoes(q: string): Promise<string[]> {
  const { data } = await api.get<string[]>('api/v1/musicas/generos/sugestoes', {
    params: { q },
  })
  return data
}


export function matchesGenres(musica: Musica, selected: string[]): boolean {
  if (selected.length === 0) return true

  const wanted = selected.map(normalizeText)
  return musica.generos.some((genero) => wanted.includes(normalizeText(genero)))
}
