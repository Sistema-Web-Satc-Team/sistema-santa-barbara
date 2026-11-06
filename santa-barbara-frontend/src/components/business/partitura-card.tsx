import type { Partitura } from '#/api/partitura'
import { baixarArquivoPartitura } from '#/api/partitura'
import { Badge } from '#/components/ui/badge'
import { Button } from '#/components/ui/button'
import { getErrorMessage } from '#/lib/api-error'
import { capitalize } from '#/lib/utils'
import { useState } from 'react'
import { toast } from 'sonner'


import {
  DownloadIcon,
  FileTextIcon,
  MoreHorizontalIcon,
  Trash2Icon,
} from 'lucide-react'

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '#/components/ui/dropdown-menu'

interface PartituraCardProps {
  partitura: Partitura
  canDelete: boolean
  onDelete: () => void
}

export function PartituraCard({
  partitura,
  canDelete,
  onDelete,
}: PartituraCardProps) {
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
      toast.error(
        getErrorMessage(error, 'Não foi possível baixar a partitura.'),
      )
    } finally {
      setIsDownloading(false)
    }
  }

  console.log(partitura.titulo)

  return (
    <article className="group flex min-w-0 items-center gap-3 rounded-xl border bg-card p-3 transition-colors hover:bg-muted/30">

      <div className="flex size-10 shrink-0 items-center justify-center rounded-lg bg-muted">
        <FileTextIcon className="size-5 text-muted-foreground" />
      </div>

      <div className="min-w-0 flex-1">
        <h3 className="truncate text-sm font-medium">
          {capitalize(partitura.titulo)}
        </h3>

        {partitura.arranjador && (
          <p className="truncate text-xs text-muted-foreground">
            {partitura.arranjador}
          </p>
        )}

        <p className="truncate text-xs text-muted-foreground">
          {partitura.instrumentos.length > 0
            ? partitura.instrumentos
                .map(i => capitalize(i.nome))
                .join(', ')
            : 'Geral / Todos'}
        </p>
      </div>

      <div className="flex shrink-0 items-center gap-1">

        <Button
          type="button"
          variant="ghost"
          size="icon"
          className="size-8"
          title="Baixar PDF"
          aria-label="Baixar PDF"
          disabled={isDownloading || !partitura.partituraUrl}
          onClick={() => void handleDownload()}
        >
          <DownloadIcon className="size-4" />
        </Button>

        {canDelete && (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button
                type="button"
                variant="ghost"
                size="icon"
                className="size-8"
                aria-label="Mais opções"
              >
                <MoreHorizontalIcon className="size-4" />
              </Button>
            </DropdownMenuTrigger>

            <DropdownMenuContent align="end">
              <DropdownMenuItem
                variant="destructive"
                onSelect={onDelete}
              >
                <Trash2Icon className="size-4" />
                Remover partitura
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        )}

      </div>
    </article>
  )
}
