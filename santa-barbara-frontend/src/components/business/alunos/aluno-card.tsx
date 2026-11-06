import type { Aluno } from '#/api/alunos'
import {
  AlunoAvatar,
  AlunoDetailsDialog,
  formatSexo,
} from '#/components/business/alunos/aluno-details-dialog'
import { formatPhone } from '#/components/business/membros/members'
import { Badge } from '#/components/ui/badge'
import { Button } from '#/components/ui/button'
import { Card, CardContent } from '#/components/ui/card'
import { Separator } from '#/components/ui/separator'
import { capitalizeAll, cn } from '#/lib/utils'
import { Eye, GraduationCap, Mail, Phone } from 'lucide-react'
import { useState } from 'react'

export interface AlunoCardProps {
  aluno: Aluno
  onViewDetails: (aluno: Aluno) => void
}

export function AlunoCard({ aluno, onViewDetails }: AlunoCardProps) {
  const nome = capitalizeAll(String(aluno.nome).toLowerCase())
  const isMinor = Number(aluno.idade) < 18
  const telefone = aluno.telefone ? formatPhone(aluno.telefone) : 'Não informado'

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

              <Button
                type="button"
                variant="ghost"
                size="icon"
                className="size-7 shrink-0 text-muted-foreground hover:text-foreground"
                aria-label={`Ver detalhes de ${nome}`}
                title="Ver mais detalhes"
                onClick={() => onViewDetails(aluno)}
              >
                <Eye className="size-4" />
              </Button>
            </div>

            <p className="mt-0.5 text-xs text-muted-foreground">
              {aluno.idade} {aluno.idade === 1 ? 'ano' : 'anos'} • Sexo:{' '}
              {formatSexo(String(aluno.sexo))}
            </p>

             
              
            <div className="mt-2 flex flex-wrap items-center gap-1.5">

              {/* 
              MODULO DE TURMAS NAO IMPLEMENTADO

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
              */}

              {isMinor && (
                <Badge variant="warning" className="shrink-0">
                  Menor de 18 anos
                </Badge>
              )}
            </div>
  
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

        <Button
          type="button"
          variant="outline"
          size="sm"
          className="mt-auto w-full gap-2"
          onClick={() => onViewDetails(aluno)}
        >
          <Eye className="size-4" />
          Ver mais
        </Button>
      </CardContent>
    </Card>
  )
}

export interface AlunosGridProps {
  alunos: Aluno[]
  hasFilters?: boolean
}

export function AlunosGrid({ alunos, hasFilters = false }: AlunosGridProps) {
  const [selectedAluno, setSelectedAluno] = useState<Aluno | null>(null)

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
    <>
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">
        {alunos.map((aluno, index) => (
          <AlunoCard
            key={aluno.id ?? index}
            aluno={aluno}
            onViewDetails={setSelectedAluno}
          />
        ))}
      </div>

      <AlunoDetailsDialog
        aluno={selectedAluno}
        open={Boolean(selectedAluno)}
        onOpenChange={(open) => {
          if (!open) setSelectedAluno(null)
        }}
      />
    </>
  )
}
