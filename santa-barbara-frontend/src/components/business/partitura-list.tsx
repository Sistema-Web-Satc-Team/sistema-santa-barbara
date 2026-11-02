import { listarPartiturasDaMusica } from '#/api/partitura'
import { PartituraCard } from '#/components/business/partitura-card'
import { Spinner } from '#/components/ui/spinner'
import { useQuery } from '@tanstack/react-query'
import { FileTextIcon } from 'lucide-react'

interface PartituraListProps {
  idMusica: string
}

export function PartituraList({ idMusica }: PartituraListProps) {
  const {
    data: partituras,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['partituras-musica', idMusica],
    queryFn: () => listarPartiturasDaMusica(idMusica),
    retry: false,
  })

  return (
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
            <PartituraCard key={partitura.id} partitura={partitura} />
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center gap-2 py-6 text-center text-muted-foreground">
          <FileTextIcon className="size-8" aria-hidden />
          <p>Nenhuma partitura cadastrada para esta música.</p>
        </div>
      )}
    </div>
  )
}
