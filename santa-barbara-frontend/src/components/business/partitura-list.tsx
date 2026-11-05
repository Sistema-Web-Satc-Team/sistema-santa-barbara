import { excluirPartitura, listarPartiturasDaMusica } from '#/api/partitura'
import { PartituraCard } from '#/components/business/partitura-card'
import { Button } from '#/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '#/components/ui/dialog'
import { Spinner } from '#/components/ui/spinner'
import { getErrorMessage } from '#/lib/api-error'
import { capitalize } from '#/lib/utils'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { FileTextIcon } from 'lucide-react'
import { useState } from 'react'
import { toast } from 'sonner'

interface PartituraListProps {
  idMusica: string
  canDelete: boolean
}

export function PartituraList({ idMusica, canDelete }: PartituraListProps) {
  const queryClient = useQueryClient()
  const [partituraExclusao, setPartituraExclusao] = useState<{
    id: string
    titulo: string
  } | null>(null)
  const {
    data: partituras,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['partituras-musica', idMusica],
    queryFn: () => listarPartiturasDaMusica(idMusica),
    retry: false,
  })
  const excluir = useMutation({
    mutationFn: excluirPartitura,
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ['partituras-musica', idMusica],
      })
      toast.success('Partitura removida com sucesso.')
      setPartituraExclusao(null)
    },
    onError: (error) => {
      toast.error(
        getErrorMessage(error, 'Não foi possível remover a partitura.'),
      )
    },
  })

  return (
    <>
      <div className="space-y-3 border-t pt-4">
        <h4 className="font-medium">Partituras</h4>
        {isLoading ? (
          <div className="flex justify-center py-6" role="status">
            <Spinner />
            <span className="sr-only">Carregando partituras...</span>
          </div>
        ) : isError ? (
          <p className="py-4 text-center text-sm text-destructive">
            Não foi possível carregar as partituras. Tente novamente.
          </p>
        ) : partituras?.length ? (
          <div className="grid grid-cols-1 gap-3 sm:grid-cols-2 lg:grid-cols-3">
            {partituras.map((partitura) => (
              <PartituraCard
                key={partitura.id}
                partitura={partitura}
                canDelete={canDelete}
                onDelete={() => setPartituraExclusao(partitura)}
              />
            ))}
          </div>
        ) : (
          <div className="flex flex-col items-center gap-2 py-6 text-center text-muted-foreground">
            <FileTextIcon className="size-8" aria-hidden />
            <p>Nenhuma partitura cadastrada para esta música.</p>
          </div>
        )}
      </div>

      <Dialog
        open={partituraExclusao !== null}
        onOpenChange={(open) => {
          if (!open && !excluir.isPending) setPartituraExclusao(null)
        }}
      >
        <DialogContent className="max-w-sm">
          <DialogHeader>
            <DialogTitle>Remover partitura?</DialogTitle>
            <DialogDescription>
              {partituraExclusao
                ? `"${capitalize(partituraExclusao.titulo)}" será removida. Essa ação não pode ser desfeita.`
                : ''}
            </DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => setPartituraExclusao(null)}
              disabled={excluir.isPending}
            >
              Cancelar
            </Button>
            <Button
              type="button"
              variant="destructive"
              onClick={() => {
                if (partituraExclusao) excluir.mutate(partituraExclusao.id)
              }}
              disabled={excluir.isPending}
            >
              {excluir.isPending && <Spinner />}
              Remover
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  )
}
