import { api } from '#/lib/axios'

export interface Partitura{
    id: string
    titulo: string
    intrumentos: string[]
    idArquivo?: string
}
export interface PartituraRequest{
    idMusica: string
}

export interface CriarPartituraRequest{
    titulo: string
    instrumentos: string[]

}

export interface CadatrarPartiturasParams{
    idMusica: string
    arquivo: File 
    dados: CriarPartituraRequest

}

export interface PartituraFileRequest{
    idArquivo: string
}

export async function GetPartiturasDaMusicaById({ idMusica }: PartituraRequest): Promise<Partitura[]> {
    const { data } = await api.get<Partitura[]>(`/musicas/${idMusica}/partituras`)
    return data
}

export async function CreatePartitura({idMusica, arquivo, dados}: CadatrarPartiturasParams): Promise<any> {

        const formData = new FormData()

        formData.append('file', arquivo)

        formData.append('request',JSON.stringify(dados))

        const { data } = await api.post(`/musicas/${idMusica}/partituras`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })

    return data

}

export async function GetFilePartituraById({idArquivo}:PartituraFileRequest): Promise<Blob> {
    const { data } = await api.get<Blob>(`/partituras/files${idArquivo}`, {
        ResponseType: 'blob'
    })
    return data

}
