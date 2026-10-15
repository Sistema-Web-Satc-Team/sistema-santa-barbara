import { fetchMemberResponsaveis, removeMemberResponsavel, saveMemberResponsavel } from '#/api/member-responsaveis'
import type { RelacaoValue } from '#/lib/relacao-responsavel'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'

export function useMemberResponsaveis(idMembro: string, enabled = true) {
  const qc = useQueryClient()
  const queryKey = ['member-responsaveis', idMembro]
  const invalidate = () => qc.invalidateQueries({ queryKey })

  const query = useQuery({
    queryKey,
    queryFn: () => fetchMemberResponsaveis(idMembro),
    enabled,
  })

  const save = useMutation({
    mutationFn: (v: { idResponsavel: string; grauRelacao: RelacaoValue }) =>
      saveMemberResponsavel(idMembro, v.idResponsavel, v.grauRelacao),
    onSuccess: invalidate,
  })

  const remove = useMutation({
    mutationFn: (idResponsavel: string) => removeMemberResponsavel(idMembro, idResponsavel),
    onSuccess: invalidate,
  })

  return { query, save, remove }
}