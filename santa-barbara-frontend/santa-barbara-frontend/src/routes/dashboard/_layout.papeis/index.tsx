import { RoleCard } from '#/components/business/role-card';
import { Spinner } from '#/components/ui/spinner';
import { useListRoles } from '#/hooks/use-list-roles';

import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/dashboard/_layout/papeis/')({
  component: Papeis,
})

function Papeis() {
  const { data, state } = useListRoles();

  if (state.isLoading) {
    return (
      <div className="w-full flex items-center justify-center min-h-screen">
        <Spinner />
      </div>
    );
  }

  if (state.isEmpty) {
    return <div className="w-full flex items-center justify-center min-h-screen text-destructive text-2xl">Nenhum papel encontrado.</div>;
  }

  return (
    <div className="p-8">
      <h2 className="text-3xl font-bold mb-8">Papéis</h2>
      <div className="grid grid-cols-2 gap-4">
        {data?.map((role: string) => (
          <RoleCard key={role} role={role} />
        ))}
      </div>
    </div>
  );
}