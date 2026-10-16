import { searchMembrosConvidaveis } from '#/api/convites'
import { Button } from '#/components/ui/button'
import { Command, CommandGroup, CommandInput, CommandItem, CommandList } from '#/components/ui/command'
import { Popover, PopoverContent, PopoverTrigger } from '#/components/ui/popover'
import { useDebounce } from '#/hooks/use-debounce'
import { keepPreviousData, useQuery } from '@tanstack/react-query'
import { ChevronsUpDown } from 'lucide-react'
import { useState } from 'react'

interface Props {
  id?: string
  value: string
  onChangeValue: (id: string) => void
  onBlur?: () => void
  isInvalid?: boolean
}

export function MembroConviteSelect({ id, value, onChangeValue, onBlur, isInvalid }: Props) {
  const [open, setOpen] = useState(false)
  const [termo, setTermo] = useState('')
  const [nomeSelecionado, setNomeSelecionado] = useState('')

  const debounced = useDebounce(termo.trim(), 300)

  const { data: membros = [], isFetching } = useQuery({
    queryKey: ['membros-convidaveis', debounced],
    queryFn: () => searchMembrosConvidaveis(debounced),
    enabled: open,
    staleTime: 15_000,
    placeholderData: keepPreviousData,
  })

  const semResultado = !isFetching && debounced === termo.trim() && membros.length === 0

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
          {value && nomeSelecionado ? nomeSelecionado : 'Buscar membro...'}
          <ChevronsUpDown className="size-4 opacity-50" />
        </Button>
      </PopoverTrigger>

      <PopoverContent className="w-(--radix-popover-trigger-width) p-0" align="start">
        <Command shouldFilter={false}>
          <CommandInput placeholder="Nome ou e-mail..." value={termo} onValueChange={setTermo} />
          <CommandList>
            {membros.length > 0 && (
              <CommandGroup>
                {membros.map((m) => {
                  const semEmail = !m.email
                  return (
                    <CommandItem
                      key={m.id}
                      value={m.id}
                      disabled={semEmail}
                      onSelect={() => {
                        setNomeSelecionado(m.nome)
                        onChangeValue(m.id)
                        setOpen(false)
                      }}
                    >
                      <div className="flex flex-col">
                        <span>{m.nome}</span>
                        <span className="text-xs text-muted-foreground">
                          {semEmail ? 'Sem e-mail cadastrado' : m.email}
                        </span>
                      </div>
                    </CommandItem>
                  )
                })}
              </CommandGroup>
            )}

            {isFetching && membros.length === 0 && (
              <p className="p-3 text-sm text-muted-foreground">Buscando...</p>
            )}
            {semResultado && (
              <p className="p-3 text-sm text-muted-foreground">
                Nenhum membro sem acesso encontrado. Quem já tem convite aparece na tabela.
              </p>
            )}
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  )
}