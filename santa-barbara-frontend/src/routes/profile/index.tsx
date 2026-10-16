import { ProfileAvatarEditor } from '#/components/business/profile-avatar-editor';
import { Button } from '#/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '#/components/ui/card';
import { Field, FieldError, FieldLabel } from '#/components/ui/field';
import { Input } from '#/components/ui/input';
import { Spinner } from '#/components/ui/spinner';
import { useAuth } from '#/hooks/use-auth';
import { capitalizeAll } from '#/lib/utils';
import { formSchemaProfileUpdate } from '#/schemas/form-schema-profile-update';
import { useForm } from '@tanstack/react-form';
import { createFileRoute, Navigate, useNavigate } from '@tanstack/react-router';
import { toast } from 'sonner';


export const Route = createFileRoute('/profile/')({
    component: Profile,
})



function Profile() {
    const { state, actions } = useAuth();
    const profile = state.profile;

    const navigate = useNavigate();

    const displayName = profile?.nomeUsuario ?? capitalizeAll(profile?.nome || profile?.email || "Usuário");

   
    const form = useForm({
        defaultValues: {
            endereco: profile?.endereco ?? '',
            telefone: profile?.telefone ?? ''
        },
        validators: {
            onSubmit: formSchemaProfileUpdate
        },
        onSubmit: async ({ value }) => {
            try {
                await actions.updateProfile({
                    endereco: value.endereco.trim() || undefined,
                    telefone: value.telefone.trim() || undefined
                });

                toast.success("Dados atualizados com sucesso.")


            } catch (err: any) {
                console.log(err);
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

    if (!profile?.id) {
        return <Navigate to="/login" replace />
    }


    return (
        <div className="flex items-center justify-center w-screen h-screen">
            <Card className='min-w-xl'>
                <CardHeader className="flex flex-row items-center gap-4 space-y-0">
                    <div className="flex flex-col">
                        <CardTitle className="text-2xl">{displayName}</CardTitle>
                        <CardDescription>Gerencie suas informações pessoais e de contato.</CardDescription>
                    </div>
                </CardHeader>
                <CardContent className="pb-0">
                    <ProfileAvatarEditor />
                </CardContent>

                <div className="mb-0 ml-6 text-foreground flex items-center ">
                    <span className='font-semibold'>E-mail:</span>
                    <span className='text-sm ml-2'>{profile.email}</span>
                </div>

                <form onSubmit={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    form.handleSubmit();
                }}>
                    <CardContent className="space-y-4">
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
                        <Button
                            type="button"
                            variant="outline"
                            onClick={() => navigate({ to: "/" })}
                        >
                            Voltar
                        </Button>

                        <form.Subscribe
                            selector={(state) =>
                                [
                                    state.canSubmit,
                                    state.isSubmitting,
                                    state.values
                                ] as const
                            }
                        >
                            {([canSubmit, isSubmitting, values]) => {
                                const hasChanges =
                                    values.endereco !== (profile?.endereco ?? '') ||
                                    values.telefone !== (profile?.telefone ?? '');

                                return (
                                    <Button
                                        type="submit"
                                        disabled={!canSubmit || isSubmitting || !hasChanges}
                                    >
                                        {isSubmitting && <Spinner className="mr-2 size-4" />}
                                        Salvar Alterações
                                    </Button>
                                );
                            }}
                        </form.Subscribe>
                    </CardFooter>
                </form>
            </Card>
        </div>
    );
}
