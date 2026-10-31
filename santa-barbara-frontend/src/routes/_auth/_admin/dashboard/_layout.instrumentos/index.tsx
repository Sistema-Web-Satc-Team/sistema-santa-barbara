import type { Instrumento, InstrumentoInput } from '#/api/instrumento'
import { INSTRUMENT_TYPES, normalizeInstrumentType } from '#/api/instrumento'
import { InstrumentCard } from '#/components/business/instrument-card'
import { InstrumentDeleteDialog } from '#/components/business/instrument-delete-dialog'
import { InstrumentFormDialog } from '#/components/business/instrument-form-dialog'
import { Button } from '#/components/ui/button'
import { ButtonGroup } from '#/components/ui/button-group'
import { Field, FieldLabel } from '#/components/ui/field'
import { Input } from '#/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '#/components/ui/select'
import { Spinner } from '#/components/ui/spinner'
import { useAuth } from '#/hooks/use-auth'
import { useInstrumentMutations } from '#/hooks/use-instrument-mutations'
import { useListInstruments } from '#/hooks/use-list-instruments'
import { createFileRoute } from '@tanstack/react-router'
import { PencilIcon, PlusIcon, Trash2Icon } from 'lucide-react'
import { useRef, useState } from 'react'

export const Route = createFileRoute('/_auth/_admin/dashboard/_layout/instrumentos/')({
  component: Instrumentos,
})

const MANAGER_ROLES = ['SUPER_ADMIN', 'ADMIN', 'MAESTRO']

function Instrumentos() {
  const { state: authState } = useAuth()
  const { data, filters, state, actions } = useListInstruments()
  const { create, update, remove } = useInstrumentMutations()

  const [formOpen, setFormOpen] = useState(false)
  const [editing, setEditing] = useState<Instrumento | null>(null)
  const [deleting, setDeleting] = useState<Instrumento | null>(null)
  const searchTermRef = useRef('')

  const canManage =
    authState.profile?.funcoes.some((role) => MANAGER_ROLES.includes(role)) ??
    false

  const page = filters.page ?? 0
  const totalPages = data?.totalPages ?? 1
  const hasFilters = Boolean(filters.nome) || Boolean(filters.tipo)

  function openCreate() {
    setEditing(null)
    setFormOpen(true)
  }

  function openEdit(instrumento: Instrumento) {
    setEditing(instrumento)
    setFormOpen(true)
  }

  function handleSubmit(values: InstrumentoInput): Promise<void> {
    if (!editing) return create.mutateAsync(values)

    const unchanged =
      values.nome === editing.nome &&
      values.descricao === (editing.descricao ?? '') &&
      values.tipo === normalizeInstrumentType(editing.tipo)
    if (unchanged) return Promise.resolve()

    return update.mutateAsync({ id: editing.id, ...values })
  }

  async function handleDelete() {
    if (!deleting) return
    try {
      await remove.mutateAsync(deleting.id)
      setDeleting(null)
    } catch {}
  }

  function search() {
    actions.setFilter('nome', searchTermRef.current)
    actions.setFilter('page', 0)
  }

  if (!canManage) {
    return (
      <div className="w-full flex items-center justify-center min-h-screen text-destructive text-xl">
        Você não tem permissão para gerenciar instrumentos.
      </div>
    )
  }

  return (
    <>
      <div className="flex flex-row mb-8">
        <h2 className="text-3xl font-bold mr-auto">Instrumentos</h2>
        <Button onClick={openCreate}>
          <PlusIcon />
          Novo instrumento
        </Button>
      </div>

      <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-end">
        <Field className="sm:max-w-md">
          <FieldLabel htmlFor="instrument-search">Buscar</FieldLabel>
          <ButtonGroup>
            <Input
              id="instrument-search"
              placeholder="Busque pelo nome do instrumento..."
              onChange={(e) => {
                searchTermRef.current = e.target.value
              }}
              onKeyDown={(e) => {
                if (e.key === 'Enter') search()
              }}
            />
            <Button variant="outline" onClick={search}>
              Buscar
            </Button>
          </ButtonGroup>
        </Field>

        <Field className="sm:w-48">
          <FieldLabel htmlFor="instrument-type-filter">Tipo</FieldLabel>
          <Select
            value={filters.tipo ?? 'todos'}
            onValueChange={(value) => {
              actions.setFilter('tipo', value === 'todos' ? undefined : value)
              actions.setFilter('page', 0)
            }}
          >
            <SelectTrigger id="instrument-type-filter" className="w-full">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="todos">Todos</SelectItem>
              {INSTRUMENT_TYPES.map((type) => (
                <SelectItem key={type.value} value={type.value}>
                  {type.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </Field>
      </div>

      {state.isLoading ? (
        <div className="w-full flex items-center justify-center py-24">
          <Spinner />
        </div>
      ) : state.isEmpty ? (
        <div className="w-full flex items-center justify-center py-24 text-muted-foreground text-lg">
          {hasFilters
            ? 'Nenhum instrumento encontrado com esses filtros.'
            : 'Nenhum instrumento cadastrado!'}
        </div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
          {data?.content.map((instrumento) => (
            <InstrumentCard
              key={instrumento.id}
              instrumento={instrumento}
              actions={
                <>
                  <Button
                    type="button"
                    variant="ghost"
                    size="icon"
                    aria-label={`Editar ${instrumento.nome}`}
                    onClick={() => openEdit(instrumento)}
                  >
                    <PencilIcon />
                  </Button>
                  <Button
                    type="button"
                    variant="ghost"
                    size="icon"
                    className="text-destructive hover:text-destructive"
                    aria-label={`Remover ${instrumento.nome}`}
                    onClick={() => setDeleting(instrumento)}
                  >
                    <Trash2Icon />
                  </Button>
                </>
              }
            />
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <div className="mt-6 flex items-center justify-center gap-4">
          <Button
            variant="outline"
            size="sm"
            disabled={page <= 0}
            onClick={() => actions.setFilter('page', page - 1)}
          >
            Anterior
          </Button>
          <span className="text-sm text-muted-foreground">
            Página {page + 1} de {totalPages}
          </span>
          <Button
            variant="outline"
            size="sm"
            disabled={page + 1 >= totalPages}
            onClick={() => actions.setFilter('page', page + 1)}
          >
            Próxima
          </Button>
        </div>
      )}

      <InstrumentFormDialog
        key={editing?.id ?? 'novo'}
        open={formOpen}
        onOpenChange={setFormOpen}
        instrumento={editing}
        onSubmit={handleSubmit}
      />

      <InstrumentDeleteDialog
        instrumento={deleting}
        isDeleting={remove.isPending}
        onCancel={() => setDeleting(null)}
        onConfirm={() => void handleDelete()}
      />
    </>
  )
}
