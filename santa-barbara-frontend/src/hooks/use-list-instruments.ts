import { fetchInstrumentos } from '#/api/instrumento'
import type { InstrumentoFilterRequest } from '#/api/instrumento'
import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { useFilter } from './use-filter'

export function useListInstruments() {
  const { filters, setFilter, clearFilters } = useFilter<InstrumentoFilterRequest>({
    page: 0,
    size: 12,
  })

  const { data, isLoading } = useQuery({
    queryKey: ['instruments-list', filters],
    queryFn: () => fetchInstrumentos(filters),
    placeholderData: keepPreviousData,

    staleTime: 1000 * 60 * 5,
    retry: false,
    refetchOnWindowFocus: false,
  })

  const isEmpty = !isLoading && (!data?.content || data.content.length === 0)

  return {
    data,
    filters,
    state: { isLoading, isEmpty },
    actions: { setFilter, clearFilters },
  }
}
