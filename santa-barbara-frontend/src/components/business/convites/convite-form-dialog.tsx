import type { ConviteEnviado } from '#/api/convites'
import { Button } from '#/components/ui/button'
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '#/components/ui/dialog'
import { useAppForm } from '#/lib/form'
import { mensagemDeErro } from '#/lib/registrar-membro'
import { formConviteSchema } from '#/schemas/form-schema-convite'
import { toast } from 'sonner'
import { ConviteFormFields } from './convite-form-fields'
import type { CanalValue } from './convites'

interface Props {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSubmit: (v: { idMembro: string; canal: CanalValue }) => Promise<ConviteEnviado>
}

export function ConviteFormDialog({ open, onOpenChange, onSubmit }: Props) {
  const form = useAppForm({
    defaultValues: { idMembro: '', canal: 'EMAIL' },
    validators: { onSubmit: formConviteSchema },
    onSubmit: async ({ value }) => {
      try {
        const enviado = await onSubmit({ idMembro: value.idMembro, canal: value.canal as CanalValue })
        toast.success('Convite enviado.', {
          action: {
            label: 'Copiar link',
            onClick: () => navigator.clipboard.writeText(enviado.URL),
          },
        })
        form.reset()
        onOpenChange(false)
      } catch (err) {
        toast.error(mensagemDeErro(err))
      }
    },
  })

  return (
    <Dialog
      open={open}
      onOpenChange={(o) => {
        if (!o) form.reset()
        onOpenChange(o)
      }}
    >
      <DialogContent className="max-w-md gap-0 p-0">
        <DialogHeader className="border-b p-4 pb-2">
          <DialogTitle>Convidar membro</DialogTitle>
        </DialogHeader>

        <form
          onSubmit={(e) => {
            e.preventDefault()
            e.stopPropagation()
            form.handleSubmit()
          }}
        >
          <div className="p-4">
            <ConviteFormFields form={form} />
          </div>
          <DialogFooter className="border-t p-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancelar
            </Button>
            <form.Subscribe selector={(s) => s.isSubmitting}>
              {(busy) => (
                <Button type="submit" disabled={busy}>
                  {busy ? 'Enviando...' : 'Enviar convite'}
                </Button>
              )}
            </form.Subscribe>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}