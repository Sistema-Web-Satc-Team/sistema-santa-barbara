import { MusicasPage } from '#/components/pages/MusicasPage'
import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/_auth/maestro/_layout/musicas')({
  component: Musicas,
})

function Musicas() {
  return <MusicasPage />
}
