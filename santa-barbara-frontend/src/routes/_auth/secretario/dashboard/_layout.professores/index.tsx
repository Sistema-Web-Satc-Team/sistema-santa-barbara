import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_auth/secretario/dashboard/_layout/professores/')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/dashboard/_layout/professores/"!</div>
}
