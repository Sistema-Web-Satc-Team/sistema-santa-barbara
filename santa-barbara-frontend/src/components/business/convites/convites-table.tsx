import type { Convite } from '#/api/convites'
import { Badge } from '#/components/ui/badge'
import { Button } from '#/components/ui/button'
import {
    DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel,
    DropdownMenuSeparator, DropdownMenuTrigger,
} from '#/components/ui/dropdown-menu'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '#/components/ui/table'
import { capitalizeAll } from '#/lib/utils'
import { Copy, Ellipsis, Mail, Send } from 'lucide-react'
import { CANAIS, type CanalValue, STATUS_META, formatarDataHora, statusEfetivo } from './convites'

interface Props {
  convites: Convite[]
  onCopiar: (c: Convite) => void
  onReenviar: (c: Convite, canal: CanalValue) => void
}

export function ConvitesTable({ convites, onCopiar, onReenviar }: Props) {
  if (convites.length === 0) {
    return (
      <div className="flex flex-col items-center rounded-xl border border-dashed bg-card px-6 py-16 text-center">
        <span className="mb-4 inline-flex size-14 items-center justify-center rounded-full bg-primary/10 text-primary">
          <Mail className="size-6" aria-hidden="true" />
        </span>
        <h3 className="text-lg font-semibold">Nenhum convite encontrado</h3>
        <p className="mt-1 max-w-sm text-sm text-muted-foreground">
          Convide um membro ou ajuste a busca e os filtros.
        </p>
      </div>
    )
  }

  return (
    <div className="w-full overflow-hidden rounded-xl border bg-card shadow-xs">
      <Table>
        <TableHeader className="bg-muted/60">
          <TableRow className="hover:bg-transparent">
            {['Membro', 'Contato', 'Enviado em', 'Expira em', 'Status'].map((t) => (
              <TableHead key={t} className="h-11 px-4 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                {t}
              </TableHead>
            ))}
            <TableHead className="h-11 w-16 px-4 text-right text-xs font-semibold uppercase tracking-wider text-muted-foreground">
              Ações
            </TableHead>
          </TableRow>
        </TableHeader>

        <TableBody>
          {convites.map((c) => {
            const status = statusEfetivo(c)
            const meta = STATUS_META[status] ?? { label: status, variant: 'muted' as const }
            const aceito = status === 'ACEITO'

            return (
              <TableRow key={c.idConvite}>
                <TableCell className="px-4 py-3 font-medium">
                  {capitalizeAll(c.membro.nome.toLowerCase())}
                </TableCell>
                <TableCell className="px-4 py-3 text-sm text-muted-foreground">
                  {c.membro.email || c.membro.telefone || '-'}
                </TableCell>
                <TableCell className="px-4 py-3 text-sm tabular-nums">{formatarDataHora(c.criadoEm)}</TableCell>
                <TableCell className="px-4 py-3 text-sm tabular-nums">
                  {aceito ? '-' : formatarDataHora(c.expiraEm)}
                </TableCell>
                <TableCell className="px-4 py-3">
                  <Badge variant={meta.variant}>{meta.label}</Badge>
                </TableCell>
                <TableCell className="px-4 py-2 text-right">
                  {!aceito && (
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="ghost" size="icon" aria-label={`Ações do convite de ${c.membro.nome}`}>
                          <Ellipsis className="size-5" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end" className="w-52">
                        <DropdownMenuItem onSelect={() => onCopiar(c)}>
                          <Copy /> Copiar link
                        </DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuLabel className="text-xs font-normal text-muted-foreground">
                          Reenviar por
                        </DropdownMenuLabel>
                        {CANAIS.map((canal) => (
                          <DropdownMenuItem key={canal.value} onSelect={() => onReenviar(c, canal.value)}>
                            <Send /> {canal.label}
                          </DropdownMenuItem>
                        ))}
                      </DropdownMenuContent>
                    </DropdownMenu>
                  )}
                </TableCell>
              </TableRow>
            )
          })}
        </TableBody>
      </Table>
    </div>
  )
}