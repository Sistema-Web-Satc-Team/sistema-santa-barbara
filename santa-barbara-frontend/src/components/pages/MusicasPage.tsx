import type { Musica, MusicaInput } from '#/api/musica'
import { matchesGenres } from '#/api/musica'
import { GenreMultiSelect } from '#/components/business/genre-multi-select'
import { MusicCreateDialog } from '#/components/business/music-create-dialog'
import { MusicDeleteDialog } from '#/components/business/music-delete-dialog'
import { MusicDetailsDialog } from '#/components/business/music-details-dialog'
import { MusicTable } from '#/components/business/music-table'
import { Button } from '#/components/ui/button'
import { Field, FieldLabel } from '#/components/ui/field'
import { Input } from '#/components/ui/input'
import { Spinner } from '#/components/ui/spinner'
import { useAuth } from '#/hooks/use-auth'
import { useInfiniteScroll } from '#/hooks/use-infinite-scroll'
import { useListMusicas } from '#/hooks/use-list-musicas'
import { useMusicaMutations } from '#/hooks/use-musica-mutations'
import { uniqueText } from '#/lib/text'
import { PlusIcon } from 'lucide-react'
import { useEffect, useState } from 'react'


const EDITOR_ROLES = ['SUPER_ADMIN', 'ADMIN', 'MAESTRO']
const DELETE_ROLES = ['SUPER_ADMIN', 'ADMIN','MAESTRO']
const CREATE_ROLES = ['SUPER_ADMIN', 'ADMIN','MAESTRO']

interface MusicasPageProps {
  permitirRemocao?: boolean
}

export function MusicasPage({ permitirRemocao = false }: MusicasPageProps) {
    const { state: authState } = useAuth()

  const roles = authState.profile?.funcoes ?? []
  const canEdit = roles.some((role) => EDITOR_ROLES.includes(role))
  const canDelete = permitirRemocao && roles.some((role) => DELETE_ROLES.includes(role))
  const canCreate = roles.some((role) => CREATE_ROLES.includes(role))


  const [searchInput, setSearchInput] = useState('')
  const [search, setSearch] = useState('')
  const [selectedGenres, setSelectedGenres] = useState<string[]>([])
  const [editing, setEditing] = useState<Musica | null>(null)
  const [deleting, setDeleting] = useState<Musica | null>(null)
  const [creating, setCreating] = useState(false)


  useEffect(() => {
    const timer = setTimeout(() => setSearch(searchInput.trim()), 350)
    return () => clearTimeout(timer)
  }, [searchInput])

  const { musicas, totalElements, state, actions } = useListMusicas(search)
 const { create, update, remove } = useMusicaMutations()

  const sentinel = useInfiniteScroll({
    onLoadMore: actions.loadMore,
    enabled: state.hasNextPage && !state.isFetchingNextPage,
    itemCount: musicas.length,
  })

  const visibleMusicas = musicas.filter((musica) => matchesGenres(musica, selectedGenres))
  const knownGenres = uniqueText(musicas.flatMap((musica) => musica.generos))
  const hasFilters = search !== '' || selectedGenres.length > 0

  function handleCreate(values: MusicaInput): Promise<void> {
    return create.mutateAsync(values)
  }


  function handleUpdate(values: MusicaInput): Promise<void> {
    if (!editing) return Promise.resolve()
    return update.mutateAsync({ id: editing.id, ...values })
  }

  async function handleDelete() {
    if (!deleting) return
    try {
      await remove.mutateAsync(deleting.id)
      setDeleting(null)
    } catch {

    }
  }

  return (
    <>
      <div className="mb-8 flex flex-row items-start">
        <div className="mr-auto">
          <h2 className="text-3xl font-bold">Músicas</h2>
          {!state.isLoading && !state.isError && (
            <p className="text-muted-foreground">
              {totalElements === 1 ? '1 música no acervo' : `${totalElements} músicas no acervo`}
            </p>
          )}
        </div>
        {canCreate && (
          <Button onClick={() => setCreating(true)}>
            <PlusIcon />
            Adicionar música
          </Button>
        )}
     </div>


      <div className="mb-8 flex flex-col gap-4 sm:flex-row sm:items-end">
        <Field className="min-w-0 flex-1">
          <FieldLabel htmlFor="music-search">Buscar</FieldLabel>
          <Input
            id="music-search"
            placeholder="Busque por música ou compositor..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
          />
        </Field>

        <Field className="sm:w-64">
          <FieldLabel htmlFor="music-genres">Gêneros</FieldLabel>
          <GenreMultiSelect
            id="music-genres"
            value={selectedGenres}
            onChange={setSelectedGenres}
            options={knownGenres}
            placeholder="Todos os gêneros"
          />
        </Field>
      </div>

      {state.isLoading ? (
        <div className="w-full flex items-center justify-center py-24">
          <Spinner />
        </div>
      ) : state.isError ? (
        <div className="w-full flex items-center justify-center py-24 text-destructive text-lg">
          Não foi possível carregar as músicas.
        </div>
      ) : visibleMusicas.length === 0 && !state.hasNextPage ? (
        <div className="w-full flex items-center justify-center py-24 text-muted-foreground text-lg">
          {hasFilters
            ? 'Nenhuma música encontrada com esses filtros.'
            : 'Não existem músicas cadastradas.'}
        </div>
      ) : (
        <MusicTable
          musicas={visibleMusicas}
          canEdit={canEdit}
          canDelete={canDelete}
          onOpen={setEditing}
          onDelete={setDeleting}
        />
      )}


      <div ref={sentinel} className="flex h-16 items-center justify-center">
        {state.isFetchingNextPage && <Spinner />}
      </div>
      <MusicCreateDialog open={creating} onOpenChange={setCreating} onSubmit={handleCreate} />

      <MusicDetailsDialog
        musica={editing}
        onClose={() => setEditing(null)}
        onSubmit={handleUpdate}
      />

      <MusicDeleteDialog
        musica={deleting}
        isDeleting={remove.isPending}
        onCancel={() => setDeleting(null)}
        onConfirm={() => void handleDelete()}
      />
    </>
  )
}