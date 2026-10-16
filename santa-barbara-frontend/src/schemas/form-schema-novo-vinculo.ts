import { z } from 'zod';

function dataExiste({ dia, mes, ano }: { dia: string; mes: string; ano: string }) {
  if (!/^\d{2}$/.test(dia) || !/^\d{2}$/.test(mes) || !/^\d{4}$/.test(ano)) return false
  const d = new Date(Number(ano), Number(mes) - 1, Number(dia))
  return (
    d.getFullYear() === Number(ano) &&
    d.getMonth() === Number(mes) - 1 &&
    d.getDate() === Number(dia)
  )
}

export const formNovoVinculoSchema = z.object({
  funcao: z.string().min(1, 'Selecione a função.'),
  dataInicio: z
    .object({ dia: z.string(), mes: z.string(), ano: z.string() })
    .refine(dataExiste, 'Informe uma data de início válida.'),
})

export type NovoVinculoValues = z.infer<typeof formNovoVinculoSchema>