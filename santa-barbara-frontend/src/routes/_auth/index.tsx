import { Spinner } from "#/components/ui/spinner";
import { useAuth } from "#/hooks/use-auth";
import { Navigate, createFileRoute, redirect } from "@tanstack/react-router";


export const Route = createFileRoute('/_auth/')({
  beforeLoad: ({ context }) => {
    if (context.profile.funcoes.includes('SUPER_ADMIN')) {
      throw redirect({ to: '/dashboard/membros' })
    }
  },
  component: Hub,
})

function Hub() {
    const { state } = useAuth();

    if (state.isLoading) {
        return <Spinner />
    }

    if (state.profile?.funcoes.includes("SUPER_ADMIN")) {
        return <Navigate to="/dashboard/membros" replace />
    }

    return <>
        <h2>Selecione o seu papel</h2>

    </>


}