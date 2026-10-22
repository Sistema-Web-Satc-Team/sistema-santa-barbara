import { Button } from '#/components/ui/button'
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '#/components/ui/dialog'
import { useAppForm } from '#/lib/form'
import { mensagemDeErro } from '#/lib/registrar-membro'
import type { RelacaoValue } from '#/lib/relacao-responsavel'
import { formVinculoResponsavelSchema, type VinculoResponsavelValues } from '#/schemas/form-schema-vinculo-responsavel'
import { toast } from 'sonner'
import { VinculoResponsavelFormFields } from './vinculo-responsavel-form-fields'

interface Props {
  open: boolean
  onOpenChange: (open: boolean) => void
  initialValues: VinculoResponsavelValues
  nomeFixo?: string
  excludeIds: string[]
  onSubmit: (values: { idResponsavel: string; grauRelacao: RelacaoValue }) => Promise<void>
}

export function VinculoResponsavelDialog({ open, onOpenChange, initialValues, nomeFixo, excludeIds, onSubmit }: Props) {
  const form = useAppForm({
    defaultValues: initialValues,
    validators: { onSubmit: formVinculoResponsavelSchema },
    onSubmit: async ({ value }) => {
      try {
        await onSubmit({
            idResponsavel: value.idResponsavel,
            grauRelacao: value.grauRelacao as RelacaoValue,
        })
        toast.success(nomeFixo ? 'Relação atualizada.' : 'Responsável adicionado.')
        onOpenChange(false)
      } catch (err) {
        toast.error(mensagemDeErro(err))
      }
    },
  })

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-md gap-0 p-0">
        <DialogHeader className="border-b p-4 pb-2">
          <DialogTitle>{nomeFixo ? 'Editar relação' : 'Adicionar responsável'}</DialogTitle>
        </DialogHeader>

        <form
          onSubmit={(e) => {
            e.preventDefault()
            e.stopPropagation()
            form.handleSubmit()
          }}
        >
          <div className="p-4">
            <VinculoResponsavelFormFields form={form} nomeFixo={nomeFixo} excludeIds={excludeIds} />
          </div>
          <DialogFooter className="border-t p-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancelar
            </Button>
            <form.Subscribe selector={(s) => s.isSubmitting}>
              {(busy) => (
                <Button type="submit" disabled={busy}>
                  {busy ? 'Salvando...' : 'Salvar'}
                </Button>
              )}
            </form.Subscribe>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}