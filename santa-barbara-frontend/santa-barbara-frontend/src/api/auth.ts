import { api } from "#/lib/axios";
import type { UUID } from "node:crypto";

export interface LoginRequest {
    login: string;
    senha: string;
}

export interface UpdateProfileFields {
  email?: string,
  nomeDeUsuario?: string,
  telefone?: string,
  endereco?: string,
  avatarUrl?: string 
}

export interface MeResponse {
  id: UUID,
  nome: string,
  nomeUsuario: string,
  email: string,
  telefone: string,
  endereco: string,
  papeis: string[],
  urlAvatar: string,
  age: number,
  dataNascimento: string
}


export async function login(data: LoginRequest): Promise<void> {
    await api.post("/api/v1/auth/login", data);
}

export async function logout(): Promise<void> {
    await api.post("/api/v1/auth/logout");
}

export async function fetchUserProfile(): Promise<MeResponse> {
  const { data } = await api.get('/api/v1/auth/me')
  return data
}

export async function updateUserProfile(data: UpdateProfileFields) {
  await api.patch('/api/v1/auth/me', data);
}

export async function uploadUserAvatar(file: File): Promise<string> {
  const formData = new FormData();
  formData.append('file', file);

  const { data } = await api.post<{ url: string }>('/api/v1/auth/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });

  return data.url;
}

