import type { Musica } from '#/api/musica'
import { PartituraCreateDialog } from '#/components/business/partitura-create-dialog'
import { PartituraList } from '#/components/business/partitura-list'
import { Button } from '#/components/ui/button'
import { ChevronDownIcon, FilePlusIcon, FileTextIcon } from 'lucide-react'
import { useId, useState } from 'react'

interface MusicSheetActionsProps {
  musica: Musica
  canAdd: boolean
}

export function MusicSheetActions({ musica, canAdd }: MusicSheetActionsProps) {
  const [listOpen, setListOpen] = useState(false)
  const [createOpen, setCreateOpen] = useState(false)
  const listId = useId()

  return (
    <>
      <div className="min-w-0 flex-1 space-y-3">
        <div className="flex flex-wrap gap-2">
          <Button
            type="button"
            variant="outline"
            size="sm"
            className="w-full sm:w-auto"
            aria-expanded={listOpen}
            aria-controls={listId}
            onClick={() => setListOpen((open) => !open)}
          >
            <FileTextIcon />
            {listOpen ? 'Ocultar partituras' : 'Ver partituras'}
            <ChevronDownIcon
              className={
                listOpen
                  ? 'rotate-180 transition-transform'
                  : 'transition-transform'
              }
            />
          </Button>
          {canAdd && (
            <Button
              type="button"
              variant="outline"
              size="sm"
              className="w-full sm:w-auto"
              onClick={() => setCreateOpen(true)}
            >
              <FilePlusIcon />
              Adicionar partituras
            </Button>
          )}
        </div>
        {listOpen && (
          <div id={listId}>
            <PartituraList idMusica={musica.id} />
          </div>
        )}
      </div>

      {canAdd && (
        <PartituraCreateDialog
          idMusica={musica.id}
          tituloMusica={musica.titulo}
          open={createOpen}
          onOpenChange={setCreateOpen}
        />
      )}
    </>
  )
}
