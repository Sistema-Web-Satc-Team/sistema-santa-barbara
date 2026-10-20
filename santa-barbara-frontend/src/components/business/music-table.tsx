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
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '#/components/ui/table'
import { capitalize } from '#/lib/utils'
import { EllipsisIcon, MusicIcon, PencilIcon, Trash2Icon } from 'lucide-react'

interface MusicTableProps {
  musicas: Musica[]

  canEdit: boolean

  canDelete: boolean
  onOpen: (musica: Musica) => void
  onDelete: (musica: Musica) => void
}

export function MusicTable({ musicas, canEdit, canDelete, onOpen, onDelete }: MusicTableProps) {
  return (
    <div className="w-full max-w-full overflow-x-auto">
      <Table>
        <TableHeader>
          <TableRow>
            <TableHead>Música</TableHead>
            <TableHead>Gêneros</TableHead>
            {canEdit && <TableHead className="text-right">Ações</TableHead>}
          </TableRow>
        </TableHeader>

        <TableBody>
          {musicas.map((musica) => (
            <TableRow key={musica.id}>
              <TableCell>
                <div className="flex items-center gap-3">
                  <div className="flex size-10 shrink-0 items-center justify-center rounded-lg bg-muted">
                    <MusicIcon className="size-4" aria-hidden />
                  </div>
                  <div className="min-w-0">
                    <p className="truncate font-medium">{musica.titulo}</p>
                    <p className="truncate text-sm text-muted-foreground">
+                      {musica.compositor?.trim() ? musica.compositor : 'Compositor não informado'}
+                    </p>
                  </div>
                </div>
              </TableCell>

              <TableCell>
                <div className="flex flex-wrap gap-1.5">
                  {musica.generos.map((genero) => (
                    <Badge key={genero} variant="secondary">
                      {capitalize(genero)}
                    </Badge>
                  ))}
                </div>
              </TableCell>

              {canEdit && (
                <TableCell>
                  <div className="flex items-center justify-end gap-2">
                    <MusicSheetActions />


                    <DropdownMenu modal={false}>
                      <DropdownMenuTrigger asChild>
                        <Button
                          type="button"
                          variant="ghost"
                          size="icon"
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
                  </div>
                </TableCell>
              )}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}
