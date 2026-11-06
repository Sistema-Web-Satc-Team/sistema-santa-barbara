import { api } from "#/lib/axios";

export interface Sala {
    id: number;
    code: string;
    capacidade: number;
}

export async function listarSalas(capacidade?: number): Promise<Sala[]> {
    const { data } = await api.get<Sala[]>("/api/v1/salas", {
        params: capacidade ? { capacidade } : undefined,
    });
    return data;
}
