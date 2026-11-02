import { Check, X } from "lucide-react";
import type { Turma } from "#/api/turmas";
import { requisitosParaIniciar } from "#/lib/turmas";
import { cn } from "#/lib/utils";

// RN-01 e RN-02: a turma só inicia na data de início se tiver tudo isso.
export function TurmaRequisitos({ turma }: { turma: Turma }) {
    const itens = requisitosParaIniciar(turma);
    const faltando = itens.filter((item) => !item.ok).length;

    return (
        <div
            className={cn(
                "rounded-lg border p-3 text-sm",
                faltando > 0 ? "border-warning/40 bg-warning/5" : "border-success/40 bg-success/5",
            )}
        >
            <p className="font-medium">
                {faltando > 0
                    ? `Faltam ${faltando} ${faltando === 1 ? "requisito" : "requisitos"} para a turma iniciar`
                    : "A turma está pronta para iniciar na data de início"}
            </p>
            <ul className="mt-2 space-y-1">
                {itens.map((item) => (
                    <li key={item.chave} className="flex items-start gap-2">
                        {item.ok ? (
                            <Check className="mt-0.5 size-4 shrink-0 text-success" aria-hidden="true" />
                        ) : (
                            <X className="mt-0.5 size-4 shrink-0 text-warning" aria-hidden="true" />
                        )}
                        <span className={item.ok ? undefined : "text-muted-foreground"}>
                            {item.rotulo}
                            <span className="sr-only">{item.ok ? " (ok)" : " (pendente)"}</span>
                        </span>
                    </li>
                ))}
            </ul>
        </div>
    );
}
