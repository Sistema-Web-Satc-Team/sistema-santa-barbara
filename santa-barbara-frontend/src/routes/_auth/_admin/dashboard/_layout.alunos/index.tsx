import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_auth/_admin/dashboard/_layout/alunos/')({
  component: RouteComponent,
})

function RouteComponent() {
  return <div>Hello "/dashboard/_layout/alunos/"!</div>
}
