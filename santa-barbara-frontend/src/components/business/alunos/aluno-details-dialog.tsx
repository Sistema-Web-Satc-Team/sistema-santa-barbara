import type { Aluno, AlunoResponsavel } from '#/api/alunos'
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
import { normalizeText } from '#/lib/text'
import { capitalizeAll, cn } from '#/lib/utils'
import { Guitar, Mail, Phone, UsersRound } from 'lucide-react'

const SEXO_LABEL: Record<string, string> = {
  MASCULINO: 'Masculino',
  FEMININO: 'Feminino',
  NAO_INFORMADO: 'Não informado',
  'NAO INFORMADO': 'Não informado',
}

const RELACAO_LABEL: Record<string, string> = {
  mae: 'Mãe',
  pai: 'Pai',
  avo: 'Avô/Avó',
  tio: 'Tio/Tia',
  tia: 'Tia',
  padrinho: 'Padrinho',
  madrinha: 'Madrinha',
  progenitores: 'Pai/Mãe',
  responsavel_legal: 'Responsável legal',
  'responsavel legal': 'Responsável legal',
}

export function formatSexo(sexo: string): string {
  const key = sexo?.toUpperCase().trim()
  if (SEXO_LABEL[key]) return SEXO_LABEL[key]
  return capitalizeAll(sexo.toLowerCase())
}

export function formatRelacao(relacao?: string | null): string {
  if (!relacao) return 'Responsável'
  const norm = normalizeText(relacao)
  if (RELACAO_LABEL[norm]) return RELACAO_LABEL[norm]
  return capitalizeAll(relacao.toLowerCase())
}

export function AlunoAvatar({
  name,
  minor,
}: {
  name: string
  minor: boolean
}) {
  return (
    <span
      aria-hidden="true"
      className={cn(
        'inline-flex size-12 shrink-0 items-center justify-center rounded-full text-sm font-bold overflow-hidden',
        minor ? 'bg-warning/15 text-warning' : 'bg-primary/10 text-primary',
      )}
    >
      {initials(name)}
    </span>
  )
}

function ResponsavelBox({ responsavel }: { responsavel: AlunoResponsavel }) {
  const nome = capitalizeAll(String(responsavel.nome).toLowerCase())
  const relacao = formatRelacao(responsavel.relacao)
  const telefone = responsavel.telefone ? formatPhone(responsavel.telefone) : null

  return (
    <div className="flex flex-col gap-1.5 rounded-lg border bg-muted/30 p-3 text-xs">
      <div className="flex items-center justify-between gap-2">
        <span className="font-semibold text-foreground truncate text-sm">
          {nome}
        </span>
        <Badge variant="outline" className="px-2 py-0.5 text-[11px] shrink-0">
          {relacao}
        </Badge>
      </div>
      <div className="flex flex-wrap items-center gap-x-4 gap-y-1 text-muted-foreground">
        {telefone && (
          <span className="inline-flex items-center gap-1.5 tabular-nums">
            <Phone className="size-3.5 shrink-0" aria-hidden="true" />
            {telefone}
          </span>
        )}
        {responsavel.email && (
          <span className="inline-flex items-center gap-1.5 min-w-0 truncate">
            <Mail className="size-3.5 shrink-0" aria-hidden="true" />
            <span className="truncate">{responsavel.email}</span>
          </span>
        )}
        {!telefone && !responsavel.email && (
          <span className="italic">Sem contato informado</span>
        )}
      </div>
    </div>
  )
}

export interface AlunoDetailsDialogProps {
  aluno: Aluno | null
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function AlunoDetailsDialog({
  aluno,
  open,
  onOpenChange,
}: AlunoDetailsDialogProps) {
  if (!aluno) return null

  const nome = capitalizeAll(String(aluno.nome).toLowerCase())
  const isMinor = Number(aluno.idade) < 18
  const telefone = aluno.telefone ? formatPhone(aluno.telefone) : 'Não informado'
  const instrumentos = aluno.instrumentos?.aprendendo ?? []
  const responsaveis = aluno.responsaveis ?? []

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader className="text-left">
          <div className="flex items-start gap-3.5 pr-6">
            <AlunoAvatar name={nome} minor={isMinor} />
            <div className="min-w-0 flex-1">
              <DialogTitle className="truncate text-lg font-semibold leading-tight text-foreground">
                {nome}
              </DialogTitle>
              <DialogDescription className="mt-1 text-xs text-muted-foreground">
                {aluno.idade} {aluno.idade === 1 ? 'ano' : 'anos'} • Sexo:{' '}
                {formatSexo(String(aluno.sexo))}
              </DialogDescription>

              <div className="mt-2 flex flex-wrap items-center gap-1.5">
                {aluno.temTurma ? (
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

                {isMinor && <Badge variant="warning">Menor de 18 anos</Badge>}
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
                aluno.email
                  ? 'text-foreground font-medium'
                  : 'text-muted-foreground italic',
              )}
            >
              {aluno.email || 'E-mail não informado'}
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
            <span>Instrumentos</span>
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
              Nenhum instrumento de interesse
            </p>
          )}
        </div>

        <Separator />

        <div className="space-y-2.5">
          <div className="flex items-center gap-1.5 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            <UsersRound className="size-3.5" aria-hidden="true" />
            <span>Responsável Legal</span>
          </div>

          {responsaveis.length > 0 ? (
            <div className="flex flex-col gap-2">
              {responsaveis.map((resp, idx) => (
                <ResponsavelBox key={resp.id ?? idx} responsavel={resp} />
              ))}
            </div>
          ) : (
            <p className="rounded-lg border border-dashed bg-muted/20 px-3 py-2.5 text-xs text-muted-foreground">
              Sem responsáveis
            </p>
          )}
        </div>
      </DialogContent>
    </Dialog>
  )
}
