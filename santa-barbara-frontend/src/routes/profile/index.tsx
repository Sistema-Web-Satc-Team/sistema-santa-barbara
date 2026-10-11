import { Button } from '#/components/ui/button';
import { Card, CardContent, CardFooter, CardHeader } from '#/components/ui/card';
import { Spinner } from '#/components/ui/spinner';
import { useAuth } from '#/hooks/use-auth';
import { useForm } from '@tanstack/react-form';
import { createFileRoute, Navigate, useNavigate } from '@tanstack/react-router';
import { toast } from 'sonner';
import z from 'zod';

import { Avatar, AvatarFallback, AvatarImage } from '#/components/ui/avatar';
import { CardDescription, CardTitle } from '#/components/ui/card';
import { Field, FieldError, FieldLabel } from '#/components/ui/field';
import { Input } from '#/components/ui/input';

export const Route = createFileRoute('/profile/')({
  component: Profile,
})

function Profile() {
    const { data, state, actions } = useAuth();
    const navigate = useNavigate();

    const displayName = data?.nome || data?.nomeUsuario || data?.email || "Usuário";
    const userInitials = displayName.slice(0, 2).toUpperCase();

    const formSchema = z.object({
        email: z.email().nonempty(),
        endereco: z
                  .string()
                  .min(6, "O endereço muito pequeno.")
                  .max(256, "Endereço muito grande.")
                  .nonoptional(),
        telefone: z.string()
            .min(11, "Telefone precisa ter no minimo 11 caracteres.")
            .max(20, "Telefone com muitos caracteres")
            .nonoptional()
      })
    
      const form = useForm({
        defaultValues: {
          email: data.email,
          endereco: data.endereco,
          telefone: data.telefone
        },
        validators: {
          onSubmit: formSchema
        },
        onSubmit: async ({value}) => {
          try {
            await actions.updateProfile({
                email: value.email.toString(),
                endereco: value.endereco,
                telefone: value.telefone
            })
    
            toast.success("Dados atualizados com sucesso.")
    
    
          } catch (err: any) {
            toast.error(err.message || "Erro ao atualizar os dados.");
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
  
    if (!data?.id || data?.id == "") {
      return <Navigate to="/login" replace />
    }


    return (
        <div className="flex items-center justify-center w-screen h-screen">
            <Card className='min-w-xl'>
                <CardHeader className="flex flex-row items-center gap-4 space-y-0">
                    <Avatar className='w-16 h-16'>
                        <AvatarImage src={data.urlAvatar ?? ""} alt={data.nome} />
                        <AvatarFallback>
                            {userInitials}
                        </AvatarFallback>
                    </Avatar>
                    <div className="flex flex-col">
                        <CardTitle className="text-2xl">{displayName}</CardTitle>
                        <CardDescription>Gerencie suas informações pessoais e de contato.</CardDescription>
                    </div>
                </CardHeader>

                <form onSubmit={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    form.handleSubmit();
                }}>
                    <CardContent className="space-y-4">
                        <form.Field name="email">
                            {(field) => {
                                const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0;
                                return (
                                    <Field data-invalid={isInvalid} className="mb-4 text-foreground">
                                        <FieldLabel htmlFor={field.name}>E-mail</FieldLabel>
                                        <Input
                                            id={field.name}
                                            name={field.name}
                                            value={field.state.value}
                                            onBlur={field.handleBlur}
                                            onChange={(e) => field.handleChange(e.target.value)}
                                            aria-invalid={isInvalid}
                                            placeholder="seu@email.com"
                                            className="placeholder:tracking-widest aria-invalid:placeholder:text-destructive/50"
                                            type="email"
                                        />
                                        {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
                                    </Field>
                                );
                            }}
                        </form.Field>

                        <form.Field name="endereco">
                            {(field) => {
                                const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0;
                                return (
                                    <Field data-invalid={isInvalid} className="mb-4 text-foreground">
                                        <FieldLabel htmlFor={field.name}>Endereço</FieldLabel>
                                        <Input
                                            id={field.name}
                                            name={field.name}
                                            value={field.state.value}
                                            onBlur={field.handleBlur}
                                            onChange={(e) => field.handleChange(e.target.value)}
                                            aria-invalid={isInvalid}
                                            placeholder="Rua Exemplo, 123"
                                        />
                                        {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
                                    </Field>
                                );
                            }}
                        </form.Field>

                        <form.Field name="telefone">
                            {(field) => {
                                const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0;
                                return (
                                    <Field data-invalid={isInvalid} className="mb-4 text-foreground">
                                        <FieldLabel htmlFor={field.name}>Telefone</FieldLabel>
                                        <Input
                                            id={field.name}
                                            name={field.name}
                                            value={field.state.value}
                                            onBlur={field.handleBlur}
                                            onChange={(e) => field.handleChange(e.target.value)}
                                            aria-invalid={isInvalid}
                                            placeholder="(00) 00000-0000"
                                        />
                                        {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
                                    </Field>
                                );
                            }}
                        </form.Field>
                    </CardContent>

                    <CardFooter className="flex justify-end gap-2 mt-8">
                        <form.Subscribe
                            selector={(state) => [state.canSubmit, state.isSubmitting]}
                        >
                            {([canSubmit, isSubmitting]) => (
                                <Button type="submit" disabled={!canSubmit}>
                                    {isSubmitting ? <Spinner className="mr-2 size-4" /> : null}
                                    Salvar Alterações
                                </Button>
                            )}
                        </form.Subscribe>
                    </CardFooter>
                </form>
            </Card>
        </div>
    );
}
