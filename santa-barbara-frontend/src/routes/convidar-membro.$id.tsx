import { validarConvite } from '#/api/convites';
import { AceitarConviteForm } from '#/components/business/convites/aceitar-convite-form';
import { Spinner } from '#/components/ui/spinner';
import { useQuery } from '@tanstack/react-query';
import { createFileRoute, Navigate } from '@tanstack/react-router';

export const Route = createFileRoute('/convidar-membro/$id')({
  component: ConvidarMembroPage,
});

function ConvidarMembroPage() {
  const { id } = Route.useParams();

  const conviteQuery = useQuery({
    queryKey: ['convites', 'validar', id],
    queryFn: () => validarConvite(id),
    retry: false,
  });

  if (conviteQuery.isPending) {
    return (
      <main className="flex min-h-svh items-center justify-center">
        <div className="flex items-center gap-3 text-muted-foreground">
          <Spinner />
          <span>Validando seu convite...</span>
        </div>
      </main>
    );
  }

  if (conviteQuery.isError) {
    return (
      <main className="flex min-h-svh items-center justify-center p-6">
        <p role="alert" className="text-sm text-destructive">
          Não foi possível validar o convite. Verifique sua conexão
          ou tente novamente.
        </p>
      </main>
    );
  }

  if (conviteQuery.data === false) {
    return <Navigate to="/login" replace />;
  }

  return <AceitarConviteForm idConvite={id} />;
}