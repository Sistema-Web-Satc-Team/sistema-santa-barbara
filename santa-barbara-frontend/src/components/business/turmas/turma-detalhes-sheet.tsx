import { UserMinus, UserPlus, ArrowRightLeft, LogOut, Replace } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import type { AlunoDaTurma, ProfessorDaTurma, Turma } from "#/api/turmas";
import { Button } from "#/components/ui/button";
import {
    Sheet,
    SheetContent,
    SheetDescription,
    SheetHeader,
    SheetTitle,
} from "#/components/ui/sheet";
import { Spinner } from "#/components/ui/spinner";
import { useTurma } from "#/hooks/use-turma";
import { useTurmaMutations } from "#/hooks/use-turma-mutations";
import { acoesPermitidas, formatarData, TURNO_LABEL } from "#/lib/turmas";
import { ConfirmarAcaoDialog } from "./confirmar-acao-dialog";
import { DesistenciaDialog, TransferenciaDialog } from "./turma-matricula-dialogs";
import { TurmaHorariosResumo } from "./turma-horarios-resumo";
import { TurmaOcupacao } from "./turma-ocupacao";
import { TurmaPessoaLinha } from "./turma-pessoa-linha";
import { TurmaPessoaPickerDialog } from "./turma-pessoa-picker-dialog";
import { TurmaRequisitos } from "./turma-requisitos";
import { TurmaStatusBadge } from "./turma-status-badge";

interface TurmaDetalhesSheetProps {
    idTurma: number;
    onOpenChange: (open: boolean) => void;
}

type Dialogo =
    | { tipo: "matricular" }
    | { tipo: "vincular" }
    | { tipo: "substituir" }
    | { tipo: "remover-aluno"; aluno: AlunoDaTurma }
    | { tipo: "remover-professor"; professor: ProfessorDaTurma }
    | { tipo: "transferir"; aluno: AlunoDaTurma }
    | { tipo: "desistir"; aluno: AlunoDaTurma };

export function TurmaDetalhesSheet({ idTurma, onOpenChange }: TurmaDetalhesSheetProps) {
    const { data: turma, isLoading, isError } = useTurma(idTurma);
    const m = useTurmaMutations();
    const [dialogo, setDialogo] = useState<Dialogo | null>(null);
    const fechar = (aberto: boolean) => !aberto && setDialogo(null);

    return (
        <Sheet open onOpenChange={onOpenChange}>
            <SheetContent className="w-full overflow-y-auto sm:max-w-xl">
                {isLoading || !turma ? (
                    <div className="flex flex-1 items-center justify-center p-8">
                        {isError ? (
                            <p className="text-sm text-destructive">Não foi possível carregar a turma.</p>
                        ) : (
                            <Spinner />
                        )}
                    </div>
                ) : (
                    <Conteudo turma={turma} dialogo={dialogo} setDialogo={setDialogo} fechar={fechar} m={m} />
                )}
            </SheetContent>
        </Sheet>
    );
}

