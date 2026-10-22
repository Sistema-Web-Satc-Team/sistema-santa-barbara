import { fetchInstrumentos } from '#/api/instrumento'
import { Badge } from '#/components/ui/badge'
import { Button } from '#/components/ui/button'
import { ButtonGroup } from '#/components/ui/button-group'
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '#/components/ui/command'
import { Field, FieldLabel } from '#/components/ui/field'
import { Input } from '#/components/ui/input'
import { Popover, PopoverContent, PopoverTrigger } from '#/components/ui/popover'
import { Switch } from '#/components/ui/switch'
import { normalizeText, uniqueText } from '#/lib/text'
import { capitalizeAll } from '#/lib/utils'
import { useQuery } from '@tanstack/react-query'
import { CheckIcon, ChevronsUpDownIcon, GuitarIcon, XIcon } from 'lucide-react'
import { useRef, useState } from 'react'

const DEFAULT_INSTRUMENTS = [
  'Bateria',
  'Clarinete',
  'Flauta',
  'Guitarra',
  'Percussão',
  'Saxofone',
  'Trombone',
  'Trompete',
  'Tuba',
  'Violão',
]

export interface AlunosFiltersProps {
  search?: string
  selectedInstruments: string[]
  apenasMenores: boolean
  knownInstruments?: string[]
  onSearchChange: (search: string) => void
  onInstrumentsChange: (instruments: string[]) => void
  onApenasMenoresChange: (apenasMenores: boolean) => void
  onClearFilters: () => void
}

