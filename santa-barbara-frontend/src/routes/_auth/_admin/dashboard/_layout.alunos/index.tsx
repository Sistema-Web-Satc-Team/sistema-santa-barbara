import { AlunosPage } from '#/components/pages/AlunosPage'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_auth/_admin/dashboard/_layout/alunos/')({
  component: Alunos,
})

function Alunos() {
  return <AlunosPage />
}
