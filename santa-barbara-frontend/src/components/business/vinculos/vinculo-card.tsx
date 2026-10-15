import type { Vinculo } from '#/api/member-vinculos'
import { Badge } from '#/components/ui/badge'
import { Button } from '#/components/ui/button'
import { Tooltip, TooltipContent, TooltipTrigger } from '#/components/ui/tooltip'
import { duracao, exibirData } from '#/lib/datas'
import { Trash2 } from 'lucide-react'
import type { ReactNode } from 'react'

function CardBase({ funcao, descricao, children, footer }: {
  funcao: string
  descricao?: string
  children: ReactNode
  footer?: ReactNode
}) {
  return (
    <li className="rounded-xl border bg-card p-4">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <h4 className="text-base font-semibold leading-tight">{funcao}</h4>
          {descricao && <p className="mt-0.5 line-clamp-1 text-xs text-muted-foreground">{descricao}</p>}
          <p className="mt-2 text-xs text-muted-foreground">{children}</p>
        </div>
      </div>
      {footer && <div className="mt-4 flex items-center justify-end gap-2">{footer}</div>}
    </li>
  )
}

/* --- Ativo / Agendado --- */

interface AtivoProps {
  vinculo: Vinculo
  onEncerrar: (v: Vinculo) => void
  onCancelar: (v: Vinculo) => void
}

export function VinculoAtivoCard({ vinculo, onEncerrar, onCancelar }: AtivoProps) {
  const agendado = vinculo.status === 'AGENDADO'

  return (
    <CardBase
      funcao={vinculo.funcao.nome}
      descricao={vinculo.funcao.descricao}
      footer={
        <>
          {agendado ? (
            <Button
              type="button" variant="ghost" size="icon"
              className="text-destructive"
              aria-label={`Cancelar vínculo ${vinculo.funcao.nome}`}
              onClick={() => onCancelar(vinculo)}
            >
              <Trash2 className="size-4" />
            </Button>
          ) : (
            <Tooltip>
              <TooltipTrigger asChild>
                <span tabIndex={0} className="inline-flex">
                  <Button
                    type="button" variant="ghost" size="icon"
                    aria-disabled="true"
                    aria-label="Não é possível cancelar vínculos ativos"
                    className="cursor-not-allowed text-muted-foreground/50 hover:bg-transparent hover:text-muted-foreground/50"
                    onClick={(e) => e.preventDefault()}
                  >
                    <Trash2 className="size-4" />
                  </Button>
                </span>
              </TooltipTrigger>
              <TooltipContent>Não é possível cancelar ou remover vínculos ativos</TooltipContent>
            </Tooltip>
          )}

          {!agendado && (
            <Button
              type="button" variant="outline" size="sm"
              className="border-destructive/60 text-destructive hover:bg-destructive/10"
              onClick={() => onEncerrar(vinculo)}
            >
              Encerrar vínculo
            </Button>
          )}
        </>
      }
    >
      {agendado ? (
        <>
          <Badge variant="info" className="mr-2">Agendado</Badge>
          Começa em {exibirData(vinculo.dataInicio)}
        </>
      ) : (
        <>
          Desde {exibirData(vinculo.dataInicio)} • Há {duracao(vinculo.dataInicio)}
        </>
      )}
    </CardBase>
  )
}

/* --- Encerrado --- */

export function VinculoEncerradoCard({ vinculo }: { vinculo: Vinculo }) {
  return (
    <li className="rounded-xl border bg-card p-4">
      <div className="flex items-start justify-between gap-3">
        <h4 className="text-sm font-semibold">{vinculo.funcao.nome}</h4>
        {vinculo.dataTermino && (
          <Badge variant="muted">{duracao(vinculo.dataInicio, vinculo.dataTermino)}</Badge>
        )}
      </div>
      <p className="mt-1 text-xs text-muted-foreground">
        De {exibirData(vinculo.dataInicio)}
        {vinculo.dataTermino ? ` até ${exibirData(vinculo.dataTermino)}` : ''}
      </p>
    </li>
  )
}

/* --- Cancelado --- */

export function VinculoCanceladoCard({ vinculo }: { vinculo: Vinculo }) {
  return (
    <li className="rounded-xl border border-dashed bg-card/50 p-4 opacity-70">
      <div className="flex items-start justify-between gap-3">
        <h4 className="text-sm font-semibold line-through">{vinculo.funcao.nome}</h4>
        <Badge variant="danger">Cancelado</Badge>
      </div>
      <p className="mt-1 text-xs text-muted-foreground">
        Previsto para {exibirData(vinculo.dataInicio)}
      </p>
    </li>
  )
}