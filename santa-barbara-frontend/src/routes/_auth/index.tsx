import { HubCard } from "#/components/business/hub/HubCard";
import { Spinner } from "#/components/ui/spinner";
import { useAuth } from "#/hooks/use-auth";
import { createFileRoute, redirect } from "@tanstack/react-router";
import type { ToPathOption } from "@tanstack/react-router";


const RolePageRedirect: Record<string, ToPathOption & {}> = {
  SUPER_ADMIN: "/dashboard/membros",
  ADMIN: "/dashboard/membros",
  SECRETARIO: "/secretario/dashboard/membros",
  ALUNO: "/aluno/musicas",
  PROFESSOR: "/professor/musicas",
  MUSICO_BANDA: "/musico/musicas",
  MAESTRO: "/maestro/musicas"
}

const RoleDetails: Record<string, { nome: string; descricao: string }> = {
  SUPER_ADMIN: {
    nome: "Administrador supremo",
    descricao: "Acessar todas as áreas administrativas do sistema.",
  },
  ADMIN: {
    nome: "Administrador",
    descricao: "Gerenciar músicas e demais recursos administrativos.",
  },
  SECRETARIO: {
    nome: "Secretário geral",
    descricao: "Gerenciar membros, vínculos e convites.",
  },
  ALUNO: {
    nome: "Aluno",
    descricao: "Consultar músicas e partituras.",
  },
  PROFESSOR: {
    nome: "Professor",
    descricao: "Consultar músicas e partituras.",
  },
  MUSICO_BANDA: {
    nome: "Músico da banda",
    descricao: "Consultar músicas e partituras.",
  },
  MAESTRO: {
    nome: "Maestro",
    descricao: "Gerenciar músicas e partituras.",
  },
}


export const Route = createFileRoute('/_auth/')({
  beforeLoad: ({ context }) => {
    if (context.profile.funcoes.length === 1) {
      const destination = RolePageRedirect[context.profile.funcoes[0]]
      if (destination) {
        throw redirect({ to: destination })
      }
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
       return <Spinner />
    }

    const availableRoles = state.profile.funcoes.filter((role) => RolePageRedirect[role])

    return <>
        <h2 className="text-4xl font-bold mx-4 mt-8 mb-16 text-primary">Selecione o seu papel</h2>

        {availableRoles.length > 0 ? (
        <div className="grid grid-cols-1 gap-4 px-4 sm:grid-cols-2 lg:grid-cols-3 lg:px-32">
          {availableRoles.map((roleCode) => {
            const destination = RolePageRedirect[roleCode]
            const roleDetails = RoleDetails[roleCode]

            return (
              <HubCard
                key={roleCode}
                role={{
                  code: roleCode,
                  nome: roleDetails.nome,
                  descricao: roleDetails.descricao,
                }}
                route={destination}
                callback={() => actions.setCurrentRole(roleCode)}
              />
            )
          })}
        </div>
        ) : (
          <p className="mx-4 text-muted-foreground">
            Sua conta não possui um papel com área disponível. Entre em contato com a administração.
          </p>
        )}
    </>


}