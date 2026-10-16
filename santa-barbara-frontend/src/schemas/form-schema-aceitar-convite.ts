import { z } from 'zod';

export const aceitarConviteSchema = z.object({
  nomeDeUsuario: z
    .string()
    .trim()
    .min(3, 'O nome de usuário deve ter pelo menos 3 caracteres.')
    .max(100, 'O nome de usuário deve ter no máximo 100 caracteres.'),

  senha: z
    .string()
    .min(6, 'A senha deve ter pelo menos 6 caracteres.')
    .max(256, 'A senha deve ter no máximo 256 caracteres.'),


  confirmarSenha: z.string().min(1, 'Confirme sua senha.'),

});

export type AceitarConviteFormData = z.infer<
  typeof aceitarConviteSchema
>;