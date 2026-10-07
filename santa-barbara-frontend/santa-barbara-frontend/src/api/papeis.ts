import { api } from "#/lib/axios";

interface PapeisResponse {
    content: string[];
    pageNumber: number;
    pageSize: number;
}


export async function fetchRoles(): Promise<string[]> {
    const response = await api.get<PapeisResponse>(
        `api/v1/papeis?page=0&size=50`
    );
    
    return response.data.content ?? [];
}
