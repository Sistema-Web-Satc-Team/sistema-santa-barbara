import { fetchAlunos, type Aluno, type AlunosFilterRequest } from '#/api/alunos'
import { useInfiniteQuery, useQueryClient } from '@tanstack/react-query'
import { useCallback, useMemo } from 'react'
import { useFilter } from './use-filter'

const PAGE_SIZE = 12

export interface ListAlunosProps {
  alunos: Aluno[]
  totalElements: number
  filters: Partial<AlunosFilterRequest>
  state: {
    isLoading: boolean
    isError: boolean
    isEmpty: boolean
    hasNextPage: boolean
    isFetchingNextPage: boolean
  }
  actions: {
    clearFilters: () => void
    setFilter: <K extends keyof AlunosFilterRequest>(
      key: K,
      value: AlunosFilterRequest[K],
    ) => void
    loadMore: () => void
    refresh: () => Promise<void>
  }
}

export function useAlunos(): ListAlunosProps {
  const { filters, setFilter, clearFilters } = useFilter<AlunosFilterRequest>({
    apenasMenores: false,
    instrumentosAprendendo: [],
  })

  const queryClient = useQueryClient()

  const query = useInfiniteQuery({
    queryKey: [
      'alunos-list',
      filters.search,
      filters.instrumentosAprendendo,
      filters.apenasMenores,
    ],
    initialPageParam: 0,
    queryFn: ({ pageParam }) =>
      fetchAlunos({
        ...filters,
        page: pageParam,
        size: PAGE_SIZE,
      }),
    getNextPageParam: (lastPage) =>
      lastPage.page + 1 < lastPage.totalPages ? lastPage.page + 1 : undefined,
    staleTime: 1000 * 60 * 5,
    retry: false,
    refetchOnWindowFocus: false,
  })

  const { fetchNextPage } = query

  const alunos = useMemo(
    () => query.data?.pages.flatMap((page) => page.content) ?? [],
    [query.data],
  )

  const totalElements = query.data?.pages.at(0)?.totalElements ?? 0

  const loadMore = useCallback(() => {
    void fetchNextPage()
  }, [fetchNextPage])

  async function refresh() {
    await queryClient.invalidateQueries({ queryKey: ['alunos-list'] })
  }

  const isEmpty = !query.isLoading && alunos.length === 0

  return {
    alunos,
    totalElements,
    filters,
    state: {
      isLoading: query.isLoading,
      isError: query.isError,
      isEmpty,
      hasNextPage: query.hasNextPage,
      isFetchingNextPage: query.isFetchingNextPage,
    },
    actions: {
      setFilter,
      clearFilters,
      loadMore,
      refresh,
    },
  }
}
