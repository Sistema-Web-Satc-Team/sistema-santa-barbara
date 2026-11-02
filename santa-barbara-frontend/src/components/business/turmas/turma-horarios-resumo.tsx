import type { HorariosPorDia, StatusTurma } from "#/api/turmas";
import { formatarHora, horariosOrdenados } from "#/lib/turmas";

interface TurmaHorariosResumoProps {
    horarios: HorariosPorDia | undefined;
    status: StatusTurma;
}

// Só leitura: os horários são definidos na Grade de horários.
export function TurmaHorariosResumo({ horarios, status }: TurmaHorariosResumoProps) {
    const itens = horariosOrdenados(horarios);
    const travado = status !== "PLANEJAMENTO"; // RN-12

    return (
        <div className="space-y-2">
            {itens.length === 0 ? (
                <p className="text-sm text-muted-foreground">
                    Nenhum horário definido. Defina na Grade de horários.
                </p>
            ) : (
                <dl className="space-y-1">
                    {itens.map(({ dia, faixas }) => (
                        <div key={dia.chave} className="flex flex-wrap gap-x-3 text-sm">
                            <dt className="w-28 font-medium">{dia.rotulo}</dt>
                            <dd className="tabular-nums text-muted-foreground">
                                {faixas
                                    .map(
                                        (faixa) =>
                                            `${formatarHora(faixa.horaInicio)}–${formatarHora(faixa.horaFim)}`,
                                    )
                                    .join(", ")}
                            </dd>
                        </div>
                    ))}
                </dl>
            )}
            {travado && (
                <p className="text-xs text-muted-foreground">
                    Os horários só podem ser alterados enquanto a turma está em planejamento.
                </p>
            )}
        </div>
    );
}
