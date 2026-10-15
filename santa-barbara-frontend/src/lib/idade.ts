export interface DataNascimentoValue {
  dia: string
  mes: string
  ano: string
}

function toDate({ dia, mes, ano }: DataNascimentoValue) {
  if (!/^\d{2}$/.test(dia) || !/^\d{2}$/.test(mes) || !/^\d{4}$/.test(ano)) return null
  const d = new Date(`${ano}-${mes}-${dia}T00:00:00`)
  return Number.isNaN(d.getTime()) ? null : d
}

export function dataNascimentoValida(data: DataNascimentoValue) {
  const d = toDate(data)
  return d !== null && d < new Date()
}

export function calcularIdade(data: DataNascimentoValue) {
  const nasc = toDate(data)
  if (!nasc) return null
  const hoje = new Date()
  let idade = hoje.getFullYear() - nasc.getFullYear()
  const fez =
    hoje.getMonth() > nasc.getMonth() ||
    (hoje.getMonth() === nasc.getMonth() && hoje.getDate() >= nasc.getDate())
  return fez ? idade : idade - 1
}

export function ehMenor(data: DataNascimentoValue) {
  const idade = calcularIdade(data)
  return idade !== null && idade < 18
}