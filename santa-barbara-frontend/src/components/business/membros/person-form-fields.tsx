import { Field, FieldError, FieldLabel } from "#/components/ui/field";
import { Input } from "#/components/ui/input";

import { withForm } from "#/lib/form";
import { ehMenor } from "#/lib/idade";
import type { MemberFormValues } from "#/schemas/form-schema-member";
import { DataNascimentoInput } from "./data-nascimento";
import { SexoSelect } from "./sexo-select";

export const PersonFormFields = withForm({

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
      <form.Field name="nome">
        {(field) => (
            <Field className="text-foreground">
                <FieldLabel htmlFor={field.name}>Nome Completo*</FieldLabel>

                <Input     
                    id={field.name}
                    name={field.name}
                    onChange={(e) => field.handleChange(e.target.value)}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    type="text"
                    placeholder="Nome"
                    required={true}
                />

                {
                    field.state.meta.isTouched && field.state.meta.errors.length > 0 && (
                        <FieldError errors={field.state.meta.errors} />
                    )
                }
            </Field>
        )}
      </form.Field>

      <div className="flex gap-4">

        <form.Field name="sexo">
            {(field) => {
                const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0;
                return (
                    <Field>
                        <FieldLabel htmlFor={field.name}>Sexo*</FieldLabel>
                        <SexoSelect
                            id={field.name}
                            value={field.state.value}
                            handleBlur={field.handleBlur}
                            handleChange={field.handleChange}
                        />
                        {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
                    </Field>
                );
            }}
        </form.Field>

        <form.Field name="dataNascimento">
            {(field) => (
                <Field className="text-foreground ml-auto">
                    <FieldLabel htmlFor={field.name}>Data de Nascimento*</FieldLabel>

                    <form.Subscribe selector={(s) => s.values.dataNascimento}>
                        {(d) => { console.log('data:', d, 'menor:', ehMenor(d)); return null }}
                    </form.Subscribe>

                    <DataNascimentoInput 
                        value={field.state.value}
                        onChangeValue={field.handleChange}
                        onBlur={field.handleBlur}
                        isInvalid={field.state.meta.isTouched && field.state.meta.errors.length > 0}
                        
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



    </div>
  ),
})