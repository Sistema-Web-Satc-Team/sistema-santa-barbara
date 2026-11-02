import type { Musica } from '#/api/musica'
import { MusicSheetActions } from '#/components/business/music-sheet-actions'
import { Badge } from '#/components/ui/badge'
import { Button } from '#/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '#/components/ui/dropdown-menu'
import { capitalize } from '#/lib/utils'
import { EllipsisIcon, MusicIcon, PencilIcon, Trash2Icon } from 'lucide-react'

interface MusicListProps {
  musicas: Musica[]
  canEdit: boolean
  canDelete: boolean
  onOpen: (musica: Musica) => void
  onDelete: (musica: Musica) => void
}

export function MusicList({
  musicas,
  canEdit,
  canDelete,
  onOpen,
  onDelete,
}: MusicListProps) {
  return (
    <div className="grid min-w-0 gap-4">
      {musicas.map((musica) => (
        <article
          key={musica.id}
          className="min-w-0 space-y-4 rounded-lg border p-4 sm:p-5"
        >
          <div className="flex min-w-0 items-start gap-3">
            <div className="flex size-10 shrink-0 items-center justify-center rounded-lg bg-muted">
              <MusicIcon className="size-4" aria-hidden />
            </div>
            <div className="min-w-0 flex-1">
              <h3 className="break-words font-medium">{musica.titulo}</h3>
              <p className="break-words text-sm text-muted-foreground">
                {musica.compositor?.trim()
                  ? musica.compositor
                  : 'Compositor não informado'}
              </p>
            </div>
          </div>

          {musica.generos.length > 0 && (
            <div className="flex flex-wrap gap-1.5">
              {musica.generos.map((genero) => (
                <Badge key={genero} variant="secondary">
                  {capitalize(genero)}
                </Badge>
              ))}
            </div>
          )}

          <div className="flex min-w-0 flex-col gap-3 sm:flex-row sm:items-start">
            <MusicSheetActions musica={musica} canAdd={canEdit} />
            {canEdit && (
              <DropdownMenu modal={false}>
                <DropdownMenuTrigger asChild>
                  <Button
                    type="button"
                    variant="ghost"
                    size="icon"
                    className="self-start"
                    aria-label={`Ações de ${musica.titulo}`}
                  >
                    <EllipsisIcon />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem onSelect={() => onOpen(musica)}>
                    <PencilIcon />
                    Detalhes e edição
                  </DropdownMenuItem>
                  {canDelete && (
                    <DropdownMenuItem
                      className="text-destructive focus:text-destructive"
                      onSelect={() => onDelete(musica)}
                    >
                      <Trash2Icon />
                      Remover
                    </DropdownMenuItem>
                  )}
                </DropdownMenuContent>
              </DropdownMenu>
            )}
          </div>
        </article>
      ))}
    </div>
  )
}
