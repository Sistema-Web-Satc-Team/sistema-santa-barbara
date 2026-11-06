import { fetchGeneroSugestoes } from '#/api/musica'
import { Badge } from '#/components/ui/badge'
import { Button } from '#/components/ui/button'
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from '#/components/ui/command'
import { Popover, PopoverContent, PopoverTrigger } from '#/components/ui/popover'
import { capitalize } from '#/lib/utils'
import { normalizeText, uniqueText } from '#/lib/text'
import { useQuery } from '@tanstack/react-query'
import { CheckIcon, ChevronsUpDownIcon, XIcon } from 'lucide-react'
import { useEffect, useState } from 'react'

interface GenreMultiSelectProps {
  value: string[]
  onChange: (genres: string[]) => void

  options?: string[]
  placeholder?: string

  allowCreate?: boolean

  showChips?: boolean
  id?: string
}

export function GenreMultiSelect({
  value,
  onChange,
  options = [],
  placeholder = 'Todos os gêneros',
  allowCreate = false,
  showChips = false,
  id,
}: GenreMultiSelectProps) {
  const [open, setOpen] = useState(false)
  const [term, setTerm] = useState('')
  const [debouncedTerm, setDebouncedTerm] = useState('')

  useEffect(() => {
    const timer = setTimeout(() => setDebouncedTerm(term.trim()), 300)
    return () => clearTimeout(timer)
  }, [term])

  const { data: suggestions = [] } = useQuery({
    queryKey: ['genre-suggestions', debouncedTerm],
    queryFn: () => fetchGeneroSugestoes(debouncedTerm),
    enabled: open,
    staleTime: 1000 * 60,
    retry: false,
  })

  const normalizedTerm = normalizeText(term)
  const candidates = uniqueText([
    ...value,
    ...options.filter((option) => normalizeText(option).includes(normalizedTerm)),
    ...suggestions,
  ])

  const isSelected = (genre: string) =>
    value.some((item) => normalizeText(item) === normalizeText(genre))

  const canCreate =
    allowCreate &&
    normalizedTerm !== '' &&
    !candidates.some((candidate) => normalizeText(candidate) === normalizedTerm)

  function toggle(genre: string) {
    if (isSelected(genre)) {
      onChange(value.filter((item) => normalizeText(item) !== normalizeText(genre)))
    } else {
      onChange([...value, genre])
    }
  }

  function create() {
    onChange([...value, term.trim()])
    setTerm('')
  }

  function handleOpenChange(next: boolean) {
    setOpen(next)
    if (!next) setTerm('')
  }

  return (
    <div className="space-y-2">
      <Popover open={open} onOpenChange={handleOpenChange}>
        <PopoverTrigger asChild>
          <Button
            id={id}
            type="button"
            variant="outline"
            role="combobox"
            aria-expanded={open}
            className="w-full justify-between font-normal"
          >
            <span className={value.length === 0 ? 'text-muted-foreground' : 'truncate'}>
              {value.length === 0 ? placeholder : value.map(capitalize).join(', ')}
            </span>
            <ChevronsUpDownIcon className="ml-2 size-4 shrink-0 opacity-50" />
          </Button>
        </PopoverTrigger>

        <PopoverContent className="w-72 p-0" align="start">

          <Command shouldFilter={false}>
            <CommandInput
              value={term}
              onValueChange={setTerm}
              placeholder="Buscar gênero..."
            />
            <CommandList>
              {candidates.length === 0 && !canCreate && (
                <CommandEmpty>Nenhum gênero encontrado.</CommandEmpty>
              )}

              <CommandGroup>
                {candidates.map((genre) => (
                  <CommandItem key={genre} value={genre} onSelect={() => toggle(genre)}>
                    <CheckIcon
                      className={isSelected(genre) ? 'size-4 opacity-100' : 'size-4 opacity-0'}
                    />
                    {capitalize(genre)}
                  </CommandItem>
                ))}

                {canCreate && (
                  <CommandItem value={`criar-${term}`} onSelect={create}>
                    Adicionar “{term.trim()}”
                  </CommandItem>
                )}
              </CommandGroup>
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>

      {showChips && value.length > 0 && (
        <div className="flex flex-wrap gap-1.5">
          {value.map((genre) => (
            <Badge key={genre} variant="secondary" className="gap-1">
              {capitalize(genre)}
              <button
                type="button"
                aria-label={`Remover ${genre}`}
                className="rounded-full hover:text-destructive"
                onClick={() => toggle(genre)}
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
