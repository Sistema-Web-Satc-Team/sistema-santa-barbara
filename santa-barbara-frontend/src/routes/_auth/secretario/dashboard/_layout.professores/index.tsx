import { ProfessoresPage } from '#/components/pages/ProfessoresPage'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute(
  '/_auth/secretario/dashboard/_layout/professores/',
)({
  component: Professores,
})

function Professores() {
  return <ProfessoresPage />
}
