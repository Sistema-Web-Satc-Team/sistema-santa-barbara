// components/.../vinculos/member-vinculos-drawer.tsx
import type { Member } from '#/api/member'
import type { Vinculo } from '#/api/member-vinculos'
import {
    AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent,
    AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle,
} from '#/components/ui/alert-dialog'
import { Button } from '#/components/ui/button'
import { Sheet, SheetContent } from '#/components/ui/sheet'
import { Spinner } from '#/components/ui/spinner'
import { TooltipProvider } from '#/components/ui/tooltip'
import { useMemberVinculos } from '#/hooks/use-member-vinculos'
import { exibirData, partsParaISO } from '#/lib/datas'

import { mensagemDeErro } from '#/routes/_auth/dashboard/_layout.membros/registrar-membro'
import { Plus } from 'lucide-react'
import { useState, type ReactNode } from 'react'
import { toast } from 'sonner'
import { NovoVinculoForm } from './novo-vinculo-form'
import { VinculoAtivoCard, VinculoCanceladoCard, VinculoEncerradoCard } from './vinculo-card'
import { VinculosHeader } from './vinculos-header'

type Acao = { tipo: 'encerrar' | 'cancelar'; vinculo: Vinculo } | null

function Secao({ titulo, action, children }: { titulo: string; action?: ReactNode; children: ReactNode }) {
  return (
    <section className="space-y-3">
      <div className="flex items-center justify-between">
        <h3 className="text-xs font-medium uppercase tracking-wider text-muted-foreground">{titulo}</h3>
        {action}
      </div>
      {children}
    </section>
  )
}

const Vazio = () => (
  <p className="py-2 text-center text-xs uppercase tracking-widest text-muted-foreground/60">Nenhum</p>
)

interface Props {
  member: Member
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function MemberVinculosDrawer({ member, open, onOpenChange }: Props) {
  const { query, criar, encerrar, cancelar } = useMemberVinculos(member.id, open)
  const [adicionando, setAdicionando] = useState(false)
  const [acao, setAcao] = useState<Acao>(null)

  const data = query.data
  const atuais = data?.vinculos.atuais ?? []

  async function confirmarAcao() {
    if (!acao) return
    try {
      if (acao.tipo === 'encerrar') {
        await encerrar.mutateAsync(acao.vinculo.id)
        toast.success('Vínculo encerrado.')
      } else {
        await cancelar.mutateAsync(acao.vinculo.id)
        toast.success('Vínculo cancelado.')
      }
    } catch (err) {
      toast.error(mensagemDeErro(err))
    } finally {
      setAcao(null)
    }
  }

  return (
    <>
      <Sheet open={open} onOpenChange={onOpenChange}>
        <SheetContent side="right" className="flex w-full flex-col gap-0 p-0 sm:max-w-md">
          <VinculosHeader
            nome={data?.nome ?? member.nome}
            idade={data?.idade ?? member.idade}
            sexo={data?.sexo ?? member.sexo}
            avatarUrl={member.avatarUrl}
          />

          <TooltipProvider>
            <div className="flex-1 space-y-8 overflow-y-auto p-6">
              {query.isLoading && <Spinner />}
              {query.isError && <p className="text-sm text-destructive">{mensagemDeErro(query.error)}</p>}

              {data && (
                <>
                  <Secao
                    titulo="Vínculos atuais"
                    action={
                      !adicionando && (
                        <Button type="button" variant="outline" size="sm" onClick={() => setAdicionando(true)}>
                          <Plus /> Atribuir nova função
                        </Button>
                      )
                    }
                  >
                    {adicionando && (
                      <NovoVinculoForm
                        onCancel={() => setAdicionando(false)}
                        onSubmit={(v) =>
                          criar
                            .mutateAsync({ funcao: v.funcao, dataInicio: exibirData(partsParaISO(v.dataInicio)) })
                            .then(() => undefined)
                        }
                      />
                    )}

                    {atuais.length === 0 ? (
                      <Vazio />
                    ) : (
                      <ul className="space-y-3">
                        {atuais.map((v) => (
                          <VinculoAtivoCard
                            key={v.id}
                            vinculo={v}
                            onEncerrar={(x) => setAcao({ tipo: 'encerrar', vinculo: x })}
                            onCancelar={(x) => setAcao({ tipo: 'cancelar', vinculo: x })}
                          />
                        ))}
                      </ul>
                    )}
                  </Secao>

                  <Secao titulo="Vínculos encerrados">
                    {data.vinculos.encerrados.length === 0 ? (
                      <Vazio />
                    ) : (
                      <ul className="space-y-3">
                        {data.vinculos.encerrados.map((v) => <VinculoEncerradoCard key={v.id} vinculo={v} />)}
                      </ul>
                    )}
                  </Secao>

                  <Secao titulo="Vínculos cancelados">
                    {data.vinculos.cancelados.length === 0 ? (
                      <Vazio />
                    ) : (
                      <ul className="space-y-3">
                        {data.vinculos.cancelados.map((v) => <VinculoCanceladoCard key={v.id} vinculo={v} />)}
                      </ul>
                    )}
                  </Secao>
                </>
              )}
            </div>
          </TooltipProvider>
        </SheetContent>
      </Sheet>

      <AlertDialog open={!!acao} onOpenChange={(o) => !o && setAcao(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>
              {acao?.tipo === 'encerrar' ? 'Encerrar vínculo?' : 'Cancelar vínculo?'}
            </AlertDialogTitle>
            <AlertDialogDescription>
              {acao?.tipo === 'encerrar'
                ? `O vínculo "${acao.vinculo.funcao.nome}" (desde ${exibirData(acao.vinculo.dataInicio)}) será encerrado e passará para o histórico.`
                : acao
                  ? `O vínculo agendado "${acao.vinculo.funcao.nome}" será cancelado.`
                  : ''}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Voltar</AlertDialogCancel>
            <AlertDialogAction onClick={confirmarAcao}>
              {acao?.tipo === 'encerrar' ? 'Encerrar' : 'Cancelar vínculo'}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  )
}