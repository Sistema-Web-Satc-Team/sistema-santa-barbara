import z from "zod";

const digitos = (v: string) => v.replace(/\D/g, '')

export const formSchemaProfileUpdate = z.object({
    telefone: z
            .string()
            .refine((v) => v === '' || digitos(v).length >= 11, 'O telefone deve ter pelo menos 11 dígitos.'),
    endereco: z
        .string()
        .trim()
        .refine((v) => v === '' || v.length >= 2, 'O endereço deve ter pelo menos 2 caracteres.'),
})