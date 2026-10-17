import { fetchMusicas } from '#/api/musica'
import { useInfiniteQuery } from '@tanstack/react-query'
import { useCallback, useMemo } from 'react'

const PAGE_SIZE = 20

export function useListMusicas(search: string) {
  const query = useInfiniteQuery({
    queryKey: ['musicas-list', search],
    initialPageParam: 0,
    queryFn: ({ pageParam }) => fetchMusicas({ search, page: pageParam, size: PAGE_SIZE }),
    getNextPageParam: (lastPage) =>
      lastPage.page + 1 < lastPage.totalPages ? lastPage.page + 1 : undefined,

    staleTime: 1000 * 60 * 5,
    retry: false,
    refetchOnWindowFocus: false,
  })

  const { fetchNextPage } = query

  const musicas = useMemo(
    () => query.data?.pages.flatMap((page) => page.content) ?? [],
    [query.data],
  )

  const loadMore = useCallback(() => {
    void fetchNextPage()
  }, [fetchNextPage])

  return {
    musicas,
    totalElements: query.data?.pages.at(0)?.totalElements ?? 0,
    state: {
      isLoading: query.isLoading,
      isError: query.isError,
      hasNextPage: query.hasNextPage,
      isFetchingNextPage: query.isFetchingNextPage,
    },
    actions: { loadMore },
  }
}
