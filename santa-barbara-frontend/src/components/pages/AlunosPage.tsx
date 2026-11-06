import { AlunosGrid } from '#/components/business/alunos/aluno-card'
import { AlunosFilters } from '#/components/business/alunos/alunos-filters'
import { Spinner } from '#/components/ui/spinner'
import { useAlunos } from '#/hooks/use-alunos'
import { useInfiniteScroll } from '#/hooks/use-infinite-scroll'
import { uniqueText } from '#/lib/text'

export function AlunosPage() {
  const { alunos, totalElements, filters, state, actions } = useAlunos()

  const sentinel = useInfiniteScroll({
    onLoadMore: actions.loadMore,
    enabled: state.hasNextPage && !state.isFetchingNextPage,
    itemCount: alunos.length,
  })

  if (state.isLoading) {
    return (
      <div className="w-full flex items-center justify-center min-h-screen">
        <Spinner />
      </div>
    )
  }

  const knownInstruments = uniqueText(
    alunos.flatMap((aluno) => aluno.instrumentos?.aprendendo ?? []),
  )

  const hasFilters =
    Boolean(filters.search?.trim()) ||
    Boolean(filters.instrumentosAprendendo?.length) ||
    Boolean(filters.apenasMenores)

  return (
    <>
      <div className="flex flex-row items-start mb-8">
        <div className="mr-auto">
          <h2 className="text-3xl font-bold">Alunos da Banda</h2>
          {!state.isError && (
            <p className="text-sm text-muted-foreground mt-1">
              {totalElements === 1
                ? '1 aluno encontrado'
                : `${totalElements} alunos encontrados`}
            </p>
          )}
        </div>
      </div>

      <AlunosFilters
        search={filters.search}
        selectedInstruments={filters.instrumentosAprendendo ?? []}
        apenasMenores={filters.apenasMenores ?? false}
        knownInstruments={knownInstruments}
        onSearchChange={(value) => {
          actions.setFilter('search', value || undefined)
        }}
        onInstrumentsChange={(instruments) => {
          actions.setFilter('instrumentosAprendendo', instruments)
        }}
        onApenasMenoresChange={(checked) => {
          actions.setFilter('apenasMenores', checked)
        }}
        onClearFilters={() => {
          actions.setFilter('search', undefined)
          actions.setFilter('instrumentosAprendendo', [])
          actions.setFilter('apenasMenores', false)
        }}
      />

      {state.isError ? (
        <div className="w-full flex items-center justify-center p-12 text-destructive">
          Não foi possível carregar a listagem de alunos.
        </div>
      ) : (
        <AlunosGrid alunos={alunos} hasFilters={hasFilters} />
      )}

      <div ref={sentinel} className="flex h-16 items-center justify-center">
        {state.isFetchingNextPage && <Spinner />}
      </div>
    </>
  )
}
