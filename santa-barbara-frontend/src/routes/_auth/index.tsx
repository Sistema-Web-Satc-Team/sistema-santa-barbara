import type { Role } from "#/api/funcoes";
import { HubCard } from "#/components/business/hub/HubCard";
import { Spinner } from "#/components/ui/spinner";
import { useAuth } from "#/hooks/use-auth";
import { Navigate, createFileRoute, redirect, type ToPathOption } from "@tanstack/react-router";


const RolePageRedirect: Record<string, ToPathOption & {}> = {
  SUPER_ADMIN: "/dashboard/membros",
  SECRETARIO: "/secretario/dashboard/membros",
  ALUNO: "/aluno/musicas",
  PROFESSOR: "/professor/musicas",
  MUSICO_BANDA: "/musico/musicas",
  MAESTRO: "/maestro/musicas"
}


export const Route = createFileRoute('/_auth/')({
  beforeLoad: ({ context }) => {

    if (context.profile.funcoes.length == 1) {
      const role = context.profile.funcoes[0].code;
      throw redirect({ to: RolePageRedirect[role] })
    }

  },
  component: Hub,
})

function Hub() {
    const { state, actions } = useAuth();

    if (state.isLoading) {
        return <Spinner />
    }

    if (!state.profile) {
       <Navigate to="/" replace />
    }

    return <>
        <h2 className="text-4xl font-bold mx-4 mt-8 mb-16 text-primary">Selecione o seu papel</h2>

        <div className="grid grid-cols-3 mx-32 gap-4">
          {state.profile?.funcoes.map((role) => (
            <HubCard role={role as Role} route={RolePageRedirect[role.code]} callback={() => {
              actions.setCurrentRole(role.code);
            }} />
          ))}
        </div>
    </>


}