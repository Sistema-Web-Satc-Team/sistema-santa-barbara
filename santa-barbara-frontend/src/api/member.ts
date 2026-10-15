import { api } from "#/lib/axios";
import type { Role } from "./funcoes";


export type Sexo = "MASCULINO" | "FEMININO" | "NAO_INFORMADO";
export type StatusCredencialAcesso =  "ATIVO" | "SUSPENSO" | "BLOQUEADO" | "SEM_ACESSO";

export interface Member {
    id: string;
    nome: string;
    telefone: string;
    endereco: string;
    email: string;
    dataNascimento: string;
    sexo: Sexo;
    idade: number;
    hasVinculos: boolean;
    menorDeIdade: boolean;
    statusCredencialAcesso: StatusCredencialAcesso;
    avatarUrl?: string;
}

export interface UpdateMemberRequest {
    nome?: string;
    email?: string;
    telefone?: string;
    endereco?: string;
    sexo?: string;
    dataNascimento?: string;
}

export interface RegisterMemberRequest {
    nome: string;
    email?: string
    telefone?: string
    endereco?: string
    sexo: string;
    dataNascimento: string;
}

// Filters

export interface MembersFilterRequest extends PageFilters<Member> {
    search?: string,
}

/*
*
* BINDS
*
*/

export type StatusBind = "ATIVO" | "ENCERRADO" | "CANCELADO" | "SUSPENSO";

export interface Bind {
  id: number;
  dataInicio: string;
  dataTermino: string;
  funcao: Role;
  status: StatusBind;
}

export interface BindsGroup {
  atuais: Bind[];
  encerrados: Bind[];
  cancelados: Bind[];
}

export interface MemberBinds {
  id: string;
  nome: string;
  dataNascimento: string; 
  idade: number;
  sexo: Sexo;
  vinculos: BindsGroup;
}

/*
*
* Responsável
* 
*/

export type RelacaoResponsavel = "PROGENITORES" | "AVO" | "TIO" | "PADRINHO" | "RESPOSAVEL_LEGAL";


export interface RequestMembroMenor {
    menor: RegisterMemberRequest,
    idResponsavel: string;
    grauRelacao: RelacaoResponsavel;
}


async function fetchMembers(request: MembersFilterRequest): Promise<PageDefaultResponse<Member>> {
    const  { data } = await api.get(`api/v1/membros`,
        {
            params: {
                search: request.search,
                size: request.size,
                sort: request.sort,
                page: request.page
            },
        }
    );

    return data;
}

async function fetchMemberBindsById(request: FetchByIdRequest): Promise<Member> {
    const { data } = await api.get(`api/v1/membros/${request.id}/vinculos`);
    return data;
}

async function updateMemberById(id: string, request: UpdateMemberRequest): Promise<void> {
    const {...body} = request;
    await api.patch(`api/v1/membros/${id}`, body)
}

async function registerMember(request: RegisterMemberRequest): Promise<Member> {
  const { data } = await api.post<Member>('api/v1/membros', request)
  return data
}

async function cadastrarMembroMenor(request: RequestMembroMenor) {
    await api.post(`api/v1/membros/menores`, request)
}

async function searchResponsaveis(search: string): Promise<Member[]> {
  const { data } = await api.get<PageDefaultResponse<Member>>('api/v1/membros', {
    params: {
      search: search || undefined,
      maiorDeIdade: true,
      size: 8,
      sort: 'nome,asc',
      page: 0,
    },
  })
  return data.content // ajuste se o seu PageDefaultResponse usar outro nome
}

export { cadastrarMembroMenor, fetchMemberBindsById, fetchMembers, registerMember, searchResponsaveis, updateMemberById };

