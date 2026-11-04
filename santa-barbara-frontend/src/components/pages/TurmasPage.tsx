import { Plus } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import type { TurmaResumo } from "#/api/turmas";
import { ConfirmarAcaoDialog } from "#/components/business/turmas/confirmar-acao-dialog";
import { TurmaDetalhesSheet } from "#/components/business/turmas/turma-detalhes-sheet";
import { TurmaFiltros } from "#/components/business/turmas/turma-filtros";
import { TurmaFormDialog } from "#/components/business/turmas/turma-form-dialog";
import { TurmaTable } from "#/components/business/turmas/turma-table";

import { Button } from "#/components/ui/button";
import { Spinner } from "#/components/ui/spinner";
import { useTurmaMutations } from "#/hooks/use-turma-mutations";
import { useTurmas } from "#/hooks/use-turmas";
import { mensagemDeErro } from "#/lib/mensagem-erro";
import { DataPagination } from "../business/data-pagination";

const TAMANHOS = [10, 20, 50];

export function TurmasPage({
    idDetalhe,
    onDetalheChange,
}: {
    idDetalhe?: number;
    onDetalheChange: (id: number | undefined) => void;
}) {
    const { state, actions } = useTurmas();
    const { cancelar, interromper } = useTurmaMutations();
    const [criando, setCriando] = useState(false);
    const [editando, setEditando] = useState<TurmaResumo | null>(null);
    const [cancelando, setCancelando] = useState<TurmaResumo | null>(null);
    const [interrompendo, setInterrompendo] = useState<TurmaResumo | null>(null);

    const pagina = state.turmas;

    return (
        <div className="space-y-4">
            <div className="flex flex-wrap items-center gap-3">
                <h2 className="mr-auto text-3xl font-bold">Turmas</h2>
                <Button onClick={() => setCriando(true)}>
                    <Plus /> Nova turma
                </Button>
            </div>

            <TurmaFiltros status={state.filters.status} onStatusChange={(s) => actions.setFilter("status", s)} />

            {state.isLoading ? (
                <div className="flex justify-center p-10">
                    <Spinner />
                </div>
            ) : state.error ? (
                <p role="alert" className="text-destructive">
                    {mensagemDeErro(state.error, "Não foi possível carregar as turmas.")}
                </p>
            ) : state.isEmpty ? (
                <p className="py-10 text-center text-muted-foreground">Nenhuma turma encontrada.</p>
            ) : (
                pagina && (
                    <>
                        <TurmaTable
                            turmas={pagina.content}
                            onDetalhes={(t) => onDetalheChange(t.id)}
                            onEditar={setEditando}
                            onCancelar={setCancelando}
                            onInterromper={setInterrompendo}
                        />
                        <DataPagination
                            pagination={{
                                page: pagina.page,
                                size: pagina.size,
                                totalElements: pagina.totalElements,
                                totalPages: pagina.totalPages,
                            }}
                            pagesSizes={TAMANHOS}
                            onPageChange={(p: number) => actions.setFilter("page", p)}
                            onSizeChange={(s: number) => actions.setFilter("size", s)}
                        />
                    </>
                )
            )}

            {criando && (
                <TurmaFormDialog
                    open
                    onOpenChange={(o) => !o && setCriando(false)}
                    onSaved={(id) => onDetalheChange(id)}
                />
            )}
            {editando && (
                <TurmaFormDialog
                    key={editando.id}
                    open
                    turma={editando}
                    onOpenChange={(o) => !o && setEditando(null)}
                />
            )}
            {cancelando && (
                <ConfirmarAcaoDialog
                    open
                    onOpenChange={(o) => !o && setCancelando(null)}
                    titulo="Cancelar turma"
                    descricao={`${cancelando.nome} será cancelada. Só é possível em planejamento.`}
                    rotuloConfirmar="Cancelar turma"
                    destrutivo
                    onConfirm={async () => {
                        await cancelar(cancelando.id);
                        toast.success("Turma cancelada.");
                    }}
                />
            )}
            {interrompendo && (
                <ConfirmarAcaoDialog
                    open
                    onOpenChange={(o) => !o && setInterrompendo(null)}
                    titulo="Interromper turma"
                    descricao={`${interrompendo.nome} será interrompida. Isso não pode ser desfeito.`}
                    rotuloConfirmar="Interromper"
                    destrutivo
                    onConfirm={async () => {
                        await interromper(interrompendo.id);
                        toast.success("Turma interrompida.");
                    }}
                />
            )}
            {idDetalhe !== undefined && (
                <TurmaDetalhesSheet idTurma={idDetalhe} onOpenChange={(o) => !o && onDetalheChange(undefined)} />
            )}
        </div>
    );
}
