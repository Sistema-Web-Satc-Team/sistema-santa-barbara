import { api } from "#/lib/axios";
import type { UUID } from "node:crypto";
import type { Role } from "./funcoes";

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
  funcoes: Role[],
  avatarURL: string
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

export async function saveProfilePhoto(
  photo: Blob
): Promise<void> {
  const formData = new FormData()
  formData.append('file', photo, 'foto-perfil.jpg')

  await api.request({
    method: 'POST',
    url: '/api/v1/auth/me/foto-perfil',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}

export async function deleteProfilePhoto(): Promise<void> {
  await api.delete('/api/v1/auth/me/foto-perfil')
}
