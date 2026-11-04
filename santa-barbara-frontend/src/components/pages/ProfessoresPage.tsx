import { ProfessoresGrid } from '#/components/business/professores/professor-card'
import { ProfessoresFilters } from '#/components/business/professores/professores-filters'
import { Spinner } from '#/components/ui/spinner'
import { useInfiniteScroll } from '#/hooks/use-infinite-scroll'
import { useProfessores } from '#/hooks/use-professores'
import { uniqueText } from '#/lib/text'

export function ProfessoresPage() {
  const { professores, totalElements, filters, state, actions } =
    useProfessores()

  const sentinel = useInfiniteScroll({
    onLoadMore: actions.loadMore,
    enabled: state.hasNextPage && !state.isFetchingNextPage,
    itemCount: professores.length,
  })

  if (state.isLoading) {
    return (
      <div className="w-full flex items-center justify-center min-h-screen">
        <Spinner />
      </div>
    )
  }

  const knownInstruments = uniqueText(
    professores.flatMap((prof) => prof.instrumentos?.ensinando ?? []),
  )

  const hasFilters =
    Boolean(filters.search?.trim()) ||
    Boolean(filters.instrumentosEnsinando?.length)

  return (
    <>
      <div className="flex flex-row items-start mb-8">
        <div className="mr-auto">
          <h2 className="text-3xl font-bold">Professores da Banda</h2>
          {!state.isError && (
            <p className="text-sm text-muted-foreground mt-1">
              {totalElements === 1
                ? '1 professor encontrado'
                : `${totalElements} professores encontrados`}
            </p>
          )}
        </div>
      </div>

      <ProfessoresFilters
        search={filters.search}
        selectedInstruments={filters.instrumentosEnsinando ?? []}
        knownInstruments={knownInstruments}
        onSearchChange={(value) => {
          actions.setFilter('search', value || undefined)
        }}
        onInstrumentsChange={(instruments) => {
          actions.setFilter('instrumentosEnsinando', instruments)
        }}
        onClearFilters={() => {
          actions.setFilter('search', undefined)
          actions.setFilter('instrumentosEnsinando', [])
        }}
      />

      {state.isError ? (
        <div className="w-full flex items-center justify-center p-12 text-destructive">
          Não foi possível carregar a listagem de professores.
        </div>
      ) : (
        <ProfessoresGrid professores={professores} hasFilters={hasFilters} />
      )}

      <div ref={sentinel} className="flex h-16 items-center justify-center">
        {state.isFetchingNextPage && <Spinner />}
      </div>
    </>
  )
}
