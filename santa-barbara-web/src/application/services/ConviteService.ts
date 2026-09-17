import axios from 'axios';
import { 
  Convite, 
  CriarConviteRequest, 
  ConviteResponse, 
  RegistrarNovoUsuarioRequest 
} from '@/application/model/ConviteModel';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3001/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use((config) => {
  const token = 
    localStorage.getItem('token') ||
    localStorage.getItem('authToken') ||
    localStorage.getItem('accessToken') ||
    localStorage.getItem('jwt');

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export async function listarConvites(): Promise<Convite[]> {
  try {
    const response = await apiClient.get<ConviteResponse[]>('/convites');
    return response.data.map(mapearConviteResponse);
  } catch (error) {
    console.error('Erro ao listar convites:', error);
    throw error;
  }
}

export async function criarConvite(dados: CriarConviteRequest): Promise<Convite> {
  try {
    const response = await apiClient.post<ConviteResponse>('/convites', dados);
    return mapearConviteResponse(response.data);
  } catch (error) {
    console.error('Erro ao criar convite:', error);
    throw error;
  }
}

export async function obterConvite(idConvite: string): Promise<Convite> {
  try {
    const response = await apiClient.get<ConviteResponse>(`/convites/${idConvite}`);
    return mapearConviteResponse(response.data);
  } catch (error) {
    console.error('Erro ao obter convite:', error);
    throw error;
  }
}

export async function registrarNovoUsuario(dados: RegistrarNovoUsuarioRequest): Promise<void> {
  try {
    await apiClient.post('/convites/registrar', dados);
  } catch (error) {
    console.error('Erro ao registrar novo usuário:', error);
    throw error;
  }
}

function mapearConviteResponse(response: ConviteResponse): Convite {
  return {
    id: response.id,
    emailConvidado: response.emailConvidado,
    papelDesignado: response.papelDesignado as 'ALUNO' | 'PROFESSOR' | 'ADMINISTRADOR',
    dataConvite: new Date(response.dataConvite),
    status: response.status as 'PENDENTE' | 'ACEITO' | 'EXPIRADO' | 'RECUSADO',
  };
}
