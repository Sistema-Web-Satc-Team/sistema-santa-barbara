import { useMemo, useState, type FormEvent } from "react";
import { toast } from "sonner";
import { TURNOS, type TurmaResumo } from "#/api/turmas";
import { Button } from "#/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "#/components/ui/dialog";
import { Field, FieldDescription, FieldError, FieldGroup, FieldLabel } from "#/components/ui/field";
import { Input } from "#/components/ui/input";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "#/components/ui/select";
import { Spinner } from "#/components/ui/spinner";
import { useAcaoAsync } from "#/hooks/use-acao-async";
import { useTurmaMutations } from "#/hooks/use-turma-mutations";
import { useInstrumentos, useSalas } from "#/hooks/use-turma-opcoes";
import { errosPorCampo, type ErrosPorCampo } from "#/lib/erros-zod";
import { idsDosInstrumentos, montarAtualizacao, montarCriacao, TURNO_LABEL } from "#/lib/turmas";
import { criarTurmaFormSchema, type TurmaFormState } from "#/schemas/form-schema-turma";

interface TurmaFormDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    // Sem turma = criar. Com turma = editar.
    turma?: TurmaResumo;
    onSaved?: (idTurma: number) => void;
}

type CamposTexto = Omit<TurmaFormState, "idsInstrumentos">;

