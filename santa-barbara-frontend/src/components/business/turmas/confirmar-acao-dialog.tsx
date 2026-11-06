import type { ReactNode } from "react";
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
} from "#/components/ui/alert-dialog";
import { buttonVariants } from "#/components/ui/button";
import { Spinner } from "#/components/ui/spinner";
import { useAcaoAsync } from "#/hooks/use-acao-async";

interface ConfirmarAcaoDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    titulo: string;
    descricao: ReactNode;
    rotuloConfirmar: string;
    destrutivo?: boolean;
    mensagemErroPadrao?: string;
    onConfirm: () => Promise<void>;
}

// Confirmação para ações que mudam a turma: cancelar, interromper e remover participante.
export function ConfirmarAcaoDialog({
    open,
    onOpenChange,
    titulo,
    descricao,
    rotuloConfirmar,
    destrutivo = false,
    mensagemErroPadrao = "Não foi possível concluir a ação.",
    onConfirm,
}: ConfirmarAcaoDialogProps) {
    const { pendente, erro, executar } = useAcaoAsync();

    return (
        <AlertDialog open={open} onOpenChange={(aberto) => !pendente && onOpenChange(aberto)}>
            <AlertDialogContent>
                <AlertDialogHeader>
                    <AlertDialogTitle>{titulo}</AlertDialogTitle>
                    <AlertDialogDescription asChild>
                        <div className="space-y-2">{descricao}</div>
                    </AlertDialogDescription>
                </AlertDialogHeader>

                {erro && (
                    <p role="alert" className="text-sm text-destructive">
                        {erro}
                    </p>
                )}

                <AlertDialogFooter>
                    <AlertDialogCancel disabled={pendente}>Voltar</AlertDialogCancel>
                    <AlertDialogAction
                        disabled={pendente}
                        className={destrutivo ? buttonVariants({ variant: "destructive" }) : undefined}
                        onClick={async (evento) => {
                            // O AlertDialog fecha sozinho ao clicar: só fecha se der certo.
                            evento.preventDefault();
                            const ok = await executar(onConfirm, mensagemErroPadrao);
                            if (ok) onOpenChange(false);
                        }}
                    >
                        {pendente && <Spinner />}
                        {rotuloConfirmar}
                    </AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    );
}
