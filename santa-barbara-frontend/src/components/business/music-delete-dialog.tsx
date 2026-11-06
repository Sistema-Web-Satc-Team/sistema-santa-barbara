import type { Musica } from '#/api/musica'
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

interface MusicDeleteDialogProps {

  musica: Musica | null
  isDeleting: boolean
  onCancel: () => void
  onConfirm: () => void
}

export function MusicDeleteDialog({
  musica,
  isDeleting,
  onCancel,
  onConfirm,
}: MusicDeleteDialogProps) {
  return (
    <Dialog
      open={musica !== null}
      onOpenChange={(open) => {
        if (!open && !isDeleting) onCancel()
      }}
    >
      <DialogContent className="max-w-sm">
        <DialogHeader>
          <DialogTitle>Remover música?</DialogTitle>
          <DialogDescription>
            {musica
              ? `"${musica.titulo}" será removida do acervo. Essa ação não pode ser desfeita.`
              : ''}
          </DialogDescription>
        </DialogHeader>

        <DialogFooter>
          <Button type="button" variant="outline" onClick={onCancel} disabled={isDeleting}>
            Cancelar
          </Button>
          <Button type="button" variant="destructive" onClick={onConfirm} disabled={isDeleting}>
            {isDeleting && <Spinner />}
            Remover
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
