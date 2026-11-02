import type { StatusTurma } from "#/api/turmas";
import { Badge } from "#/components/ui/badge";
import { STATUS_LABEL } from "#/lib/turmas";
import { cn } from "#/lib/utils";

// Cores do tema (success, warning, info) definidas em styles.css.
const ESTILOS: Record<StatusTurma, string> = {
    PLANEJAMENTO: "border-info/30 bg-info/10 text-info",
    EM_ANDAMENTO: "border-success/30 bg-success/10 text-success",
    ENCERRADO: "border-border bg-muted text-muted-foreground",
    CANCELADO: "border-destructive/30 bg-destructive/10 text-destructive",
    INTERROMPIDA: "border-warning/30 bg-warning/10 text-warning",
};

export function TurmaStatusBadge({
    status,
    className,
}: {
    status: StatusTurma;
    className?: string;
}) {
    return (
        <Badge variant="outline" className={cn("font-semibold", ESTILOS[status], className)}>
            {STATUS_LABEL[status]}
        </Badge>
    );
}
