import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { z } from "zod";
import { TurmasPage } from "#/components/pages/TurmasPage";

const searchSchema = z.object({
    turma: z.coerce.number().int().positive().optional().catch(undefined),
});

export const Route = createFileRoute("/_auth/secretario/dashboard/_layout/turmas/")({
    validateSearch: searchSchema,
    component: TurmasRoute,
});

function TurmasRoute() {
    const { turma } = Route.useSearch();
    const navigate = useNavigate({ from: Route.fullPath });
    return (
        <TurmasPage
            idDetalhe={turma}
            onDetalheChange={(id) => navigate({ search: { turma: id }, replace: true })}
        />
    );
}
