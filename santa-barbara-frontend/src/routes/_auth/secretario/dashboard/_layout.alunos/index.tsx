import { AlunosPage } from '#/components/pages/AlunosPage'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_auth/secretario/dashboard/_layout/alunos/')({
  component: Alunos,
})

function Alunos() {
  return <AlunosPage />
}
