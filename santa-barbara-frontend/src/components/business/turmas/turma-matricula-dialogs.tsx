import { useState, type FormEvent } from "react";
import { toast } from "sonner";
import type { AlunoDaTurma, Turma } from "#/api/turmas";
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
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "#/components/ui/select";
import { Spinner } from "#/components/ui/spinner";
import { Textarea } from "#/components/ui/textarea";
import { useAcaoAsync } from "#/hooks/use-acao-async";
import { useTurmaMutations } from "#/hooks/use-turma-mutations";
import { useTurmasEmAndamento } from "#/hooks/use-turma-opcoes";
import { errosPorCampo, type ErrosPorCampo } from "#/lib/erros-zod";
import { justificativaSchema, transferenciaSchema } from "#/schemas/form-schema-matricula";

interface DialogoMatriculaProps {
    turma: Turma;
    aluno: AlunoDaTurma;
    onOpenChange: (open: boolean) => void;
}

// Só EM_ANDAMENTO: a justificativa fica no histórico da matrícula.
export function DesistenciaDialog({ turma, aluno, onOpenChange }: DialogoMatriculaProps) {
    const { desistirDaMatricula } = useTurmaMutations();
    const { pendente, erro, executar } = useAcaoAsync();
    const [justificativa, setJustificativa] = useState("");
    const [erros, setErros] = useState<ErrosPorCampo>({});

    async function enviar(evento: FormEvent<HTMLFormElement>) {
        evento.preventDefault();
        const resultado = justificativaSchema.safeParse({ justificativa });
        if (!resultado.success) {
            setErros(errosPorCampo(resultado.error));
            return;
        }
        setErros({});
        const ok = await executar(async () => {
            await desistirDaMatricula({
                idMatricula: aluno.idMatricula,
                justificativa: resultado.data.justificativa,
            });
            toast.success(`${aluno.nome} saiu da turma ${turma.nome}.`);
        }, "Não foi possível registrar a desistência.");
        if (ok) onOpenChange(false);
    }

    return (
        <Dialog open onOpenChange={(aberto) => !pendente && onOpenChange(aberto)}>
            <DialogContent className="sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Registrar desistência</DialogTitle>
                    <DialogDescription>
                        {aluno.nome} deixa de ocupar vaga em {turma.nome}. O histórico da matrícula é
                        preservado.
                    </DialogDescription>
                </DialogHeader>
                <form onSubmit={enviar} noValidate className="space-y-4">
                    <Field data-invalid={Boolean(erros.justificativa)}>
                        <FieldLabel htmlFor="desistencia-motivo">Motivo</FieldLabel>
                        <Textarea
                            id="desistencia-motivo"
                            value={justificativa}
                            onChange={(e) => setJustificativa(e.target.value)}
                            aria-invalid={Boolean(erros.justificativa)}
                        />
                        {erros.justificativa && <FieldError>{erros.justificativa}</FieldError>}
                    </Field>
                    {erro && (
                        <p role="alert" className="text-sm text-destructive">
                            {erro}
                        </p>
                    )}
                    <DialogFooter>
                        <Button type="button" variant="outline" disabled={pendente} onClick={() => onOpenChange(false)}>
                            Voltar
                        </Button>
                        <Button type="submit" variant="destructive" disabled={pendente}>
                            {pendente && <Spinner />}
                            Registrar desistência
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}

// Só EM_ANDAMENTO, para outra turma também EM_ANDAMENTO. O backend valida conflito de horário (RN-05).
export function TransferenciaDialog({ turma, aluno, onOpenChange }: DialogoMatriculaProps) {
    const { transferirMatricula } = useTurmaMutations();
    const destinos = useTurmasEmAndamento();
    const { pendente, erro, executar } = useAcaoAsync();
    const [idNovaTurma, setIdNovaTurma] = useState("");
    const [justificativa, setJustificativa] = useState("");
    const [erros, setErros] = useState<ErrosPorCampo>({});

    const opcoes = (destinos.data?.content ?? []).filter((item) => item.id !== turma.id);

    async function enviar(evento: FormEvent<HTMLFormElement>) {
        evento.preventDefault();
        const resultado = transferenciaSchema.safeParse({ idNovaTurma, justificativa });
        if (!resultado.success) {
            setErros(errosPorCampo(resultado.error));
            return;
        }
        setErros({});
        const ok = await executar(async () => {
            await transferirMatricula({
                idMatricula: aluno.idMatricula,
                idNovaTurma: Number(resultado.data.idNovaTurma),
                justificativa: resultado.data.justificativa,
            });
            toast.success(`${aluno.nome} foi transferido(a).`);
        }, "Não foi possível transferir o aluno.");
        if (ok) onOpenChange(false);
    }

    return (
        <Dialog open onOpenChange={(aberto) => !pendente && onOpenChange(aberto)}>
            <DialogContent className="sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Transferir aluno</DialogTitle>
                    <DialogDescription>
                        {aluno.nome} sai de {turma.nome} e entra na turma escolhida, que também precisa
                        estar em andamento.
                    </DialogDescription>
                </DialogHeader>
                <form onSubmit={enviar} noValidate className="space-y-4">
                    <Field data-invalid={Boolean(erros.idNovaTurma)}>
                        <FieldLabel htmlFor="transferencia-turma">Turma de destino</FieldLabel>
                        <Select value={idNovaTurma} onValueChange={setIdNovaTurma}>
                            <SelectTrigger id="transferencia-turma" aria-invalid={Boolean(erros.idNovaTurma)}>
                                <SelectValue
                                    placeholder={destinos.isLoading ? "Carregando..." : "Escolha a turma"}
                                />
                            </SelectTrigger>
                            <SelectContent>
                                {opcoes.map((item) => (
                                    <SelectItem key={item.id} value={String(item.id)}>
                                        {item.nome} · {item.numeroDeAlunos}/{item.numeroVagas} vagas
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                        {!destinos.isLoading && opcoes.length === 0 && (
                            <p className="text-sm text-muted-foreground">
                                Não há outra turma em andamento para receber o aluno.
                            </p>
                        )}
                        {erros.idNovaTurma && <FieldError>{erros.idNovaTurma}</FieldError>}
                    </Field>
                    <Field data-invalid={Boolean(erros.justificativa)}>
                        <FieldLabel htmlFor="transferencia-motivo">Motivo</FieldLabel>
                        <Textarea
                            id="transferencia-motivo"
                            value={justificativa}
                            onChange={(e) => setJustificativa(e.target.value)}
                            aria-invalid={Boolean(erros.justificativa)}
                        />
                        {erros.justificativa && <FieldError>{erros.justificativa}</FieldError>}
                    </Field>
                    {erro && (
                        <p role="alert" className="text-sm text-destructive">
                            {erro}
                        </p>
                    )}
                    <DialogFooter>
                        <Button type="button" variant="outline" disabled={pendente} onClick={() => onOpenChange(false)}>
                            Voltar
                        </Button>
                        <Button type="submit" disabled={pendente || opcoes.length === 0}>
                            {pendente && <Spinner />}
                            Transferir
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}
