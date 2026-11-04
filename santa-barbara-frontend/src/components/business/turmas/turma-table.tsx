import { Ban, EllipsisVertical, Eye, OctagonX, Pencil } from "lucide-react";
import type { TurmaResumo } from "#/api/turmas";
import { Button } from "#/components/ui/button";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "#/components/ui/dropdown-menu";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "#/components/ui/table";
import { acoesPermitidas, formatarData, TURNO_LABEL } from "#/lib/turmas";
import { TurmaOcupacao } from "./turma-ocupacao";
import { TurmaStatusBadge } from "./turma-status-badge";

interface TurmaTableProps {
    turmas: TurmaResumo[];
    onDetalhes: (turma: TurmaResumo) => void;
    onEditar: (turma: TurmaResumo) => void;
    onCancelar: (turma: TurmaResumo) => void;
    onInterromper: (turma: TurmaResumo) => void;
}

function Periodo({ turma }: { turma: TurmaResumo }) {
    if (!turma.dataInicio && !turma.dataFim) {
        return <span className="text-muted-foreground">A definir</span>;
    }
    return (
        <div className="text-sm">
            <p>
                {formatarData(turma.dataInicio)} →{" "}
                {turma.dataFim ? formatarData(turma.dataFim) : "sem fim previsto"}
            </p>
            {turma.dataTermino && (
                <p className="text-xs text-muted-foreground">
                    Término em {formatarData(turma.dataTermino)}
                </p>
            )}
        </div>
    );
}

export function TurmaTable({
    turmas,
    onDetalhes,
    onEditar,
    onCancelar,
    onInterromper,
}: TurmaTableProps) {
    return (
        <Table>
            <TableHeader>
                <TableRow>
                    <TableHead>Turma</TableHead>
                    <TableHead>Sala e turno</TableHead>
                    <TableHead>Período</TableHead>
                    <TableHead>Alunos</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead className="w-12">
                        <span className="sr-only">Ações</span>
                    </TableHead>
                </TableRow>
            </TableHeader>
            <TableBody>
                {turmas.map((turma) => {
                    const acoes = acoesPermitidas(turma);
                    return (
                        <TableRow key={turma.id}>
                            <TableCell>
                                <button
                                    type="button"
                                    onClick={() => onDetalhes(turma)}
                                    className="text-left font-semibold hover:underline focus-visible:underline"
                                >
                                    {turma.nome}
                                </button>
                                <p className="text-xs text-muted-foreground">
                                    {turma.instrumentos.length > 0
                                        ? turma.instrumentos.join(", ")
                                        : "Sem instrumento"}
                                </p>
                            </TableCell>
                            <TableCell>
                                <p className="font-medium">Sala {turma.sala.code}</p>
                                <p className="text-xs text-muted-foreground">
                                    {TURNO_LABEL[turma.turno] ?? turma.turno}
                                </p>
                            </TableCell>
                            <TableCell>
                                <Periodo turma={turma} />
                            </TableCell>
                            <TableCell>
                                <TurmaOcupacao
                                    alunos={turma.numeroDeAlunos}
                                    vagas={turma.numeroVagas}
                                />
                            </TableCell>
                            <TableCell>
                                <TurmaStatusBadge status={turma.status} />
                            </TableCell>
                            <TableCell>
                                <DropdownMenu>
                                    <DropdownMenuTrigger asChild>
                                        <Button
                                            variant="ghost"
                                            size="icon"
                                            aria-label={`Ações da turma ${turma.nome}`}
                                        >
                                            <EllipsisVertical />
                                        </Button>
                                    </DropdownMenuTrigger>
                                    <DropdownMenuContent align="end">
                                        <DropdownMenuItem onSelect={() => onDetalhes(turma)}>
                                            <Eye />
                                            Ver detalhes
                                        </DropdownMenuItem>
                                        {acoes.editar && (
                                            <DropdownMenuItem onSelect={() => onEditar(turma)}>
                                                <Pencil />
                                                Editar turma
                                            </DropdownMenuItem>
                                        )}
                                        {(acoes.cancelar || acoes.interromper) && (
                                            <DropdownMenuSeparator />
                                        )}
                                        {acoes.cancelar && (
                                            <DropdownMenuItem
                                                variant="destructive"
                                                onSelect={() => onCancelar(turma)}
                                            >
                                                <Ban />
                                                Cancelar turma
                                            </DropdownMenuItem>
                                        )}
                                        {acoes.interromper && (
                                            <DropdownMenuItem
                                                variant="destructive"
                                                onSelect={() => onInterromper(turma)}
                                            >
                                                <OctagonX />
                                                Interromper turma
                                            </DropdownMenuItem>
                                        )}
                                    </DropdownMenuContent>
                                </DropdownMenu>
                            </TableCell>
                        </TableRow>
                    );
                })}
            </TableBody>
        </Table>
    );
}
