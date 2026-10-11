import { api } from "#/lib/axios";

export interface Role {
    code: string;
    nome: string;
    descricao: string;
}


export async function fetchRoles(): Promise<Role[]> {
    const response = await api.get(
        `api/v1/funcoes`
    );
    
    return response.data ?? [];
}
