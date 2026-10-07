import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/dashboard/_layout/convites/')({
  component: ConvitarMembro,
})

function ConvitarMembro() {
      return (
        <div className="p-8">
            <h2 className="text-3xl font-bold">Convidar Membros</h2>
            
        </div>
    )
}
