import { Button } from '#/components/ui/button'
import { Input } from '#/components/ui/input'
import { Search } from 'lucide-react'
import { STATUS_FILTROS, type StatusFiltro } from './convites'

interface Props {
  status?: StatusFiltro
  onStatusChange: (s?: StatusFiltro) => void
  termo: string
  onTermoChange: (t: string) => void
}

export function ConvitesFilters({ status, onStatusChange, termo, onTermoChange }: Props) {
  return (
    <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
      <div className="flex flex-wrap gap-2" role="group" aria-label="Filtrar por status">
        <Button
          type="button" size="sm"
          variant={!status ? 'default' : 'outline'}
          onClick={() => onStatusChange(undefined)}
        >
          Todos
        </Button>
        {STATUS_FILTROS.map((s) => (
          <Button
            key={s.value} type="button" size="sm"
            variant={status === s.value ? 'default' : 'outline'}
            aria-pressed={status === s.value}
            onClick={() => onStatusChange(s.value)}
          >
            {s.label}
          </Button>
        ))}
      </div>

      <div className="relative w-full sm:max-w-xs">
        <Search className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          className="pl-9"
          placeholder="Buscar por nome ou e-mail"
          value={termo}
          onChange={(e) => onTermoChange(e.target.value)}
        />
      </div>
    </div>
  )
}