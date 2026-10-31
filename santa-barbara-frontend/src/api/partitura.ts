import { api } from '#/lib/axios'

export interface PartituraInstrumento {
  id: number
  nome: string
}

export interface Partitura {
  id: string
  titulo: string
  arranjador: string | null
  partituraUrl: string | null
  instrumentos: PartituraInstrumento[]
}

export interface CriarPartituraRequest {
  titulo: string
  arranjador?: string
  instrumentos: string[]
}

export interface CriarPartituraParams {
  idMusica: string
  arquivo: File
  dados: CriarPartituraRequest
}

export async function listarPartiturasDaMusica(idMusica: string): Promise<Partitura[]> {
  const { data } = await api.get<Partitura[]>(`api/v1/musicas/${idMusica}/partituras`)
  return data
}

export async function criarPartitura({
  idMusica,
  arquivo,
  dados,
}: CriarPartituraParams): Promise<Partitura> {
  const formData = new FormData()
  formData.append('file', arquivo)
  formData.append('request', JSON.stringify(dados))

  const { data } = await api.post<Partitura>(
    `api/v1/musicas/${idMusica}/partituras`,
    formData,
    {
      headers: { 'Content-Type': 'multipart/form-data' },
    },
  )

  return data
}

export async function baixarArquivoPartitura(url: string): Promise<Blob> {
  const { data } = await api.get<Blob>(url, { responseType: 'blob' })
  return data
}