export function TurmaFormDialog({ open, onOpenChange, turma, onSaved }: TurmaFormDialogProps) {
    const editando = turma !== undefined;
    // Em andamento só nome, vagas e fim estimado mudam; o resto é da fase de planejamento.
    const estrutural = !turma || turma.status === "PLANEJAMENTO";

    const salas = useSalas();
    const instrumentos = useInstrumentos();
    const { criar, atualizar } = useTurmaMutations();
    const { pendente, erro, executar } = useAcaoAsync();

    const [valores, setValores] = useState<CamposTexto>({
        nome: turma?.nome ?? "",
        codigoSala: turma?.sala.code ?? "",
        numeroVagas: turma ? String(turma.numeroVagas) : "",
        turno: turma?.turno ?? "",
        dataInicio: turma?.dataInicio ?? "",
        dataFim: turma?.dataFim ?? "",
    });
    // null = a pessoa ainda não mexeu: vale o que a turma já tem (a API devolve só os nomes).
    const [idsEditados, setIdsEditados] = useState<number[] | null>(null);
    const [erros, setErros] = useState<ErrosPorCampo>({});

    const idsIniciais = useMemo(
        () => (turma && instrumentos.data ? idsDosInstrumentos(turma.instrumentos, instrumentos.data) : []),
        [turma, instrumentos.data],
    );
    const idsSelecionados = idsEditados ?? idsIniciais;

    const salaEscolhida = salas.data?.find((item) => item.code === valores.codigoSala);
    const carregando = salas.isLoading || instrumentos.isLoading;
    const falhaOpcoes = salas.isError || instrumentos.isError;

    function alterar<K extends keyof CamposTexto>(campo: K, valor: CamposTexto[K]) {
        setValores((atual) => ({ ...atual, [campo]: valor }));
    }

    function alternarInstrumento(id: number) {
        setIdsEditados(
            idsSelecionados.includes(id)
                ? idsSelecionados.filter((item) => item !== id)
                : [...idsSelecionados, id],
        );
    }

    async function enviar(evento: FormEvent<HTMLFormElement>) {
        evento.preventDefault();

        const schema = criarTurmaFormSchema({
            salas: salas.data ?? [],
            minVagas: turma?.numeroDeAlunos, // RN-08
        });
        const resultado = schema.safeParse({ ...valores, idsInstrumentos: idsSelecionados });
        if (!resultado.success) {
            setErros(errosPorCampo(resultado.error));
            return;
        }
        setErros({});

        await executar(async () => {
            if (turma) {
                const body = montarAtualizacao(turma, resultado.data, instrumentos.data ?? []);
                if (Object.keys(body).length > 0) {
                    await atualizar({ idTurma: turma.id, body });
                    toast.success("Turma atualizada.");
                }
                onOpenChange(false);
                return;
            }
            const criada = await criar(montarCriacao(resultado.data));
            toast.success("Turma criada. Defina os horários na Grade e adicione professor e alunos.");
            onOpenChange(false);
            onSaved?.(criada.id);
        }, "Não foi possível salvar a turma.");
    }

    return (
        <Dialog open={open} onOpenChange={(aberto) => !pendente && onOpenChange(aberto)}>
            <DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-xl">
                <DialogHeader>
                    <DialogTitle>{editando ? "Editar turma" : "Nova turma"}</DialogTitle>
                    <DialogDescription>
                        {editando
                            ? "Só o que você alterar é enviado."
                            : "Os horários são definidos depois, na Grade de horários."}
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={enviar} noValidate className="space-y-4">
                    <FieldGroup>
                        <Field data-invalid={Boolean(erros.nome)}>
                            <FieldLabel htmlFor="turma-nome">Nome da turma</FieldLabel>
                            <Input
                                id="turma-nome"
                                value={valores.nome}
                                onChange={(e) => alterar("nome", e.target.value)}
                                placeholder="Ex.: Clarinete Iniciante"
                                aria-invalid={Boolean(erros.nome)}
                            />
                            {erros.nome && <FieldError>{erros.nome}</FieldError>}
                        </Field>

                        <Field data-invalid={Boolean(erros.idsInstrumentos)}>
                            <FieldLabel id="turma-instrumentos-rotulo">Instrumentos</FieldLabel>
                            {instrumentos.isLoading ? (
                                <Spinner />
                            ) : (
                                <div
                                    role="group"
                                    aria-labelledby="turma-instrumentos-rotulo"
                                    className="flex flex-wrap gap-2"
                                >
                                    {(instrumentos.data ?? []).map((item) => {
                                        const marcado = idsSelecionados.includes(item.id);
                                        return (
                                            <Button
                                                key={item.id}
                                                type="button"
                                                size="sm"
                                                variant={marcado ? "default" : "outline"}
                                                aria-pressed={marcado}
                                                disabled={!estrutural}
                                                onClick={() => alternarInstrumento(item.id)}
                                            >
                                                {item.nome}
                                            </Button>
                                        );
                                    })}
                                </div>
                            )}
                            {erros.idsInstrumentos && <FieldError>{erros.idsInstrumentos}</FieldError>}
                        </Field>

                        <div className="grid gap-4 sm:grid-cols-2">
                            <Field data-invalid={Boolean(erros.codigoSala)}>
                                <FieldLabel htmlFor="turma-sala">Sala</FieldLabel>
                                <Select
                                    value={valores.codigoSala}
                                    onValueChange={(v) => alterar("codigoSala", v)}
                                    disabled={!estrutural}
                                >
                                    <SelectTrigger id="turma-sala" aria-invalid={Boolean(erros.codigoSala)}>
                                        <SelectValue placeholder="Escolha a sala" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {(salas.data ?? []).map((sala) => (
                                            <SelectItem key={sala.id} value={sala.code}>
                                                {sala.code} · {sala.capacidade} lugares
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                                {erros.codigoSala && <FieldError>{erros.codigoSala}</FieldError>}
                            </Field>

                            <Field data-invalid={Boolean(erros.turno)}>
                                <FieldLabel htmlFor="turma-turno">Turno</FieldLabel>
                                <Select
                                    value={valores.turno}
                                    onValueChange={(v) => alterar("turno", v)}
                                    disabled={!estrutural}
                                >
                                    <SelectTrigger id="turma-turno" aria-invalid={Boolean(erros.turno)}>
                                        <SelectValue placeholder="Escolha o turno" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {TURNOS.map((turno) => (
                                            <SelectItem key={turno} value={turno}>
                                                {TURNO_LABEL[turno]}
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                                {erros.turno && <FieldError>{erros.turno}</FieldError>}
                            </Field>
                        </div>

                        <Field data-invalid={Boolean(erros.numeroVagas)}>
                            <FieldLabel htmlFor="turma-vagas">Vagas de aluno</FieldLabel>
                            <Input
                                id="turma-vagas"
                                inputMode="numeric"
                                value={valores.numeroVagas}
                                onChange={(e) => alterar("numeroVagas", e.target.value)}
                                aria-invalid={Boolean(erros.numeroVagas)}
                                className="sm:max-w-40"
                            />
                            <FieldDescription>
                                {salaEscolhida
                                    ? `Máximo de ${salaEscolhida.capacidade}, a capacidade da sala ${salaEscolhida.code}.`
                                    : "O máximo é a capacidade da sala escolhida."}
                                {turma && turma.numeroDeAlunos > 0
                                    ? ` Mínimo de ${turma.numeroDeAlunos}, os alunos já matriculados.`
                                    : ""}
                            </FieldDescription>
                            {erros.numeroVagas && <FieldError>{erros.numeroVagas}</FieldError>}
                        </Field>

                        <div className="grid gap-4 sm:grid-cols-2">
                            <Field data-invalid={Boolean(erros.dataInicio)}>
                                <FieldLabel htmlFor="turma-inicio">Data de início</FieldLabel>
                                <Input
                                    id="turma-inicio"
                                    type="date"
                                    value={valores.dataInicio}
                                    onChange={(e) => alterar("dataInicio", e.target.value)}
                                    disabled={!estrutural}
                                    aria-invalid={Boolean(erros.dataInicio)}
                                />
                                {erros.dataInicio && <FieldError>{erros.dataInicio}</FieldError>}
                            </Field>
                            <Field data-invalid={Boolean(erros.dataFim)}>
                                <FieldLabel htmlFor="turma-fim">Fim estimado</FieldLabel>
                                <Input
                                    id="turma-fim"
                                    type="date"
                                    value={valores.dataFim}
                                    onChange={(e) => alterar("dataFim", e.target.value)}
                                    aria-invalid={Boolean(erros.dataFim)}
                                />
                                {erros.dataFim && <FieldError>{erros.dataFim}</FieldError>}
                            </Field>
                        </div>
                        <FieldDescription>
                            O período só pode ser definido com pelo menos um professor, um horário e um
                            aluno na turma. Na data de início ela passa a Em andamento sozinha e, no fim
                            estimado, encerra sozinha.
                        </FieldDescription>
                    </FieldGroup>

                    {falhaOpcoes && (
                        <p role="alert" className="text-sm text-destructive">
                            Não foi possível carregar salas ou instrumentos. Feche e tente de novo.
                        </p>
                    )}
                    {erro && (
                        <p role="alert" className="text-sm text-destructive">
                            {erro}
                        </p>
                    )}

                    <DialogFooter>
                        <Button
                            type="button"
                            variant="outline"
                            disabled={pendente}
                            onClick={() => onOpenChange(false)}
                        >
                            Cancelar
                        </Button>
                        <Button type="submit" disabled={pendente || carregando || falhaOpcoes}>
                            {pendente && <Spinner />}
                            {editando ? "Salvar alterações" : "Criar turma"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}
