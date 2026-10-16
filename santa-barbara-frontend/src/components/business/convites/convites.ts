import type { Convite } from "#/api/convites"


export const CANAIS = [
  { value: 'EMAIL', label: 'E-mail' },
] as const

export type CanalValue = (typeof CANAIS)[number]['value']
export const canalValido = (v: string): boolean => CANAIS.some((c) => c.value === v)

export const STATUS_FILTROS = [
  { value: 'ENVIADO', label: 'Enviados' },
  { value: 'ACEITO', label: 'Aceitos' },
  { value: 'EXPIRADO', label: 'Expirados' },
] as const

export type StatusFiltro = (typeof STATUS_FILTROS)[number]['value']

export const STATUS_META: Record<string, { label: string; variant: 'info' | 'success' | 'muted' | 'warning' | 'danger' }> = {
  ENVIADO: { label: 'Enviado', variant: 'info' },
  ACEITO: { label: 'Aceito', variant: 'success' },
  EXPIRADO: { label: 'Expirado', variant: 'muted' },
}

export function statusEfetivo(c: Pick<Convite, 'status' | 'expiraEm'>) {
  if (c.status === 'ENVIADO' && new Date(c.expiraEm) < new Date()) return 'EXPIRADO'
  return c.status
}

export const formatarDataHora = (iso: string) =>
  new Date(iso).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' })