import { searchResponsaveis } from '#/api/member'
import { Button } from '#/components/ui/button'
import { Command, CommandGroup, CommandInput, CommandItem, CommandList } from '#/components/ui/command'
import { Popover, PopoverContent, PopoverTrigger } from '#/components/ui/popover'
import { useDebounce } from '#/hooks/use-debounce'
import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { ChevronsUpDown } from 'lucide-react'
import { useState } from 'react'

export interface ResponsavelOption {
  id: string
  nome: string
  email?: string
}

interface Props {
  id?: string
  value: string
  onChangeValue: (id: string) => void
  onBlur?: () => void
  isInvalid?: boolean
  onCreateNew?: (termo: string) => void
}

export function ResponsavelSelect({ id, value, onChangeValue, onBlur, isInvalid, onCreateNew }: Props) {
  const [open, setOpen] = useState(false)
  const [termo, setTermo] = useState('')
  const [selecionado, setSelecionado] = useState<ResponsavelOption | null>(null)

  const debounced = useDebounce(termo.trim(), 300)

  const { data: sugestoes = [], isFetching } = useQuery({
    queryKey: ['responsaveis', debounced],
    queryFn: () => searchResponsaveis(debounced),
    enabled: open,
    staleTime: 30_000,
    placeholderData: keepPreviousData,
  })

  const semResultado = !isFetching && debounced === termo.trim() && sugestoes.length === 0

  return (
    <Popover
      open={open}
      onOpenChange={(o) => {
        setOpen(o)
        if (!o) onBlur?.()
      }}
    >
      <PopoverTrigger asChild>
        <Button
          id={id}
          type="button"
          variant="outline"
          role="combobox"
          aria-invalid={isInvalid}
          className="w-full justify-between font-normal"
        >
          {value && selecionado ? selecionado.nome : 'Buscar responsável...'}
          <ChevronsUpDown className="size-4 opacity-50" />
        </Button>
      </PopoverTrigger>

      <PopoverContent className="w-(--radix-popover-trigger-width) p-0" align="start">

        <Command shouldFilter={false}>
          <CommandInput placeholder="Digite o nome ou e-mail..." value={termo} onValueChange={setTermo} />
          <CommandList>
            {sugestoes.length > 0 && (
              <CommandGroup>
                {sugestoes.map((m) => (
                  <CommandItem
                    key={m.id}
                    value={m.id}
                    onSelect={() => {
                      setSelecionado({ id: m.id, nome: m.nome, email: m.email })
                      onChangeValue(m.id)
                      setOpen(false)
                    }}
                  >
                    <div className="flex flex-col">
                      <span>{m.nome}</span>
                      <span className="text-xs text-muted-foreground">
                        {m.email || `${m.idade} anos`}
                      </span>
                    </div>
                  </CommandItem>
                ))}
              </CommandGroup>
            )}

            {isFetching && sugestoes.length === 0 && (
              <p className="p-3 text-sm text-muted-foreground">Buscando...</p>
            )}

            {semResultado && (
              <div className="space-y-2 p-3 text-sm">
                <p className="text-muted-foreground">Nenhum responsável encontrado.</p>
                {onCreateNew && (
                  <Button
                    type="button"
                    variant="secondary"
                    size="sm"
                    className="w-full"
                    onClick={() => {
                      onCreateNew(termo.trim())
                      setOpen(false)
                    }}
                  >
                    Cadastrar {termo.trim() ? `"${termo.trim()}"` : 'novo responsável'}
                  </Button>
                )}
              </div>
            )}
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  )
}