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

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value)
}

function normalizeRoles(value: unknown): Role[] {
  const roles = Array.isArray(value) ? value : isRecord(value) || typeof value === "string" ? [value] : null

  if (!roles) {
    throw new Error("Formato de funções inválido na resposta do perfil.")
  }

  return roles.map((role) => {
    if (typeof role === "string") {
      return { code: role, nome: role, descricao: "" }
    }

    if (
      isRecord(role) &&
      typeof role.code === "string" &&
      typeof role.nome === "string" &&
      typeof role.descricao === "string"
    ) {
      return { code: role.code, nome: role.nome, descricao: role.descricao }
    }

    throw new Error("Formato de função inválido na resposta do perfil.")
  })
}

export function normalizeMeProfile(data: unknown): MeProfileResponse {
  if (!isRecord(data)) {
    throw new Error("Formato inválido na resposta do perfil.")
  }

  const profile = data as Omit<MeProfileResponse, "funcoes"> & { funcoes: unknown }
  return { ...profile, funcoes: normalizeRoles(profile.funcoes) }
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
  const { data } = await api.get<unknown>('/api/v1/auth/me')
  return normalizeMeProfile(data)
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
