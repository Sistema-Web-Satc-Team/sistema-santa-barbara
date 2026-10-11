import { api } from "#/lib/axios";
import type { UUID } from "node:crypto";

export interface LoginRequest {
    login: string;
    senha: string;
}

export interface MeUpdateRequest {
  telefone?: string,
  endereco?: string
}

export interface MeProfileResponse {
  id: UUID,
  nome: string,
  nomeUsuario: string,
  email: string,
  telefone: string,
  endereco: string,
  dataNascimento: string,
  sexo: string,
  idade: number,
  funcoes: string[],
  avatarUrl: string
}


export async function login(data: LoginRequest): Promise<void> {
  await api.post("/api/v1/auth/login", data);
}

export async function logout(): Promise<void> {
  await api.post("/api/v1/auth/logout");
}

export async function loggedIn(): Promise<boolean> {
  try {
    await api.get("api/v1/auth/me/verificar");

    return true
  } catch {
    return false;
  }
}

export async function fetchUserProfile(): Promise<MeProfileResponse> {
  const { data } = await api.get('/api/v1/auth/me')
  return data
}

export async function updateUserProfile(data: MeUpdateRequest) {
  await api.patch('/api/v1/auth/me', data);
}

/*
export async function uploadUserAvatar(file: File): Promise<string> {
  const formData = new FormData();
  formData.append('file', file);

  const { data } = await api.post<{ url: string }>('/api/v1/auth/?', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });

  return data.url;
}
*/
