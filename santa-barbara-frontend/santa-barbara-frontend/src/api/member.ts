import { api } from "#/lib/axios";

export interface Member {
    id: string;
    nome: string;
    nomeDeUsuario: string;
    telefone: string;
    endereco: string;
    email: string;
    papeis: string[];
    idade: number;
    dataNascimento: string;
    status: "ATIVO" | "INATIVO";
}

export interface UpdateMemberRequest extends FetchByIdRequest {
    nome?: string;
    telefone?: string;
    endereco?: string;
    email?: string;
    papeis?: string[];
    dataNascimento?: string;
    status?: "ATIVO" | "INATIVO";
}

export interface RegisterMemberRequest {
    nome: string;
    email: string;
    papeis: string[];
    telefone: string;
    endereco: string;
    dataNascimento: string;
}

export interface MembersFilterRequest {
    nome?: string;
    email?: string;
    status?: string;
    search?: string;
    page?: number;
    size?: number;
}

export interface MemberByEmailRequest {
    email: string;
}

async function fetchMembers(request: MembersFilterRequest): Promise<ListResponse<Member>> {
    const  { data } = await api.get(`api/v1/membros`,
        {
            params: {
                email: request.email || undefined,
                nome: request.nome || undefined,
                status: request.status || undefined,
                page: request.page ?? 0,
                size: request.size ?? 10,
            },
        }
    );

    return data;
}

async function fetchMemberById(request: FetchByIdRequest): Promise<Member> {
    const { data } = await api.get(`api/v1/membros/` + request.id);
    return data;
}

async function fetchMemberByEmail(request: MemberByEmailRequest): Promise<Member> {
    const response = await api.get('api/v1/membros', {
        params: {
            email: request.email,
        },
    });
  
    return response.data.content?.[0] ?? null;
}

async function updateMemberById(request: UpdateMemberRequest): Promise<void> {
    const {id, ...body} = request;
    await api.patch(`api/v1/membros/${id}`, body)
}

async function registerMember(request: RegisterMemberRequest): Promise<void> {
    await api.post(`api/v1/membros`, request)
}

export { fetchMemberByEmail, fetchMemberById, fetchMembers, registerMember, updateMemberById };

