import { MembrosPage } from "#/components/pages/MembrosPage";
import { createFileRoute } from '@tanstack/react-router';

export const Route = createFileRoute('/_auth/_admin/dashboard/_layout/membros/')({
  component: Membros,
})

function Membros() {

    return (
        <MembrosPage />
    )
}