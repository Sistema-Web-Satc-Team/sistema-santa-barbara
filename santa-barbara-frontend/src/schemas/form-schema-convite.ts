
import { canalValido } from '#/components/business/convites/convites'
import { z } from 'zod'

export const formConviteSchema = z.object({
  idMembro: z.string().min(1, 'Selecione um membro.'),
  canal: z.string().refine((v) => canalValido(v), 'Selecione o canal de envio.'),
})

export type ConviteFormValues = z.infer<typeof formConviteSchema>