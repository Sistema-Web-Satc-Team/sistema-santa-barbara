import { AlunosGrid } from '#/components/business/alunos/aluno-card'
import { AlunosFilters } from '#/components/business/alunos/alunos-filters'
import { DataPagination } from '#/components/business/data-pagination'
import { Spinner } from '#/components/ui/spinner'
import { useAlunos } from '#/hooks/use-alunos'
import { uniqueText } from '#/lib/text'

export function AlunosPage() {
  const { filters, state, actions } = useAlunos()

  if (state.isLoading) {
    return (
      <div className="w-full flex items-center justify-center min-h-screen">
        <Spinner />
      </div>
    )
  }

  const alunos = state.alunos?.content ?? []
  const totalElements = state.alunos?.totalElements ?? 0

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
          actions.setFilter('page', 0)
        }}
        onInstrumentsChange={(instruments) => {
          actions.setFilter('instrumentosAprendendo', instruments)
          actions.setFilter('page', 0)
        }}
        onApenasMenoresChange={(checked) => {
          actions.setFilter('apenasMenores', checked)
          actions.setFilter('page', 0)
        }}
        onClearFilters={() => {
          actions.setFilter('search', undefined)
          actions.setFilter('instrumentosAprendendo', [])
          actions.setFilter('apenasMenores', false)
          actions.setFilter('page', 0)
        }}
      />

      {state.isError ? (
        <div className="w-full flex items-center justify-center p-12 text-destructive">
          Não foi possível carregar a listagem de alunos.
        </div>
      ) : (
        <AlunosGrid alunos={alunos} hasFilters={hasFilters} />
      )}

      {!state.isError && !state.isEmpty && (
        <DataPagination
          pagesSizes={[6, 9, 12, 24, 48]}
          pagination={{
            page: state.alunos?.page ?? 0,
            size: state.alunos?.size ?? 10,
            totalElements: state.alunos?.totalElements ?? 0,
            totalPages: state.alunos?.totalPages ?? 1,
          }}
          onPageChange={(value: number) => actions.setFilter('page', value)}
          onSizeChange={(value: number) => {
            actions.setFilter('size', value)
            actions.setFilter('page', 0)
          }}
        />
      )}
    </>
  )
}
