import type { MeProfileResponse } from '#/api/auth'
import { api } from '#/lib/axios'
import { createIsomorphicFn } from '@tanstack/react-start'
import { getRequestHeader } from '@tanstack/react-start/server'

export const fetchProfile = createIsomorphicFn()
  .server(async (): Promise<MeProfileResponse | null> => {
    const cookie = getRequestHeader('cookie')
    if (!cookie) return null
    try {
      const res = await fetch(`${api.defaults.baseURL}/api/v1/auth/me`, {
        headers: { cookie },
      })
      if (!res.ok) return null
      return (await res.json()) as MeProfileResponse
    } catch {
      return null
    }
  })
  .client(async (): Promise<MeProfileResponse | null> => {
    try {
      const { data } = await api.get('/api/v1/auth/me')
      return data
    } catch {
      return null
    }
  })