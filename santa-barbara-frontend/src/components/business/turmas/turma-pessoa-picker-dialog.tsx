import { useEffect, useState } from "react";
import { Button } from "#/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "#/components/ui/dialog";
import { Field, FieldError, FieldLabel } from "#/components/ui/field";
import { Input } from "#/components/ui/input";
import { Spinner } from "#/components/ui/spinner";
import { Textarea } from "#/components/ui/textarea";
import { useAcaoAsync } from "#/hooks/use-acao-async";
import { useCandidatos } from "#/hooks/use-turma-opcoes";
import { cn } from "#/lib/utils";
import { escolherPessoaSchema } from "#/schemas/form-schema-matricula";

interface TurmaPessoaPickerDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    tipo: "aluno" | "professor";
    titulo: string;
    descricao: string;
    rotuloConfirmar: string;
    // Quem já está na turma (aluno ou professor) não aparece: o professor não pode ser aluno dela.
    excluirIds: string[];
    // Substituição de professor aceita um motivo opcional.
    pedirMotivo?: boolean;
    onConfirmar: (idPessoa: string, motivo: string | undefined) => Promise<void>;
}

// Conflito de horário, turma lotada e matrícula duplicada vêm do backend e aparecem aqui dentro.
export function TurmaPessoaPickerDialog({
    open,
    onOpenChange,
    tipo,
    titulo,
    descricao,
    rotuloConfirmar,
    excluirIds,
    pedirMotivo = false,
    onConfirmar,
}: TurmaPessoaPickerDialogProps) {
    const [termo, setTermo] = useState("");
    const [termoBusca, setTermoBusca] = useState("");
    const [escolhida, setEscolhida] = useState("");
    const [motivo, setMotivo] = useState("");
    const [erroEscolha, setErroEscolha] = useState<string | null>(null);
    const { pendente, erro, executar } = useAcaoAsync();

    useEffect(() => {
        const espera = setTimeout(() => setTermoBusca(termo.trim()), 300);
        return () => clearTimeout(espera);
    }, [termo]);

    const candidatos = useCandidatos(tipo, termoBusca);
    const lista = (candidatos.data ?? []).filter((pessoa) => !excluirIds.includes(pessoa.id));

    async function confirmar() {
        const validacao = escolherPessoaSchema.safeParse({ idPessoa: escolhida });
        if (!validacao.success) {
            setErroEscolha(validacao.error.issues[0]?.message ?? "Escolha uma pessoa na lista.");
            return;
        }
        setErroEscolha(null);
        const ok = await executar(
            () => onConfirmar(validacao.data.idPessoa, motivo.trim() || undefined),
            "Não foi possível concluir a ação.",
        );
        if (ok) onOpenChange(false);
    }

    return (
        <Dialog open={open} onOpenChange={(aberto) => !pendente && onOpenChange(aberto)}>
            <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-lg">
                <DialogHeader>
                    <DialogTitle>{titulo}</DialogTitle>
                    <DialogDescription>{descricao}</DialogDescription>
                </DialogHeader>

                <div className="space-y-4">
                    <Field>
                        <FieldLabel htmlFor="pessoa-busca">Buscar por nome ou e-mail</FieldLabel>
                        <Input
                            id="pessoa-busca"
                            value={termo}
                            onChange={(e) => setTermo(e.target.value)}
                            placeholder="Digite para filtrar..."
                            autoComplete="off"
                        />
                    </Field>

                    <div
                        role="group"
                        aria-label={tipo === "aluno" ? "Alunos encontrados" : "Professores encontrados"}
                        className="max-h-64 space-y-1 overflow-y-auto rounded-lg border p-1"
                    >
                        {candidatos.isLoading ? (
                            <div className="flex justify-center p-4">
                                <Spinner />
                            </div>
                        ) : candidatos.isError ? (
                            <p className="p-3 text-sm text-destructive">Não foi possível buscar.</p>
                        ) : lista.length === 0 ? (
                            <p className="p-3 text-sm text-muted-foreground">Ninguém encontrado.</p>
                        ) : (
                            lista.map((pessoa) => {
                                const marcada = pessoa.id === escolhida;
                                return (
                                    <button
                                        key={pessoa.id}
                                        type="button"
                                        aria-pressed={marcada}
                                        onClick={() => {
                                            setEscolhida(pessoa.id);
                                            setErroEscolha(null);
                                        }}
                                        className={cn(
                                            "flex w-full flex-col rounded-md px-3 py-2 text-left text-sm hover:bg-accent focus-visible:ring-2 focus-visible:ring-ring focus-visible:outline-none",
                                            marcada && "bg-accent ring-1 ring-primary",
                                        )}
                                    >
                                        <span className="font-medium">{pessoa.nome}</span>
                                        <span className="break-words text-xs text-muted-foreground">
                                            {[pessoa.email, pessoa.telefone].filter(Boolean).join(" · ")}
                                        </span>
                                    </button>
                                );
                            })
                        )}
                    </div>
                    {candidatos.data && candidatos.data.length >= 20 && (
                        <p className="text-xs text-muted-foreground">
                            Mostrando os primeiros resultados. Refine a busca para achar outras pessoas.
                        </p>
                    )}
                    {erroEscolha && (
                        <FieldError>{erroEscolha}</FieldError>
                    )}

                    {pedirMotivo && (
                        <Field>
                            <FieldLabel htmlFor="pessoa-motivo">Motivo (opcional)</FieldLabel>
                            <Textarea
                                id="pessoa-motivo"
                                value={motivo}
                                onChange={(e) => setMotivo(e.target.value)}
                                placeholder="Ex.: o professor anterior saiu da banda"
                            />
                        </Field>
                    )}

                    {erro && (
                        <p role="alert" className="text-sm text-destructive">
                            {erro}
                        </p>
                    )}
                </div>

                <DialogFooter>
                    <Button
                        type="button"
                        variant="outline"
                        disabled={pendente}
                        onClick={() => onOpenChange(false)}
                    >
                        Cancelar
                    </Button>
                    <Button type="button" disabled={pendente} onClick={confirmar}>
                        {pendente && <Spinner />}
                        {rotuloConfirmar}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