function Conteudo({
    turma,
    dialogo,
    setDialogo,
    fechar,
    m,
}: {
    turma: Turma;
    dialogo: Dialogo | null;
    setDialogo: (d: Dialogo | null) => void;
    fechar: (aberto: boolean) => void;
    m: ReturnType<typeof useTurmaMutations>;
}) {
    const acoes = acoesPermitidas(turma);
    const professorIds = turma.professores.map((p) => p.idProfessor);
    // O professor da turma não pode ser aluno dela; quem já é aluno também não entra de novo.
    const idsDeAlunos = turma.alunos.map((a) => a.idAluno);

    return (
        <>
            <SheetHeader>
                <SheetTitle className="flex flex-wrap items-center gap-2">
                    {turma.nome} <TurmaStatusBadge status={turma.status} />
                </SheetTitle>
                <SheetDescription>
                    Sala {turma.sala.code} · {TURNO_LABEL[turma.turno]} ·{" "}
                    {turma.instrumentos.join(", ") || "sem instrumentos"}
                </SheetDescription>
            </SheetHeader>

            <div className="space-y-6 px-4 pb-6">
                <dl className="grid grid-cols-2 gap-3 text-sm">
                    <div>
                        <dt className="text-muted-foreground">Início</dt>
                        <dd>{formatarData(turma.dataInicio)}</dd>
                    </div>
                    <div>
                        <dt className="text-muted-foreground">Fim estimado</dt>
                        <dd>{formatarData(turma.dataFim)}</dd>
                    </div>
                    {turma.dataTermino && (
                        <div>
                            <dt className="text-muted-foreground">Término</dt>
                            <dd>{formatarData(turma.dataTermino)}</dd>
                        </div>
                    )}
                    <div>
                        <dt className="text-muted-foreground">Ocupação</dt>
                        <dd>
                            <TurmaOcupacao alunos={turma.numeroDeAlunos} vagas={turma.numeroVagas} />
                        </dd>
                    </div>
                </dl>

                {turma.status === "PLANEJAMENTO" && <TurmaRequisitos turma={turma} />}
                <TurmaHorariosResumo horarios={turma.horarios} status={turma.status} />

                <section aria-labelledby="sec-prof">
                    <div className="mb-1 flex items-center justify-between gap-2">
                        <h3 id="sec-prof" className="font-semibold">
                            Professor
                        </h3>
                        {acoes.vincularProfessor && (
                            <Button size="sm" variant="outline" onClick={() => setDialogo({ tipo: "vincular" })}>
                                <UserPlus /> Adicionar
                            </Button>
                        )}
                        {acoes.substituirProfessor && (
                            <Button size="sm" variant="outline" onClick={() => setDialogo({ tipo: "substituir" })}>
                                <Replace /> Substituir
                            </Button>
                        )}
                    </div>
                    {turma.professores.length === 0 ? (
                        <p className="text-sm text-muted-foreground">Nenhum professor na turma.</p>
                    ) : (
                        <ul className="divide-y">
                            {turma.professores.map((p) => (
                                <TurmaPessoaLinha
                                    key={p.idProfessorTurma}
                                    nome={p.nome}
                                    email={p.email}
                                    telefone={p.telefone}
                                    acoes={
                                        acoes.removerProfessor && (
                                            <Button
                                                size="icon"
                                                variant="ghost"
                                                aria-label={`Remover ${p.nome}`}
                                                onClick={() => setDialogo({ tipo: "remover-professor", professor: p })}
                                            >
                                                <UserMinus />
                                            </Button>
                                        )
                                    }
                                />
                            ))}
                        </ul>
                    )}
                </section>

                <section aria-labelledby="sec-alunos">
                    <div className="mb-1 flex items-center justify-between gap-2">
                        <h3 id="sec-alunos" className="font-semibold">
                            Alunos ({turma.numeroDeAlunos}/{turma.numeroVagas})
                        </h3>
                        {acoes.matricular && (
                            <Button size="sm" variant="outline" onClick={() => setDialogo({ tipo: "matricular" })}>
                                <UserPlus /> Adicionar
                            </Button>
                        )}
                    </div>
                    {turma.alunos.length === 0 ? (
                        <p className="text-sm text-muted-foreground">Nenhum aluno matriculado.</p>
                    ) : (
                        <ul className="divide-y">
                            {turma.alunos.map((a) => (
                                <TurmaPessoaLinha
                                    key={a.idMatricula}
                                    nome={a.nome}
                                    email={a.email}
                                    telefone={a.telefone}
                                    selo={a.ehMenor ? <span className="text-xs text-info">Menor</span> : undefined}
                                    acoes={
                                        <>
                                            {acoes.removerAluno && (
                                                <Button
                                                    size="icon"
                                                    variant="ghost"
                                                    aria-label={`Remover ${a.nome}`}
                                                    onClick={() => setDialogo({ tipo: "remover-aluno", aluno: a })}
                                                >
                                                    <UserMinus />
                                                </Button>
                                            )}
                                            {acoes.transferirAluno && (
                                                <Button
                                                    size="icon"
                                                    variant="ghost"
                                                    aria-label={`Transferir ${a.nome}`}
                                                    onClick={() => setDialogo({ tipo: "transferir", aluno: a })}
                                                >
                                                    <ArrowRightLeft />
                                                </Button>
                                            )}
                                            {acoes.desistirAluno && (
                                                <Button
                                                    size="icon"
                                                    variant="ghost"
                                                    aria-label={`Desistência de ${a.nome}`}
                                                    onClick={() => setDialogo({ tipo: "desistir", aluno: a })}
                                                >
                                                    <LogOut />
                                                </Button>
                                            )}
                                        </>
                                    }
                                />
                            ))}
                        </ul>
                    )}
                </section>
            </div>

            {dialogo?.tipo === "matricular" && (
                <TurmaPessoaPickerDialog
                    open
                    onOpenChange={fechar}
                    tipo="aluno"
                    titulo="Adicionar aluno"
                    descricao={`Matricular um aluno em ${turma.nome}. Conflito de horário e lotação são validados ao confirmar.`}
                    rotuloConfirmar="Matricular"
                    excluirIds={[...idsDeAlunos, ...professorIds]}
                    onConfirmar={async (idAluno) => {
                        await m.matricular({ idAluno, idTurma: turma.id });
                        toast.success("Aluno matriculado.");
                    }}
                />
            )}
            {dialogo?.tipo === "vincular" && (
                <TurmaPessoaPickerDialog
                    open
                    onOpenChange={fechar}
                    tipo="professor"
                    titulo="Adicionar professor"
                    descricao="Cada turma tem no máximo um professor."
                    rotuloConfirmar="Adicionar"
                    excluirIds={[...professorIds, ...idsDeAlunos]}
                    onConfirmar={async (idProfessor) => {
                        await m.vincularProfessor({ idTurma: turma.id, idProfessor });
                        toast.success("Professor adicionado.");
                    }}
                />
            )}
            {dialogo?.tipo === "substituir" && (
                <TurmaPessoaPickerDialog
                    open
                    onOpenChange={fechar}
                    tipo="professor"
                    titulo="Substituir professor"
                    descricao="O novo professor assume a turma em andamento."
                    rotuloConfirmar="Substituir"
                    pedirMotivo
                    excluirIds={[...professorIds, ...idsDeAlunos]}
                    onConfirmar={async (idNovoProfessor, justificativa) => {
                        await m.substituirProfessor({ idTurma: turma.id, idNovoProfessor, justificativa });
                        toast.success("Professor substituído.");
                    }}
                />
            )}
            {dialogo?.tipo === "remover-aluno" && (
                <ConfirmarAcaoDialog
                    open
                    onOpenChange={fechar}
                    titulo="Remover aluno"
                    descricao={`${dialogo.aluno.nome} sai de ${turma.nome}.`}
                    rotuloConfirmar="Remover"
                    destrutivo
                    onConfirm={async () => {
                        await m.removerMatricula(dialogo.aluno.idMatricula);
                        toast.success("Aluno removido.");
                    }}
                />
            )}
            {dialogo?.tipo === "remover-professor" && (
                <ConfirmarAcaoDialog
                    open
                    onOpenChange={fechar}
                    titulo="Remover professor"
                    descricao={`${dialogo.professor.nome} deixa ${turma.nome}.`}
                    rotuloConfirmar="Remover"
                    destrutivo
                    onConfirm={async () => {
                        await m.removerProfessor({ idTurma: turma.id, idProfessor: dialogo.professor.idProfessor });
                        toast.success("Professor removido.");
                    }}
                />
            )}
            {dialogo?.tipo === "transferir" && (
                <TransferenciaDialog turma={turma} aluno={dialogo.aluno} onOpenChange={fechar} />
            )}
            {dialogo?.tipo === "desistir" && (
                <DesistenciaDialog turma={turma} aluno={dialogo.aluno} onOpenChange={fechar} />
            )}
        </>
    );
}
