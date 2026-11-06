import { getInstrumentTypeLabel } from '#/api/instrumento'
import type { Instrumento } from '#/api/instrumento'
import { InstrumentIcon } from '#/components/business/instrument-icon'
import { Card, CardContent } from '#/components/ui/card'
import { capitalize, capitalizeAll } from '#/lib/utils'
import type { ReactNode } from 'react'
import { Badge } from '../ui/badge'

interface InstrumentCardProps {
  instrumento: Pick<Instrumento, 'nome' | 'descricao' | 'tipo'>

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
          <h3 className="truncate font-semibold">{capitalizeAll(instrumento.nome)}</h3>
          <Badge className="text-xs">
            {getInstrumentTypeLabel(instrumento.tipo)}
          </Badge>
          <p className="text-sm text-muted-foreground">
            {description ? capitalize(description) : 'Sem descrição'}
          </p>
        </div>

        {actions && <div className="flex shrink-0 items-start gap-1">{actions}</div>}
      </CardContent>
    </Card>
  )
}
