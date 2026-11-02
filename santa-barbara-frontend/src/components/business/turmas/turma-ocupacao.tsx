import { cn } from "#/lib/utils";

export function TurmaOcupacao({ alunos, vagas }: { alunos: number; vagas: number }) {
    const percentual = vagas > 0 ? Math.min(100, Math.round((alunos / vagas) * 100)) : 0;
    const lotada = vagas > 0 && alunos >= vagas;

    return (
        <div className="min-w-24 space-y-1" aria-label={`${alunos} de ${vagas} vagas ocupadas`}>
            <p className="text-sm tabular-nums">
                <span className="font-semibold">{alunos}</span>/{vagas}
                {lotada && <span className="ml-1.5 text-xs font-medium text-warning">lotada</span>}
            </p>
            <div className="h-1.5 overflow-hidden rounded-full bg-muted" aria-hidden="true">
                <div
                    className={cn("h-full rounded-full", lotada ? "bg-warning" : "bg-primary")}
                    style={{ width: `${percentual}%` }}
                />
            </div>
        </div>
    );
}
