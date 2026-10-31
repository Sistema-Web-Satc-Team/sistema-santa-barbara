import { criarPartitura } from '#/api/partitura'
import { fetchInstrumentos } from '#/api/instrumento'
import { Button } from '#/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '#/components/ui/dialog'
import { Field, FieldError, FieldLabel } from '#/components/ui/field'
import { Input } from '#/components/ui/input'
import { Spinner } from '#/components/ui/spinner'
import { getErrorMessage } from '#/lib/api-error'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import type { FormEvent } from 'react'
import { useState } from 'react'
import { toast } from 'sonner'

interface CriarPartituraFormValues {
  titulo: string
  arranjador: string
  instrumentos: string[]
  arquivo: File
}

interface PartituraCreateDialogProps {
  idMusica: string
  tituloMusica: string
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function PartituraCreateDialog({
  idMusica,
  tituloMusica,
  open,
  onOpenChange,
}: PartituraCreateDialogProps) {
  const queryClient = useQueryClient()
  const [titulo, setTitulo] = useState('')
  const [arranjador, setArranjador] = useState('')
  const [instrumentos, setInstrumentos] = useState<string[]>([])
  const [arquivo, setArquivo] = useState<File | null>(null)
  const [erro, setErro] = useState('')

  const {
    data: instrumentosDisponiveis,
    isLoading: carregandoInstrumentos,
    isError: erroInstrumentos,
  } = useQuery({
    queryKey: ['instruments-for-partituras'],
    queryFn: () => fetchInstrumentos({ page: 0, size: 200 }),
    enabled: open,
    retry: false,
  })

  const criar = useMutation({
    mutationFn: (values: CriarPartituraFormValues) =>
      criarPartitura({
        idMusica,
        arquivo: values.arquivo,
        dados: {
          titulo: values.titulo,
          arranjador: values.arranjador || undefined,
          instrumentos: values.instrumentos,
        },
      }),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['partituras-musica', idMusica] })
      toast.success('Partitura cadastrada com sucesso.')
      handleOpenChange(false)
    },
    onError: (error) => {
      toast.error(getErrorMessage(error, 'Não foi possível cadastrar a partitura.'))
    },
  })

  function toggleInstrumento(nome: string) {
    setInstrumentos((selecionados) =>
      selecionados.includes(nome)
        ? selecionados.filter((item) => item !== nome)
        : [...selecionados, nome],
    )
  }

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!titulo.trim()) {
      setErro('Informe o título da partitura.')
      return
    }
    if (!arquivo || arquivo.type !== 'application/pdf') {
      setErro('Selecione um arquivo PDF válido.')
      return
    }

    setErro('')
    criar.mutate({
      titulo: titulo.trim(),
      arranjador: arranjador.trim(),
      instrumentos,
      arquivo,
    })
  }

  function handleOpenChange(nextOpen: boolean) {
    if (!nextOpen) {
      setTitulo('')
      setArranjador('')
      setInstrumentos([])
      setArquivo(null)
      setErro('')
      criar.reset()
    }
    onOpenChange(nextOpen)
  }

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="max-h-[85vh] overflow-y-auto sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Adicionar partitura</DialogTitle>
          <DialogDescription>
            Cadastre um arranjo para a música {tituloMusica}.
          </DialogDescription>
        </DialogHeader>

        <form className="space-y-4" onSubmit={handleSubmit}>
          <Field>
            <FieldLabel htmlFor="partitura-titulo">Título*</FieldLabel>
            <Input
              id="partitura-titulo"
              value={titulo}
              onChange={(event) => setTitulo(event.target.value)}
              placeholder="Ex.: Tema principal"
              maxLength={256}
              required
            />
          </Field>

          <Field>
            <FieldLabel htmlFor="partitura-arranjador">Arranjador(es)</FieldLabel>
            <Input
              id="partitura-arranjador"
              value={arranjador}
              onChange={(event) => setArranjador(event.target.value)}
              placeholder="Ex.: João da Silva"
              maxLength={256}
            />
          </Field>

          <Field>
            <FieldLabel>Instrumentos</FieldLabel>
            {carregandoInstrumentos ? (
              <div className="flex items-center gap-2 text-sm text-muted-foreground" role="status">
                <Spinner className="size-4" />
                Carregando instrumentos...
              </div>
            ) : erroInstrumentos ? (
              <p className="text-sm text-destructive">
                Não foi possível carregar os instrumentos.
              </p>
            ) : (
              <div className="max-h-40 space-y-2 overflow-y-auto rounded-md border p-3">
                <label className="flex items-center gap-2 text-sm">
                  <input
                    type="checkbox"
                    checked={instrumentos.length === 0}
                    onChange={() => setInstrumentos([])}
                  />
                  Geral / Todos
                </label>
                {instrumentosDisponiveis?.content.map((instrumento) => (
                  <label key={instrumento.id} className="flex items-center gap-2 text-sm">
                    <input
                      type="checkbox"
                      checked={instrumentos.includes(instrumento.nome)}
                      onChange={() => toggleInstrumento(instrumento.nome)}
                    />
                    {instrumento.nome}
                  </label>
                ))}
                {instrumentosDisponiveis?.content.length === 0 && (
                  <p className="text-sm text-muted-foreground">
                    Nenhum instrumento cadastrado.
                  </p>
                )}
              </div>
            )}
          </Field>

          <Field>
            <FieldLabel htmlFor="partitura-arquivo">Arquivo PDF*</FieldLabel>
            <Input
              id="partitura-arquivo"
              type="file"
              accept="application/pdf,.pdf"
              onChange={(event) => setArquivo(event.target.files?.[0] ?? null)}
              required
            />
          </Field>

          {erro && <FieldError>{erro}</FieldError>}

          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => handleOpenChange(false)}
              disabled={criar.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="submit"
              disabled={criar.isPending || carregandoInstrumentos || erroInstrumentos}
            >
              {criar.isPending && <Spinner className="mr-2 size-4" />}
              Cadastrar
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
