export const RELACOES = [
  { value: 'PROGENITORES', label: 'Pai/Mãe' },
  { value: 'AVO', label: 'Avô/Avó' },
  { value: 'TIO', label: 'Tio/Tia' },
  { value: 'PADRINHO', label: 'Padrinho/Madrinha' },
  { value: 'RESPONSAVEL_LEGAL', label: 'Responsável legal' },
] as const

export type RelacaoValue = (typeof RELACOES)[number]['value']
export const relacaoValida = (v: string): boolean => RELACOES.some((r) => r.value === v)


const porLabel: Record<string, RelacaoValue> = {
  'responsável legal': 'RESPONSAVEL_LEGAL',
  'pai/mãe': 'PROGENITORES',
  progenitores: 'PROGENITORES',
  avô: 'AVO',
  avó: 'AVO',
  'avô/avó': 'AVO',
  tio: 'TIO',
  tia: 'TIO',
  'tio/tia': 'TIO',
  padrinho: 'PADRINHO',
  madrinha: 'PADRINHO',
  'padrinho/madrinha': 'PADRINHO',
}
export const relacaoDeLabel = (label: string): RelacaoValue | '' =>
  porLabel[label.trim().toLowerCase()] ?? ''