export function AlunosFilters({
  search = '',
  selectedInstruments,
  apenasMenores,
  knownInstruments = [],
  onSearchChange,
  onInstrumentsChange,
  onApenasMenoresChange,
  onClearFilters,
}: AlunosFiltersProps) {
  const [open, setOpen] = useState(false)
  const [term, setTerm] = useState('')
  const searchInputRef = useRef<HTMLInputElement>(null)

  const { data: instrumentsPage } = useQuery({
    queryKey: ['instruments-list-all'],
    queryFn: () => fetchInstrumentos({ page: 0, size: 100 }),
    staleTime: 1000 * 60 * 10,
    retry: false,
    refetchOnWindowFocus: false,
  })

  const apiInstruments = instrumentsPage?.content.map((item) => item.nome) ?? []

  const allOptions = uniqueText([
    ...selectedInstruments,
    ...apiInstruments,
    ...knownInstruments,
    ...DEFAULT_INSTRUMENTS,
  ]).sort((a, b) => normalizeText(a).localeCompare(normalizeText(b)))

  const normalizedTerm = normalizeText(term)
  const filteredOptions = allOptions.filter((option) =>
    normalizeText(option).includes(normalizedTerm),
  )

  const isSelected = (instrument: string) =>
    selectedInstruments.some(
      (item) => normalizeText(item) === normalizeText(instrument),
    )

  function toggleInstrument(instrument: string) {
    if (isSelected(instrument)) {
      onInstrumentsChange(
        selectedInstruments.filter(
          (item) => normalizeText(item) !== normalizeText(instrument),
        ),
      )
    } else {
      onInstrumentsChange([...selectedInstruments, instrument])
    }
  }

  function handleSearchSubmit() {
    const val = searchInputRef.current?.value ?? ''
    onSearchChange(val.trim())
  }

  function handleClearAll() {
    if (searchInputRef.current) {
      searchInputRef.current.value = ''
    }
    onClearFilters()
  }

  const hasActiveFilters =
    Boolean(search.trim()) || selectedInstruments.length > 0 || apenasMenores

  return (
    <div className="mb-6 space-y-3">
      <div className="flex flex-col gap-4 lg:flex-row lg:items-end">
        <Field className="min-w-0 flex-1">
          <FieldLabel htmlFor="aluno-search">Buscar aluno</FieldLabel>
          <ButtonGroup>
            <Input
              id="aluno-search"
              ref={searchInputRef}
              defaultValue={search}
              placeholder="Busque por nome ou e-mail..."
              onKeyDown={(e) => {
                if (e.key === 'Enter') {
                  handleSearchSubmit()
                }
              }}
            />
            <Button type="button" variant="outline" onClick={handleSearchSubmit}>
              Buscar
            </Button>
          </ButtonGroup>
        </Field>

        <Field className="sm:w-72">
          <FieldLabel htmlFor="aluno-instrumentos-filter">
            Filtrar por instrumento
          </FieldLabel>
          <Popover
            open={open}
            onOpenChange={(next) => {
              setOpen(next)
              if (!next) setTerm('')
            }}
          >
            <PopoverTrigger asChild>
              <Button
                id="aluno-instrumentos-filter"
                type="button"
                variant="outline"
                role="combobox"
                aria-expanded={open}
                className="w-full justify-between font-normal"
              >
                <span className="flex min-w-0 items-center gap-2 truncate">
                  <GuitarIcon className="size-4 shrink-0 text-muted-foreground" />
                  <span
                    className={
                      selectedInstruments.length === 0
                        ? 'text-muted-foreground truncate'
                        : 'truncate'
                    }
                  >
                    {selectedInstruments.length === 0
                      ? 'Todos os instrumentos'
                      : selectedInstruments.length === 1
                        ? capitalizeAll(selectedInstruments[0])
                        : `${selectedInstruments.length} instrumentos selecionados`}
                  </span>
                </span>
                <ChevronsUpDownIcon className="ml-2 size-4 shrink-0 opacity-50" />
              </Button>
            </PopoverTrigger>

            <PopoverContent className="w-72 p-0" align="start">
              <Command shouldFilter={false}>
                <CommandInput
                  value={term}
                  onValueChange={setTerm}
                  placeholder="Buscar instrumento..."
                />
                <CommandList>
                  {filteredOptions.length === 0 && (
                    <CommandEmpty>Nenhum instrumento encontrado.</CommandEmpty>
                  )}

                  <CommandGroup>
                    {filteredOptions.map((instrument) => {
                      const selected = isSelected(instrument)
                      return (
                        <CommandItem
                          key={instrument}
                          value={instrument}
                          onSelect={() => toggleInstrument(instrument)}
                        >
                          <CheckIcon
                            className={
                              selected ? 'size-4 opacity-100' : 'size-4 opacity-0'
                            }
                          />
                          {capitalizeAll(instrument)}
                        </CommandItem>
                      )
                    })}
                  </CommandGroup>
                </CommandList>
              </Command>
            </PopoverContent>
          </Popover>
        </Field>

        <div className="flex h-9 items-center gap-2.5 rounded-md border bg-card px-3">
          <Switch
            id="apenas-menores-switch"
            checked={apenasMenores}
            onCheckedChange={onApenasMenoresChange}
          />
          <label
            htmlFor="apenas-menores-switch"
            className="cursor-pointer text-sm font-medium select-none"
          >
            Apenas menores de 18 anos
          </label>
        </div>

        {hasActiveFilters && (
          <Button
            type="button"
            variant="ghost"
            size="sm"
            onClick={handleClearAll}
            className="h-9 text-muted-foreground hover:text-foreground"
          >
            <XIcon className="size-4" />
            Limpar filtros
          </Button>
        )}
      </div>

      {selectedInstruments.length > 0 && (
        <div className="flex flex-wrap items-center gap-1.5">
          <span className="text-xs text-muted-foreground mr-1">
            Instrumentos filtrados:
          </span>
          {selectedInstruments.map((instrument) => (
            <Badge key={instrument} variant="secondary" className="gap-1">
              {capitalizeAll(instrument)}
              <button
                type="button"
                aria-label={`Remover filtro ${instrument}`}
                className="rounded-full hover:text-destructive"
                onClick={() => toggleInstrument(instrument)}
              >
                <XIcon className="size-3" />
              </button>
            </Badge>
          ))}
        </div>
      )}
    </div>
  )
}
