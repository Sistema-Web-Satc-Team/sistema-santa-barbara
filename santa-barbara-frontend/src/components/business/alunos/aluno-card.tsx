import type { Aluno, AlunoResponsavel } from '#/api/alunos'
import { formatPhone, initials } from '#/components/business/membros/members'
import { Badge } from '#/components/ui/badge'
import { Card, CardContent } from '#/components/ui/card'
import { Separator } from '#/components/ui/separator'
import { normalizeText } from '#/lib/text'
import { capitalizeAll, cn } from '#/lib/utils'
import {
  GraduationCap,
  Guitar,
  Mail,
  Phone,
  ShieldAlert,
  UsersRound,
} from 'lucide-react'

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

function formatSexo(sexo: string): string {
  const key = sexo?.toUpperCase().trim()
  if (SEXO_LABEL[key]) return SEXO_LABEL[key]
  return capitalizeAll(sexo.toLowerCase())
}

function formatRelacao(relacao?: string | null): string {
  if (!relacao) return 'Responsável'
  const norm = normalizeText(relacao)
  if (RELACAO_LABEL[norm]) return RELACAO_LABEL[norm]
  return capitalizeAll(relacao.toLowerCase())
}

function AlunoAvatar({ name, minor }: { name: string; minor: boolean }) {
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
    <div className="flex flex-col gap-1 rounded-lg border bg-muted/30 p-2.5 text-xs">
      <div className="flex items-center justify-between gap-2">
        <span className="font-semibold text-foreground truncate">{nome}</span>
        <Badge variant="outline" className="px-1.5 py-0 text-[10px] shrink-0">
          {relacao}
        </Badge>
      </div>
      <div className="flex flex-wrap items-center gap-x-3 gap-y-1 text-muted-foreground">
        {telefone && (
          <span className="inline-flex items-center gap-1 tabular-nums">
            <Phone className="size-3 shrink-0" aria-hidden="true" />
            {telefone}
          </span>
        )}
        {responsavel.email && (
          <span className="inline-flex items-center gap-1 min-w-0 truncate">
            <Mail className="size-3 shrink-0" aria-hidden="true" />
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

export interface AlunoCardProps {
  aluno: Aluno
}

export function AlunoCard({ aluno }: AlunoCardProps) {
  const nome = capitalizeAll(String(aluno.nome).toLowerCase())
  const isMinor = Number(aluno.idade) < 18
  const telefone = aluno.telefone ? formatPhone(aluno.telefone) : 'Não informado'
  const instrumentos = aluno.instrumentos?.aprendendo ?? []
  const responsaveis = aluno.responsaveis ?? []

  return (
    <Card className="h-full py-5">
      <CardContent className="flex h-full flex-col gap-4">
        <div className="flex items-start gap-3.5">
          <AlunoAvatar name={nome} minor={isMinor} />

          <div className="min-w-0 flex-1">
            <div className="flex items-start justify-between gap-2">
              <h3 className="truncate text-base font-semibold leading-tight text-foreground">
                {nome}
              </h3>

              {aluno.temTurma ? (
                <Badge variant="success" className="shrink-0">
                  <span
                    className="size-1.5 rounded-full bg-current"
                    aria-hidden="true"
                  />
                  Em Turma
                </Badge>
              ) : (
                <Badge variant="muted" className="shrink-0">
                  <span
                    className="size-1.5 rounded-full bg-current"
                    aria-hidden="true"
                  />
                  Sem Turma
                </Badge>
              )}
            </div>

            <p className="mt-1 text-xs text-muted-foreground">
              {aluno.idade} {aluno.idade === 1 ? 'ano' : 'anos'} • Sexo:{' '}
              {formatSexo(String(aluno.sexo))}
            </p>

            {isMinor && (
              <div className="mt-2 flex flex-wrap items-center gap-1.5">
                <Badge variant="warning">Menor de 18 anos</Badge>
              </div>
            )}
          </div>
        </div>

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
                aluno.email ? 'text-foreground font-medium' : 'text-muted-foreground italic',
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

        <div className="space-y-2">
          <div className="flex items-center gap-1.5 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
            <Guitar className="size-3.5" aria-hidden="true" />
            <span>Instrumentos aprendendo</span>
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
            <p className="text-xs italic text-muted-foreground">
              Nenhum instrumento vinculado
            </p>
          )}
        </div>

        {isMinor && (
          <>
            <Separator />
            <div className="space-y-2">
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
                <div className="inline-flex items-center gap-1.5 text-xs text-warning">
                  <ShieldAlert className="size-3.5 shrink-0" />
                  <span>Responsável não informado</span>
                </div>
              )}
            </div>
          </>
        )}
      </CardContent>
    </Card>
  )
}

export interface AlunosGridProps {
  alunos: Aluno[]
  hasFilters?: boolean
}

export function AlunosGrid({ alunos, hasFilters = false }: AlunosGridProps) {
  if (alunos.length === 0) {
    return (
      <div className="flex flex-col items-center rounded-xl border border-dashed bg-card px-6 py-16 text-center">
        <span className="mb-4 inline-flex size-14 items-center justify-center rounded-full bg-primary/10 text-primary">
          <GraduationCap className="size-6" aria-hidden="true" />
        </span>
        <h3 className="font-display text-lg font-semibold text-foreground">
          Nenhum aluno encontrado
        </h3>
        <p className="mt-1 max-w-sm text-sm text-muted-foreground">
          {hasFilters
            ? 'Nenhum aluno corresponde aos filtros selecionados. Tente ajustar a busca ou limpar os filtros.'
            : 'Não há alunos cadastrados no momento.'}
        </p>
      </div>
    )
  }

  return (
    <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
      {alunos.map((aluno, index) => (
        <AlunoCard key={aluno.id ?? index} aluno={aluno} />
      ))}
    </div>
  )
}
