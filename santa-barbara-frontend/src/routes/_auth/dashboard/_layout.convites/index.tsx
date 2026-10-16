import type { Convite } from '#/api/convites'
import { ConviteFormDialog } from '#/components/business/convites/convite-form-dialog'
import type { CanalValue } from '#/components/business/convites/convites'
import { ConvitesFilters } from '#/components/business/convites/convites-filters'
import { ConvitesTable } from '#/components/business/convites/convites-table'
import { Button } from '#/components/ui/button'
import { Spinner } from '#/components/ui/spinner'
import { useConvites } from '#/hooks/use-convites'
import { useDebounce } from '#/hooks/use-debounce'
import { createFileRoute } from '@tanstack/react-router'
import { Plus } from 'lucide-react'
import { useEffect, useState } from 'react'
import { toast } from 'sonner'
import { z } from 'zod'
import { mensagemDeErro } from '../_layout.membros/registrar-membro'

const PAGE_SIZE = 20

const searchSchema = z.object({
  status: z.enum(['ENVIADO', 'ACEITO', 'EXPIRADO']).optional().catch(undefined),
  search: z.string().optional().catch(undefined),
  page: z.number().int().min(0).catch(0),
})

export const Route = createFileRoute('/_auth/dashboard/_layout/convites/')({
  validateSearch: (s: Record<string, unknown>) => searchSchema.parse(s),
  component: ConvitesPage,
})

function ConvitesPage() {
  const { status, search, page } = Route.useSearch()
  const navigate = Route.useNavigate()

  const [termo, setTermo] = useState(search ?? '')
  const [dialogAberto, setDialogAberto] = useState(false)
  const debounced = useDebounce(termo.trim(), 300)

  useEffect(() => {
    if ((debounced || undefined) !== search) {
      navigate({ search: (p) => ({ ...p, search: debounced || undefined, page: 0 }), replace: true })
    }
  }, [debounced])

  const { query, criar, reenviar } = useConvites({ status, search, page, size: PAGE_SIZE })
  const data = query.data

  async function copiar(c: Convite) {
    try {
      await navigator.clipboard.writeText(c.conviteURL)
      toast.success('Link copiado.')
    } catch {
      toast.error('Não foi possível copiar o link.')
    }
  }

  async function reenviarConvite(c: Convite, canal: CanalValue) {
    try {
      await reenviar.mutateAsync({ idConvite: c.idConvite, canal })
      toast.success(`Convite reenviado para ${c.membro.nome}.`)
    } catch (err) {
      toast.error(mensagemDeErro(err))
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold">Convites</h1>
          <p className="text-sm text-muted-foreground">Acompanhe e envie convites de acesso ao sistema.</p>
        </div>
        <Button onClick={() => setDialogAberto(true)}>
          <Plus /> Convidar membro
        </Button>
      </div>

      <ConvitesFilters
        status={status}
        onStatusChange={(s) => navigate({ search: (p) => ({ ...p, status: s, page: 0 }) })}
        termo={termo}
        onTermoChange={setTermo}
      />

      {query.isLoading && <Spinner />}
      {query.isError && <p className="text-sm text-destructive">{mensagemDeErro(query.error)}</p>}

      {data && (
        <>
          <ConvitesTable convites={data.content} onCopiar={copiar} onReenviar={reenviarConvite} />

          {data.totalPages > 1 && (
            <div className="flex items-center justify-between text-sm text-muted-foreground">
              <span>Página {data.page + 1} de {data.totalPages}</span>
              <div className="flex gap-2">
                <Button
                  variant="outline" size="sm" disabled={page === 0}
                  onClick={() => navigate({ search: (p) => ({ ...p, page: page - 1 }) })}
                >
                  Anterior
                </Button>
                <Button
                  variant="outline" size="sm" disabled={page + 1 >= data.totalPages}
                  onClick={() => navigate({ search: (p) => ({ ...p, page: page + 1 }) })}
                >
                  Próxima
                </Button>
              </div>
            </div>
          )}
        </>
      )}

      <ConviteFormDialog open={dialogAberto} onOpenChange={setDialogAberto} onSubmit={criar.mutateAsync} />
    </div>
  )
}