import { fetchAlunos, type Aluno, type AlunosFilterRequest } from '#/api/alunos'
import { keepPreviousData, useQuery, useQueryClient } from '@tanstack/react-query'
import { useFilter } from './use-filter'

export interface ListAlunosProps {
  filters: Partial<AlunosFilterRequest>
  state: {
    alunos: PageDefaultResponse<Aluno> | undefined
    isLoading: boolean
    isError: boolean
    isEmpty: boolean
  }
  actions: {
    clearFilters: () => void
    setFilter: <K extends keyof AlunosFilterRequest>(
      key: K,
      value: AlunosFilterRequest[K],
    ) => void
    refresh: () => Promise<void>
  }
}

export function useAlunos(): ListAlunosProps {
  const { filters, setFilter, clearFilters } = useFilter<AlunosFilterRequest>({
    page: 0,
    size: 10,
    apenasMenores: false,
    instrumentosAprendendo: [],
  })

  const queryClient = useQueryClient()

  const { data, isLoading, isError } = useQuery({
    queryKey: ['alunos-list', filters],
    queryFn: () => fetchAlunos(filters),
    placeholderData: keepPreviousData,
    staleTime: 1000 * 60 * 5,
    retry: false,
    refetchOnWindowFocus: false,
  })

  async function refresh() {
    await queryClient.invalidateQueries({ queryKey: ['alunos-list'] })
  }

  const isEmpty = !isLoading && (!data?.content || data.content.length === 0)

  return {
    filters,
    state: {
      alunos: data,
      isLoading,
      isError,
      isEmpty,
    },
    actions: {
      setFilter,
      clearFilters,
      refresh,
    },
  }
}
