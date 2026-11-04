import axios from 'axios'
import type { CreateAxiosDefaults } from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const options: CreateAxiosDefaults = {
  baseURL,
  withCredentials: true,
  xsrfCookieName: '',
}

const csrfClient = axios.create(options)

export const api = axios.create(options)

interface CsrfResponse {
  headerName: string
  token: string
}

let pendingCsrf: Promise<string> | null = null

async function getCsrfToken(): Promise<string> {
  if (!pendingCsrf) {
    pendingCsrf = csrfClient
      .get<CsrfResponse>('/api/v1/csrf')
      .then(({ data }) => {
        if (
          data.headerName !== 'X-XSRF-TOKEN' ||
          typeof data.token !== 'string' ||
          !data.token
        ) {
          throw new Error('Resposta inválida do endpoint CSRF')
        }

        return data.token
      })
      .finally(() => {
        pendingCsrf = null
      })
  }

  return pendingCsrf
}

api.interceptors.request.use(async (config) => {
  const method = config.method?.toLowerCase()
  if (method && !['get', 'head', 'options'].includes(method)) {
    config.headers.set('X-XSRF-TOKEN', await getCsrfToken())
  }

  return config
})