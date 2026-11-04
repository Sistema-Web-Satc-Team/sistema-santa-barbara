import {
  fetchProfessores,
  type Professor,
  type ProfessoresFilterRequest,
} from '#/api/professores'
import { useInfiniteQuery, useQueryClient } from '@tanstack/react-query'
import { useCallback, useMemo } from 'react'
import { useFilter } from './use-filter'

const PAGE_SIZE = 12

export interface ListProfessoresProps {
  professores: Professor[]
  totalElements: number
  filters: Partial<ProfessoresFilterRequest>
  state: {
    isLoading: boolean
    isError: boolean
    isEmpty: boolean
    hasNextPage: boolean
    isFetchingNextPage: boolean
  }
  actions: {
    clearFilters: () => void
    setFilter: <K extends keyof ProfessoresFilterRequest>(
      key: K,
      value: ProfessoresFilterRequest[K],
    ) => void
    loadMore: () => void
    refresh: () => Promise<void>
  }
}

export function useProfessores(): ListProfessoresProps {
  const { filters, setFilter, clearFilters } =
    useFilter<ProfessoresFilterRequest>({
      instrumentosEnsinando: [],
    })

  const queryClient = useQueryClient()

  const query = useInfiniteQuery({
    queryKey: [
      'professores-list',
      filters.search,
      filters.instrumentosEnsinando,
    ],
    initialPageParam: 0,
    queryFn: ({ pageParam }) =>
      fetchProfessores({
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

  const professores = useMemo(
    () => query.data?.pages.flatMap((page) => page.content) ?? [],
    [query.data],
  )

  const totalElements = query.data?.pages.at(0)?.totalElements ?? 0

  const loadMore = useCallback(() => {
    void fetchNextPage()
  }, [fetchNextPage])

  async function refresh() {
    await queryClient.invalidateQueries({ queryKey: ['professores-list'] })
  }

  const isEmpty = !query.isLoading && professores.length === 0

  return {
    professores,
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
