export interface DataParts { dia: string; mes: string; ano: string }

export function exibirData(iso: string) {
  const [ano, mes, dia] = iso.split('-')
  return `${dia}/${mes}/${ano}`
}

export function hojeParts(): DataParts {
  const d = new Date()
  return {
    dia: String(d.getDate()).padStart(2, '0'),
    mes: String(d.getMonth() + 1).padStart(2, '0'),
    ano: String(d.getFullYear()),
  }
}

export const partsParaISO = ({ dia, mes, ano }: DataParts) => `${ano}-${mes}-${dia}`

function mesesEntre(inicioISO: string, fimISO?: string | null) {
  const [y1, m1, d1] = inicioISO.split('-').map(Number)
  const fim = fimISO ? fimISO.split('-').map(Number) : (() => {
    const h = new Date()
    return [h.getFullYear(), h.getMonth() + 1, h.getDate()]
  })()
  const [y2, m2, d2] = fim
  let total = (y2 - y1) * 12 + (m2 - m1)
  if (d2 < d1) total--
  return Math.max(total, 0)
}

export function duracao(inicioISO: string, fimISO?: string | null) {
  const total = mesesEntre(inicioISO, fimISO)
  if (total < 1) return 'menos de 1 mês'
  const anos = Math.floor(total / 12)
  const meses = total % 12
  const partes: string[] = []
  if (anos) partes.push(`${anos} ${anos === 1 ? 'ano' : 'anos'}`)
  if (meses) partes.push(`${meses} ${meses === 1 ? 'mês' : 'meses'}`)
  return partes.join(' e ')
}