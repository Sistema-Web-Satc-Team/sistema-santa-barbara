import type { ReactNode } from "react";
import { Avatar, AvatarFallback } from "#/components/ui/avatar";
import { iniciais } from "#/lib/turmas";

interface TurmaPessoaLinhaProps {
    nome: string;
    email: string | null;
    telefone: string | null;
    selo?: ReactNode;
    acoes?: ReactNode;
}

export function TurmaPessoaLinha({ nome, email, telefone, selo, acoes }: TurmaPessoaLinhaProps) {
    const contato = [email, telefone].filter(Boolean).join(" · ");

    return (
        <li className="flex items-center gap-3 py-2">
            <Avatar className="size-9">
                <AvatarFallback>{iniciais(nome)}</AvatarFallback>
            </Avatar>
            <div className="min-w-0 flex-1">
                <p className="flex flex-wrap items-center gap-2 font-medium">
                    <span className="break-words">{nome}</span>
                    {selo}
                </p>
                {contato && <p className="break-words text-xs text-muted-foreground">{contato}</p>}
            </div>
            {acoes}
        </li>
    );
}
