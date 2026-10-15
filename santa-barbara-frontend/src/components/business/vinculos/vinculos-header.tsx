import { SheetDescription, SheetHeader, SheetTitle } from '#/components/ui/sheet'
import { capitalizeAll } from '#/lib/utils'

function initials(name: string) {
  const p = name.trim().split(/\s+/).filter(Boolean)
  if (!p.length) return '?'
  return ((p[0][0] ?? '') + (p.length > 1 ? p[p.length - 1][0] : '')).toUpperCase()
}

interface Props {
  nome: string
  idade?: number
  sexo?: string
  avatarUrl?: string | null
}

export function VinculosHeader({ nome, idade, sexo, avatarUrl }: Props) {
  return (
    <SheetHeader className="flex-row items-center gap-4 border-b p-6">
      <span
        aria-hidden="true"
        className="inline-flex size-14 shrink-0 items-center justify-center overflow-hidden rounded-full bg-primary/10 text-lg font-bold text-primary"
      >
        {avatarUrl ? <img src={avatarUrl} alt="" className="size-full object-cover" /> : initials(nome)}
      </span>

      <div className="min-w-0">
        <SheetDescription className="text-xs uppercase tracking-wide">
          Responsabilidades, funções e vínculos
        </SheetDescription>
        <SheetTitle className="truncate text-lg">{capitalizeAll(nome.toLowerCase())}</SheetTitle>
        {idade !== undefined && (
          <p className="text-xs text-muted-foreground">
            {idade} anos{sexo ? ` • sexo ${sexo.toLowerCase()}` : ''}
          </p>
        )}
      </div>
    </SheetHeader>
  )
}