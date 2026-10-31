import type { Musica } from '#/api/musica'
import { PartituraCreateDialog } from '#/components/business/partitura-create-dialog'
import { PartituraListDialog } from '#/components/business/partitura-list-dialog'
import { Button } from '#/components/ui/button'
import { FilePlusIcon, FileTextIcon } from 'lucide-react'
import { useState } from 'react'

interface MusicSheetActionsProps {
  musica: Musica
  canAdd: boolean
}

export function MusicSheetActions({ musica, canAdd }: MusicSheetActionsProps) {
  const [listOpen, setListOpen] = useState(false)
  const [createOpen, setCreateOpen] = useState(false)

  return (
    <>
      <div className="flex items-center gap-2">
        <Button
          type="button"
          variant="outline"
          size="sm"
          onClick={() => setListOpen(true)}
        >
          <FileTextIcon />
          Ver partituras
        </Button>
        {canAdd && (
          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={() => setCreateOpen(true)}
          >
            <FilePlusIcon />
            Adicionar partituras
          </Button>
        )}
      </div>

      <PartituraListDialog
        idMusica={musica.id}
        tituloMusica={musica.titulo}
        open={listOpen}
        onOpenChange={setListOpen}
      />
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
