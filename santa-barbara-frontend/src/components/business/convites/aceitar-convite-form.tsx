import { useForm } from '@tanstack/react-form';
import { useNavigate } from '@tanstack/react-router';
import {
    Eye,
    EyeOff,
    KeyRound,
    UserRound
} from 'lucide-react';
import { useState } from 'react';
import { toast } from 'sonner';

import { Button } from '#/components/ui/button';
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle
} from '#/components/ui/card';
import { Field, FieldError, FieldLabel } from '#/components/ui/field';
import { Input } from '#/components/ui/input';
import { Spinner } from '#/components/ui/spinner';

import { useAceitarConvite } from '#/hooks/use-convites';
import { aceitarConviteSchema, type AceitarConviteFormData } from '#/schemas/form-schema-aceitar-convite';


interface AceitarConviteFormProps {
  idConvite: string;
}

export function AceitarConviteForm({
  idConvite,
}: AceitarConviteFormProps) {
  const [mostrarSenha, setMostrarSenha] = useState(false);
  const [mostrarConfirmacao, setMostrarConfirmacao] = useState(false);

  const aceitarConvite = useAceitarConvite();
  const navigate = useNavigate();

  const form = useForm({
    defaultValues: {
      nomeDeUsuario: '',
      senha: '',
      confirmarSenha: '',
    } satisfies AceitarConviteFormData,

    validators: {
      onSubmit: aceitarConviteSchema,
    },

    onSubmit: async ({ value }) => {
      try {
        await aceitarConvite.mutateAsync({
          idConvite,
          nomeDeUsuario: value.nomeDeUsuario.trim(),
          senha: value.senha,
        });

        toast.success('Convite aceito com sucesso!', {
          description: 'Seu acesso foi criado. Agora você pode entrar.',
        });

        await navigate({ to: '/login', replace: true });
      } catch {
        toast.error('Não foi possível aceitar o convite.', {
          description:
            'Verifique os dados e tente novamente. Se o problema persistir, solicite ajuda à administração.',
        });
      }
    },
  });

  return (
    <main className="relative flex min-h-svh items-center justify-center overflow-hidden bg-background px-4 py-10">
      <div
        aria-hidden="true"
        className="pointer-events-none absolute inset-0"
      >
        <div className="absolute -left-32 -top-32 size-80 rounded-full bg-primary/5 blur-3xl" />
        <div className="absolute -bottom-40 -right-20 size-96 rounded-full bg-primary/5 blur-3xl" />
      </div>

      <div className="relative w-full max-w-md">
        <div className="mb-8 flex flex-col items-center text-center">
          <div className="mb-4 flex size-14 items-center justify-center rounded-2xl bg-primary text-primary-foreground shadow-sm">
            <img 
                src="/assets/images/Brand.jpg" 
                alt="Brasão Santa Bárbara" 
                className="size-24 object-contain contrast-[0.94]" 
            />
          </div>

          <p className="mb-2 text-xs font-bold uppercase tracking-[0.2em] text-muted-foreground">
            Santa Bárbara
          </p>

          <h1 className="font-display text-3xl font-bold tracking-tight">
            Seja bem-vindo!
          </h1>

          <p className="mt-2 max-w-sm text-sm leading-6 text-muted-foreground">
            Falta pouco para você fazer parte da plataforma.
            Crie suas credenciais para concluir seu cadastro.
          </p>
        </div>

        <Card className="border-border/80 shadow-lg shadow-primary/5">
          <CardHeader className="space-y-2">
            <CardTitle className="text-xl">
              Configure seu acesso
            </CardTitle>

            <CardDescription>
              Escolha um nome de usuário e uma senha para entrar na
              plataforma.
            </CardDescription>
          </CardHeader>

          <CardContent>
            <form
              id="aceitar-convite-form"
              className="space-y-5"
              noValidate
              onSubmit={(event) => {
                event.preventDefault();
                event.stopPropagation();
                void form.handleSubmit();
              }}
            >
              <form.Field
                name="nomeDeUsuario"
                children={(field) => {
                  const invalid =
                    field.state.meta.isTouched &&
                    !field.state.meta.isValid;

                  return (
                    <Field data-invalid={invalid}>
                      <FieldLabel htmlFor={field.name}>
                        Nome de usuário
                      </FieldLabel>

                      <div className="relative">
                        <UserRound
                          aria-hidden="true"
                          className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"
                        />

                        <Input
                          id={field.name}
                          name={field.name}
                          autoComplete="username"
                          placeholder="Ex.: laurence"
                          className="pl-9"
                          value={field.state.value}
                          onBlur={field.handleBlur}
                          onChange={(event) =>
                            field.handleChange(event.target.value)
                          }
                          aria-invalid={invalid}
                        />
                      </div>

                      {invalid && (
                        <FieldError>
                          {field.state.meta.errors
                            .map((error) =>
                              typeof error === 'string'
                                ? error
                                : error?.message,
                            )
                            .filter(Boolean)
                            .join(', ')}
                        </FieldError>
                      )}
                    </Field>
                  );
                }}
              />

              <form.Field
                name="senha"
                children={(field) => {
                  const invalid =
                    field.state.meta.isTouched &&
                    !field.state.meta.isValid;

                  return (
                    <Field data-invalid={invalid}>
                      <FieldLabel htmlFor={field.name}>
                        Senha
                      </FieldLabel>

                      <div className="relative">
                        <KeyRound
                          aria-hidden="true"
                          className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-muted-foreground"
                        />

                        <Input
                          id={field.name}
                          name={field.name}
                          type={mostrarSenha ? 'text' : 'password'}
                          autoComplete="new-password"
                          placeholder="Crie uma senha segura"
                          className="px-9"
                          value={field.state.value}
                          onBlur={field.handleBlur}
                          onChange={(event) =>
                            field.handleChange(event.target.value)
                          }
                          aria-invalid={invalid}
                        />

                        <button
                          type="button"
                          aria-label={
                            mostrarSenha ? 'Ocultar senha' : 'Mostrar senha'
                          }
                          className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground transition-colors hover:text-foreground"
                          onClick={() =>
                            setMostrarSenha((previous) => !previous)
                          }
                        >
                          {mostrarSenha ? (
                            <EyeOff className="size-4" />
                          ) : (
                            <Eye className="size-4" />
                          )}
                        </button>
                      </div>

                      <p className="text-xs text-muted-foreground">
                        Utilize entre 6 e 256 caracteres.
                      </p>

                      {invalid && (
                        <FieldError>
                          {field.state.meta.errors
                            .map((error) =>
                              typeof error === 'string'
                                ? error
                                : error?.message,
                            )
                            .filter(Boolean)
                            .join(', ')}
                        </FieldError>
                      )}
                    </Field>
                  );
                }}
              />

              <form.Field
                name="confirmarSenha"
                children={(field) => {
                  const invalid =
                    field.state.meta.isTouched &&
                    !field.state.meta.isValid;

                  return (
                    <Field data-invalid={invalid}>
                      <FieldLabel htmlFor={field.name}>
                        Confirmar senha
                      </FieldLabel>

                      <div className="relative">
                        <Input
                          id={field.name}
                          name={field.name}
                          type={mostrarConfirmacao ? 'text' : 'password'}
                          autoComplete="new-password"
                          placeholder="Digite a senha novamente"
                          className="pr-9"
                          value={field.state.value}
                          onBlur={field.handleBlur}
                          onChange={(event) =>
                            field.handleChange(event.target.value)
                          }
                          aria-invalid={invalid}
                        />

                        <button
                          type="button"
                          aria-label={
                            mostrarConfirmacao
                              ? 'Ocultar confirmação de senha'
                              : 'Mostrar confirmação de senha'
                          }
                          className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground transition-colors hover:text-foreground"
                          onClick={() =>
                            setMostrarConfirmacao((previous) => !previous)
                          }
                        >
                          {mostrarConfirmacao ? (
                            <EyeOff className="size-4" />
                          ) : (
                            <Eye className="size-4" />
                          )}
                        </button>
                      </div>

                      {invalid && (
                        <FieldError>
                          {field.state.meta.errors
                            .map((error) =>
                              typeof error === 'string'
                                ? error
                                : error?.message,
                            )
                            .filter(Boolean)
                            .join(', ')}
                        </FieldError>
                      )}
                    </Field>
                  );
                }}
              />

              <Button
                type="submit"
                className="w-full"
                disabled={aceitarConvite.isPending}
              >
                {aceitarConvite.isPending ? (
                  <>
                    <Spinner />
                    Criando seu acesso...
                  </>
                ) : (
                  'Concluir cadastro'
                )}
              </Button>
            </form>
          </CardContent>
        </Card>

        <p className="mt-6 text-center text-xs leading-5 text-muted-foreground">
          Ao concluir, você estará criando suas credenciais de acesso
          à plataforma Santa Bárbara.
        </p>
      </div>
    </main>
  );
}
