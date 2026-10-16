import type { Member } from '#/api/member'
import type { CanalValue, StatusFiltro } from '#/components/business/convites/convites'
import { api } from '#/lib/axios'

export interface Convite {
  idConvite: string
  conviteURL: string
  criadoEm: string
  expiraEm: string
  status: string
  membro: { id: string; nome: string; email: string | null; telefone: string | null }
}

export interface ConviteEnviado {
  idConvite: string
  URL: string
  criadoEm: string
  expiraEm: string
  status: string
}

export interface ConvitesFilter {
  status?: StatusFiltro
  search?: string
  page: number
  size: number
}
export interface AceitarConviteRequest {
  idConvite: string;
  senha: string;
  nomeDeUsuario: string;
}

export async function validarConvite(id: string): Promise<boolean> {
  const { data } = await api.get<boolean>(
    `api/v1/convites/validar/${id}`,
  );

  console.log(data)

  return data;
}

export async function aceitarConvite(
  request: AceitarConviteRequest,
): Promise<void> {
  await api.post('api/v1/convites/aceitar', request);
}

export async function fetchConvites(f: ConvitesFilter): Promise<PageDefaultResponse<Convite>> {
  const { data } = await api.get('api/v1/convites', {
    params: {
      status: f.status,
      search: f.search || undefined,
      page: f.page,
      size: f.size
    },
  })
  return data
}

export async function criarConvite(req: { idMembro: string; canal: CanalValue }) {
  const { data } = await api.post<ConviteEnviado>('api/v1/convites', req)
  return data
}

export async function reenviarConvite(idConvite: string, canal: CanalValue) {
  const { data } = await api.put<ConviteEnviado>(`api/v1/convites/${idConvite}`, { canal })
  return data
}


export async function searchMembrosConvidaveis(search: string): Promise<Member[]> {
  const { data } = await api.get<PageDefaultResponse<Member>>('api/v1/membros', {
    params: { search: search || undefined, semAcesso: true, size: 8, sort: 'nome,asc', page: 0 },
  })
  return data.content
}