import type { Member } from '#/api/member'
import type { ResponsavelDoMembro } from '#/api/member-responsaveis'
import {
  AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent,
  AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle,
} from '#/components/ui/alert-dialog'
import { Button } from '#/components/ui/button'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '#/components/ui/dialog'
import { Spinner } from '#/components/ui/spinner'
import { useMemberResponsaveis } from '#/hooks/use-member-responsaveis'

import { mensagemDeErro } from '#/lib/registrar-membro'
import { relacaoDeLabel, type RelacaoValue } from '#/lib/relacao-responsavel'
import { Pencil, Plus, Trash2 } from 'lucide-react'
import { useState } from 'react'
import { toast } from 'sonner'
import { VinculoResponsavelDialog } from './vinculo-responsavel-dialog'

type Editor = { mode: 'add' } | { mode: 'edit'; responsavel: ResponsavelDoMembro } | null

interface Props {
  member: Member
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function MemberResponsaveisDialog({ member, open, onOpenChange }: Props) {
  const { query, save, remove } = useMemberResponsaveis(member.id, open)
  const [editor, setEditor] = useState<Editor>(null)
  const [aRemover, setARemover] = useState<ResponsavelDoMembro | null>(null)

  const responsaveis = query.data?.responsaveis ?? []
  const excludeIds = [member.id, ...responsaveis.map((r) => r.id)]

  return (
    <>
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent className="max-w-lg">
          <DialogHeader>
            <DialogTitle>Responsáveis de {member.nome}</DialogTitle>
          </DialogHeader>

          {query.isLoading && <Spinner />}
          {query.isError && <p className="text-sm text-destructive">{mensagemDeErro(query.error)}</p>}

          {query.data && (
            <div className="space-y-3">
              {responsaveis.length === 0 ? (
                <p className="rounded-lg border border-dashed p-6 text-center text-sm text-muted-foreground">
                  Nenhum responsável cadastrado.
                </p>
              ) : (
                <ul className="space-y-2">
                  {responsaveis.map((r) => (
                    <li key={r.id} className="flex items-center justify-between gap-3 rounded-lg border p-3">
                      <div className="min-w-0">
                        <p className="truncate font-medium">{r.nome}</p>
                        <p className="truncate text-xs text-muted-foreground">
                          {r.grauRelacao}
                          {r.email ? ` • ${r.email}` : ''}
                        </p>
                      </div>
                      <div className="flex shrink-0 gap-1">
                        <Button
                          type="button" variant="ghost" size="icon"
                          aria-label={`Editar relação com ${r.nome}`}
                          onClick={() => setEditor({ mode: 'edit', responsavel: r })}
                        >
                          <Pencil className="size-4" />
                        </Button>
                        <Button
                          type="button" variant="ghost" size="icon" className="text-destructive"
                          aria-label={`Remover ${r.nome}`}
                          onClick={() => setARemover(r)}
                        >
                          <Trash2 className="size-4" />
                        </Button>
                      </div>
                    </li>
                  ))}
                </ul>
              )}

              <Button type="button" variant="outline" className="w-full" onClick={() => setEditor({ mode: 'add' })}>
                <Plus /> Adicionar responsável
              </Button>
            </div>
          )}
        </DialogContent>
      </Dialog>

      {editor && (
        <VinculoResponsavelDialog
          key={editor.mode === 'edit' ? editor.responsavel.id : 'add'}
          open
          onOpenChange={(o) => !o && setEditor(null)}
          nomeFixo={editor.mode === 'edit' ? editor.responsavel.nome : undefined}
          excludeIds={excludeIds}
          initialValues={
            editor.mode === 'edit'
              ? {
                  idResponsavel: editor.responsavel.id,
                  grauRelacao: relacaoDeLabel(editor.responsavel.grauRelacao),
                }
              : { idResponsavel: '', grauRelacao: '' }
          }
          onSubmit={(v) => save.mutateAsync({ idResponsavel: v.idResponsavel, grauRelacao: v.grauRelacao as RelacaoValue })}
        />
      )}

      <AlertDialog open={!!aRemover} onOpenChange={(o) => !o && setARemover(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Remover responsável?</AlertDialogTitle>
            <AlertDialogDescription>
              {aRemover?.nome} deixará de ser responsável por {member.nome}. Os cadastros dos dois continuam existindo.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction
              onClick={async () => {
                if (!aRemover) return
                try {
                  await remove.mutateAsync(aRemover.id)
                  toast.success('Responsável removido.')
                } catch (err) {
                  toast.error(mensagemDeErro(err))
                } finally {
                  setARemover(null)
                }
              }}
            >
              Remover
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  )
}