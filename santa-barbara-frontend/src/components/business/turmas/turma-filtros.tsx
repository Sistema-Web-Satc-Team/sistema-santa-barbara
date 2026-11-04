import { STATUS_TURMA, type StatusTurma } from "#/api/turmas";
import { Field, FieldLabel } from "#/components/ui/field";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "#/components/ui/select";
import { STATUS_LABEL } from "#/lib/turmas";

const TODOS = "TODOS";

interface TurmaFiltrosProps {
    status?: StatusTurma;
    onStatusChange: (status: StatusTurma | undefined) => void;
}

export function TurmaFiltros({ status, onStatusChange }: TurmaFiltrosProps) {
    return (
        <Field className="mb-4 w-full sm:w-64">
            <FieldLabel htmlFor="filtro-status-turma">Status</FieldLabel>
            <Select
                value={status ?? TODOS}
                onValueChange={(valor) =>
                    onStatusChange(valor === TODOS ? undefined : (valor as StatusTurma))
                }
            >
                <SelectTrigger id="filtro-status-turma">
                    <SelectValue placeholder="Todos os status" />
                </SelectTrigger>
                <SelectContent>
                    <SelectItem value={TODOS}>Todos os status</SelectItem>
                    {STATUS_TURMA.map((item) => (
                        <SelectItem key={item} value={item}>
                            {STATUS_LABEL[item]}
                        </SelectItem>
                    ))}
                </SelectContent>
            </Select>
        </Field>
    );
}
