
import type { Member, UpdateMemberRequest } from '#/api/member'
import { Button } from '#/components/ui/button'
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '#/components/ui/dialog'

import { useAppForm } from '#/lib/form'
import { buildUpdateRequest, memberToUpdateValues } from '#/routes/_auth/dashboard/_layout.membros/atualizar-membro'
import { mensagemDeErro } from '#/routes/_auth/dashboard/_layout.membros/registrar-membro'
import { formMemberUpdateSchema } from '#/schemas/form-schema-member-update'
import { toast } from 'sonner'
import { MemberEditFormFields } from './member-edit-form-fields'

interface Props {
  member: Member
  open: boolean
  onOpenChange: (open: boolean) => void
  onSubmit: (id: string, request: UpdateMemberRequest) => Promise<void>
}

export function MemberEditDialog({ member, open, onOpenChange, onSubmit }: Props) {
  const form = useAppForm({
    defaultValues: memberToUpdateValues(member),
    validators: { onSubmit: formMemberUpdateSchema },
    onSubmit: async ({ value }) => {
      const request = buildUpdateRequest(member, value)

      if (Object.keys(request).length === 0) {
        toast.info('Nenhuma alteração para salvar.')
        onOpenChange(false)
        return
      }

      try {
        await onSubmit(member.id, request)
        toast.success('Cadastro atualizado com sucesso.')
        onOpenChange(false)
      } catch (err) {
        toast.error(mensagemDeErro(err))
      }
    },
  })

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="flex flex-col md:max-w-xl max-w-md max-h-[90vh] overflow-hidden p-0 gap-0">
        <DialogHeader className="p-4 pb-2 border-b shrink-0">
          <DialogTitle>Editar cadastro</DialogTitle>
        </DialogHeader>

        <form
          onSubmit={(e) => {
            e.preventDefault()
            e.stopPropagation()
            form.handleSubmit()
          }}
          className="flex flex-col flex-1 min-h-0"
        >
          <div className="flex-1 min-h-0 overflow-y-auto p-4">
            <MemberEditFormFields form={form} />
          </div>

          <DialogFooter className="p-4 border-t shrink-0">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancelar
            </Button>
            <form.Subscribe selector={(s) => s.isSubmitting}>
              {(isSubmitting) => (
                <Button type="submit" disabled={isSubmitting}>
                  {isSubmitting ? 'Salvando...' : 'Salvar'}
                </Button>
              )}
            </form.Subscribe>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}