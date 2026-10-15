import { Field, FieldError, FieldLabel } from "#/components/ui/field";
import { TelefoneInput } from "./telefone-input";

import { Input } from "#/components/ui/input";
import { withForm } from "#/lib/form";
import { ehMenor } from "#/lib/idade";
import type { MemberFormValues } from "#/schemas/form-schema-member";



export const PersonContactFormFields = withForm({

  defaultValues: {
    nome: '',
    sexo: "NAO_INFORMADO",
    email: '',
    telefone: '',
    dataNascimento: { dia: '', mes: '', ano: '' },
    endereco: '',
  } as MemberFormValues,

  render: ({ form }) => (
    <div className="space-y-4">

      <form.Field name="email">
        {(field) => (
            <Field className="text-foreground">
                <form.Subscribe selector={(s) => ehMenor(s.values.dataNascimento)}>
                  {(menor) => (
                    <FieldLabel htmlFor={field.name}>{menor ? 'Email (opcional)' : 'Email*'}</FieldLabel>
                  )}
                </form.Subscribe>

                <Input 
                    id={field.name}
                    name={field.name}
                    onChange={(e) => field.handleChange(e.target.value)}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    placeholder="E-mail"
                />

                {
                    field.state.meta.isTouched && field.state.meta.errors.length > 0 && (
                        <FieldError errors={field.state.meta.errors} />
                    )
                }
            </Field>
        )}
      </form.Field>
      

      <form.Field name="telefone">
        {(field) => (
          <Field className="text-foreground">
            <FieldLabel htmlFor={field.name}>Telefone</FieldLabel>
            <TelefoneInput
              id={field.name}
              value={field.state.value}
              onChangeValue={field.handleChange}
              onBlur={field.handleBlur}
            />
            {field.state.meta.isTouched && field.state.meta.errors.length > 0 && (
              <FieldError errors={field.state.meta.errors} />
            )}
          </Field>
        )}
      </form.Field>


      <form.Field name="endereco">
        {(field) => (
            <Field className="text-foreground">
                <FieldLabel htmlFor={field.name}>Endereço</FieldLabel>

                <Input 
                    id={field.name}
                    name={field.name}
                    onChange={(e) => field.handleChange(e.target.value)}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    placeholder="Endereço"
                />

                {
                    field.state.meta.isTouched && field.state.meta.errors.length > 0 && (
                        <FieldError errors={field.state.meta.errors} />
                    )
                }
            </Field>
        )}
      </form.Field>
      
    </div>
  ),
})