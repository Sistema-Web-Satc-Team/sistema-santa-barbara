import {
    cancelarVinculo, criarVinculo, encerrarVinculo, fetchMemberVinculos,
    type NovoVinculoRequest,
} from '#/api/member-vinculos'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'

export function useMemberVinculos(idMembro: string, enabled = true) {
  const qc = useQueryClient()
  const queryKey = ['member-vinculos', idMembro]

  const invalidate = async () => {
    await qc.invalidateQueries({ queryKey })
    await qc.invalidateQueries({ queryKey: ['members'] })
  }

  const query = useQuery({ queryKey, queryFn: () => fetchMemberVinculos(idMembro), enabled })

  const criar = useMutation({
    mutationFn: (req: NovoVinculoRequest) => criarVinculo(idMembro, req),
    onSuccess: invalidate,
  })
  const encerrar = useMutation({
    mutationFn: (idVinculo: number) => encerrarVinculo(idMembro, idVinculo),
    onSuccess: invalidate,
  })
  const cancelar = useMutation({
    mutationFn: (idVinculo: number) => cancelarVinculo(idMembro, idVinculo),
    onSuccess: invalidate,
  })

  return { query, criar, encerrar, cancelar }
}