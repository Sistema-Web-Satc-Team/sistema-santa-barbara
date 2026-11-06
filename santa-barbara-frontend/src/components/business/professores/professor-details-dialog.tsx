import type { Professor } from '#/api/professores'
import { formatPhone, initials } from '#/components/business/membros/members'
import { Badge } from '#/components/ui/badge'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '#/components/ui/dialog'
import { Separator } from '#/components/ui/separator'
import { capitalizeAll, cn } from '#/lib/utils'
import { Guitar, Mail, Phone } from 'lucide-react'

const SEXO_LABEL: Record<string, string> = {
  MASCULINO: 'Masculino',
  FEMININO: 'Feminino',
  NAO_INFORMADO: 'Não informado',
  'NAO INFORMADO': 'Não informado',
}

export function formatSexoProfessor(sexo: string): string {
  const key = sexo?.toUpperCase().trim()
  if (SEXO_LABEL[key]) return SEXO_LABEL[key]
  return capitalizeAll(sexo.toLowerCase())
}

export function ProfessorAvatar({ name }: { name: string }) {
  return (
    <span
      aria-hidden="true"
      className="inline-flex size-12 shrink-0 items-center justify-center rounded-full bg-primary/10 text-sm font-bold text-primary overflow-hidden"
    >
      {initials(name)}
    </span>
  )
}

export interface ProfessorDetailsDialogProps {
  professor: Professor | null
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function ProfessorDetailsDialog({
  professor,
  open,
  onOpenChange,
}: ProfessorDetailsDialogProps) {
  if (!professor) return null

  const nome = capitalizeAll(String(professor.nome).toLowerCase())
  const telefone = professor.telefone
    ? formatPhone(professor.telefone)
    : 'Não informado'
  const instrumentos = professor.instrumentos?.ensinando ?? []

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader className="text-left">
          <div className="flex items-start gap-3.5 pr-6">
            <ProfessorAvatar name={nome} />
            <div className="min-w-0 flex-1">
              <DialogTitle className="truncate text-lg font-semibold leading-tight text-foreground">
                {nome}
              </DialogTitle>
              <DialogDescription className="mt-1 text-xs text-muted-foreground">
                Sexo: {formatSexoProfessor(String(professor.sexo))}
              </DialogDescription>

              <div className="mt-2 flex flex-wrap items-center gap-1.5">

                {/* 
                
                MODULO DE TURMAS NAO IMPLEMENTADO

                {professor.temTurma ? (
                  <Badge variant="success">
                    <span
                      className="size-1.5 rounded-full bg-current"
                      aria-hidden="true"
                    />
                    Em Turma
                  </Badge>
                ) : (
                  <Badge variant="muted">
                    <span
                      className="size-1.5 rounded-full bg-current"
                      aria-hidden="true"
                    />
                    Sem Turma
                  </Badge>
                )}

                */}
              </div>
            </div>
          </div>
        </DialogHeader>

        <Separator />

        <div className="space-y-1.5 text-sm">
          <div className="flex min-w-0 items-center gap-2">
            <Mail
              className="size-4 shrink-0 text-muted-foreground"
              aria-hidden="true"
            />
            <span className="sr-only">E-mail:</span>
            <span
              className={cn(
                'min-w-0 truncate',
                professor.email
                  ? 'text-foreground font-medium'
                  : 'text-muted-foreground italic',
              )}
            >
              {professor.email || 'E-mail não informado'}
            </span>
          </div>

          <div className="flex min-w-0 items-center gap-2">
            <Phone
              className="size-4 shrink-0 text-muted-foreground"
              aria-hidden="true"
            />
            <span className="sr-only">Telefone:</span>
            <span className="tabular-nums font-medium text-foreground">
              {telefone}
            </span>
          </div>
        </div>

        <Separator />

        <div className="space-y-2.5">
          <div className="flex items-center gap-1.5 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            <Guitar className="size-3.5" aria-hidden="true" />
            <span>Instrumentos que ensina</span>
          </div>

          {instrumentos.length > 0 ? (
            <div className="flex flex-wrap gap-1.5">
              {instrumentos.map((inst) => (
                <Badge key={inst} variant="secondary" className="gap-1">
                  <Guitar className="size-3" aria-hidden="true" />
                  {capitalizeAll(inst)}
                </Badge>
              ))}
            </div>
          ) : (
            <p className="rounded-lg border border-dashed bg-muted/20 px-3 py-2.5 text-xs text-muted-foreground">
              Nenhum instrumento ensinado no momento
            </p>
          )}
        </div>
      </DialogContent>
    </Dialog>
  )
}
