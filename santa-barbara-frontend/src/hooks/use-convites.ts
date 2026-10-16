import { aceitarConvite, criarConvite, fetchConvites, reenviarConvite, type AceitarConviteRequest, type ConvitesFilter } from '#/api/convites'
import type { CanalValue } from '#/components/business/convites/convites'

import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query'

export function useConvites(filter: ConvitesFilter) {
  const qc = useQueryClient()

  const invalidate = async () => {
    await qc.invalidateQueries({ queryKey: ['convites'] })
    await qc.invalidateQueries({ queryKey: ['members'] })
  }

  const query = useQuery({
    queryKey: ['convites', filter],
    queryFn: () => fetchConvites(filter),
    placeholderData: keepPreviousData,
  })

  const criar = useMutation({ mutationFn: criarConvite, onSuccess: invalidate })

  const reenviar = useMutation({
    mutationFn: (v: { idConvite: string; canal: CanalValue }) => reenviarConvite(v.idConvite, v.canal),
    onSuccess: invalidate,
  })

  return { query, criar, reenviar }
}

export function useAceitarConvite() {
  return useMutation({
    mutationFn: (request: AceitarConviteRequest) =>
      aceitarConvite(request),
  });
}