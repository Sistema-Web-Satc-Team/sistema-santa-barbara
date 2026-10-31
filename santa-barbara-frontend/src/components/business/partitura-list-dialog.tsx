import { listarPartiturasDaMusica } from '#/api/partitura'
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
import { useQuery } from '@tanstack/react-query'
import { FileTextIcon } from 'lucide-react'

interface PartituraListDialogProps {
  idMusica: string
  tituloMusica: string
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function PartituraListDialog({
  idMusica,
  tituloMusica,
  open,
  onOpenChange,
}: PartituraListDialogProps) {
  const { data: partituras, isLoading, isError } = useQuery({
    queryKey: ['partituras-musica', idMusica],
    queryFn: () => listarPartiturasDaMusica(idMusica),
    enabled: open,
    retry: false,
  })

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="flex max-h-[85vh] flex-col overflow-hidden sm:max-w-2xl">
        <DialogHeader>
          <DialogTitle>Partituras de {tituloMusica}</DialogTitle>
          <DialogDescription>
            Consulte os arranjos disponíveis e baixe o PDF para seus estudos.
          </DialogDescription>
        </DialogHeader>

        <div className="min-h-0 flex-1 space-y-3 overflow-y-auto py-1">
          {isLoading ? (
            <div className="flex justify-center py-10" role="status">
              <Spinner />
              <span className="sr-only">Carregando partituras...</span>
            </div>
          ) : isError ? (
            <p className="py-8 text-center text-sm text-destructive">
              Não foi possível carregar as partituras. Tente novamente.
            </p>
          ) : partituras?.length ? (
            partituras.map((partitura) => (
              <PartituraCard key={partitura.id} partitura={partitura} />
            ))
          ) : (
            <div className="flex flex-col items-center gap-2 py-10 text-center text-muted-foreground">
              <FileTextIcon className="size-8" aria-hidden />
              <p>Nenhuma partitura cadastrada para esta música.</p>
            </div>
          )}
        </div>

        <DialogFooter>
          <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
            Fechar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
