import type { Instrumento } from '#/api/instrumento'
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

interface InstrumentDeleteDialogProps {
  /** null = diálogo fechado. */
  instrumento: Instrumento | null
  isDeleting: boolean
  onCancel: () => void
  onConfirm: () => void
}

export function InstrumentDeleteDialog({
  instrumento,
  isDeleting,
  onCancel,
  onConfirm,
}: InstrumentDeleteDialogProps) {
  return (
    <Dialog
      open={instrumento !== null}
      onOpenChange={(open) => {
        if (!open && !isDeleting) onCancel()
      }}
    >
      <DialogContent className="max-w-sm">
        <DialogHeader>
          <DialogTitle>Remover instrumento?</DialogTitle>
          <DialogDescription>
            {instrumento
              ? `"${instrumento.nome}" será removido do sistema. Essa ação não pode ser desfeita.`
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
