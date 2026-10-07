import { Button } from "@/components/ui/button"
import {
  Field,
  FieldError,
  // FieldGroup,
  FieldLabel
} from "@/components/ui/field"
import { useForm } from "@tanstack/react-form"
import { createFileRoute, Navigate, useNavigate } from '@tanstack/react-router'
import { toast } from "sonner"
import * as z from "zod"

import { Spinner } from "#/components/ui/spinner"
import { useAuth } from "#/hooks/use-auth"
import { Input } from "@/components/ui/input"


/*
import {
  InputGroup,
  InputGroupAddon,
  InputGroupText,
  InputGroupTextarea,
} from "@/components/ui/input-group"
*/

export const Route = createFileRoute('/login/')({
  component: Login,
})


function Login() {
  const navigate = useNavigate();
  const { data, state, actions } = useAuth();

  const formSchema = z.object({
    email: z.email(),
    senha: z
              .string()
              .min(6, "A senha deve ter pelo menos 6 caracteres.")
              .max(256, "A senha deve ter menos de 256 caracteres.")
  })

  const form = useForm({
    defaultValues: {
      email: "",
      senha: ""
    },
    validators: {
      onSubmit: formSchema
    },
    onSubmit: async ({value}) => {
      try {
        await actions.login({
          login: value.email,
          senha: value.senha
        })

        toast.success("Login efetuado com sucesso.")

        await navigate({
          to: "/dashboard/membros"
        })

      } catch (err: any) {
        toast.error(err.message || "Erro ao fazer login.");
      }
    }
  })


  if (state.isLoading) {
    return (
      <div className="w-full flex items-center justify-center min-h-screen">
        <Spinner />
      </div>
    )
  }

  if (data?.id && data.id.length > 0) {
    return <Navigate to="/dashboard/membros" replace />
  }

  return (
    <div className='w-full flex items-center justify-center min-h-screen'>
      <form
        onSubmit={(e) => {
          e.preventDefault()
          form.handleSubmit()
        }}
        className="flex flex-col min-w-112.5 border px-6 py-12 rounded-lg"
      >
        <h2 className="text-2xl font-bold mx-auto mb-1 text-primary">Login</h2>
        <h3 className="text-md font-light mb-12 mx-auto text-primary/70">Bem-vindo a Banda Santa Bárbara!</h3>

        <form.Field
          name="email"
          children={(field) => {
            const isInvalid =
              field.state.meta.isTouched && !field.state.meta.isValid
            return (
              <Field data-invalid={isInvalid} className="mb-4 text-foreground">
                <FieldLabel htmlFor={field.name}>Email</FieldLabel>
                <Input
                  id={field.name}
                  name={field.name}
                  value={field.state.value}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                  aria-invalid={isInvalid}
                  placeholder="Email"
                  className="aria-invalid:placeholder:text-destructive/50"
                  autoComplete="on"
                />
                {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
              </Field>
            )
          }}
        />


        <form.Field
          name="senha"
          children={(field) => {
            const isInvalid =
              field.state.meta.isTouched && !field.state.meta.isValid
            return (
              <Field data-invalid={isInvalid} className="mb-4 text-foreground">
                <FieldLabel htmlFor={field.name}>Senha</FieldLabel>
                <Input
                  id={field.name}
                  name={field.name}
                  value={field.state.value}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                  aria-invalid={isInvalid}
                  placeholder="••••••••"
                  className="place placeholder:tracking-widest aria-invalid:placeholder:text-destructive/50"
                  type="password"
                  autoComplete="on"
                />
                {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
              </Field>
            )
          }}
        />

        <Button type="submit" className="w-full mt-8">Entrar</Button>
      </form>
    </div>
  )
}
