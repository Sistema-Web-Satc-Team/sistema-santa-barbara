import { InstrumentIcon } from '#/components/business/instrument-icon'
import { Badge } from '#/components/ui/badge'

interface InstrumentBadgeProps {
  nome: string
  tipo: string
}

/** [ (ícone) nome do instrumento ] */
export function InstrumentBadge({ nome, tipo }: InstrumentBadgeProps) {
  return (
    <Badge variant="secondary" className="gap-1.5">
      <InstrumentIcon tipo={tipo} className="size-3.5" />
      {nome}
    </Badge>
  )
}
