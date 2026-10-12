import { getInstrumentTypeLabel } from '#/api/instrumento'
import type { Instrumento } from '#/api/instrumento'
import { InstrumentIcon } from '#/components/business/instrument-icon'
import { Card, CardContent } from '#/components/ui/card'
import type { ReactNode } from 'react'

interface InstrumentCardProps {
  instrumento: Pick<Instrumento, 'nome' | 'descricao' | 'tipo'>
  /** Slot opcional para botões (editar, remover...). Fica no canto do card. */
  actions?: ReactNode
}

export function InstrumentCard({ instrumento, actions }: InstrumentCardProps) {
  const description = instrumento.descricao?.trim()

  return (
    <Card>
      <CardContent className="flex gap-4">
        <div className="flex size-12 shrink-0 items-center justify-center rounded-lg bg-muted">
          <InstrumentIcon tipo={instrumento.tipo} className="size-6" />
        </div>

        <div className="min-w-0 flex-1 space-y-1">
          <h3 className="truncate font-semibold">{instrumento.nome}</h3>
          <p className="text-xs uppercase tracking-wide text-muted-foreground">
            {getInstrumentTypeLabel(instrumento.tipo)}
          </p>
          <p className="text-sm text-muted-foreground">
            {description ? description : 'Sem descrição'}
          </p>
        </div>

        {actions && <div className="flex shrink-0 items-start gap-1">{actions}</div>}
      </CardContent>
    </Card>
  )
}
