import type { Partitura } from '#/api/partitura'
import { baixarArquivoPartitura } from '#/api/partitura'
import { Badge } from '#/components/ui/badge'
import { Button } from '#/components/ui/button'
import { getErrorMessage } from '#/lib/api-error'
import { DownloadIcon, FileTextIcon } from 'lucide-react'
import { useState } from 'react'
import { toast } from 'sonner'

interface PartituraCardProps {
  partitura: Partitura
}

export function PartituraCard({ partitura }: PartituraCardProps) {
  const [isDownloading, setIsDownloading] = useState(false)

  async function handleDownload() {
    if (!partitura.partituraUrl) {
      toast.error('O arquivo desta partitura não está disponível.')
      return
    }

    setIsDownloading(true)
    try {
      const blob = await baixarArquivoPartitura(partitura.partituraUrl)
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `${partitura.titulo || 'partitura'}.pdf`
      document.body.append(link)
      link.click()
      link.remove()
      window.setTimeout(() => URL.revokeObjectURL(url), 1000)
    } catch (error) {
      toast.error(getErrorMessage(error, 'Não foi possível baixar a partitura.'))
    } finally {
      setIsDownloading(false)
    }
  }

  return (
    <article className="flex min-w-0 items-start gap-3 rounded-lg border p-4">
      <div className="flex size-10 shrink-0 items-center justify-center rounded-md bg-muted">
        <FileTextIcon className="size-5 text-muted-foreground" aria-hidden />
      </div>

      <div className="min-w-0 flex-1 space-y-2">
        <div>
          <h3 className="truncate font-medium">{partitura.titulo}</h3>
          {partitura.arranjador && (
            <p className="truncate text-sm text-muted-foreground">
              Arranjador: {partitura.arranjador}
            </p>
          )}
        </div>

        <div className="flex flex-wrap gap-1.5">
          {partitura.instrumentos.length > 0 ? (
            partitura.instrumentos.map((instrumento) => (
              <Badge key={instrumento.id} variant="secondary">
                {instrumento.nome}
              </Badge>
            ))
          ) : (
            <Badge variant="outline">Geral / Todos</Badge>
          )}
        </div>
      </div>

      <Button
        type="button"
        variant="outline"
        size="sm"
        disabled={isDownloading || !partitura.partituraUrl}
        onClick={() => void handleDownload()}
      >
        <DownloadIcon />
        {isDownloading ? 'Baixando...' : 'Baixar PDF'}
      </Button>
    </article>
  )
}
