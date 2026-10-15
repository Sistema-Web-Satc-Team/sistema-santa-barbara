import { Field, FieldError, FieldLabel } from '#/components/ui/field'
import { Input } from '#/components/ui/input'
import { withForm } from '#/lib/form'
import { ehMenor } from '#/lib/idade'
import type { MemberUpdateValues } from '#/schemas/form-schema-member-update'
import { DataNascimentoInput } from './data-nascimento'
import { SexoSelect } from './sexo-select'
import { TelefoneInput } from './telefone-input'

export const MemberEditFormFields = withForm({
  defaultValues: {} as MemberUpdateValues,

  render: ({ form }) => (
    <div className="space-y-4">
      <form.Field name="nome">
        {(field) => {
          const invalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
          return (
            <Field>
              <FieldLabel htmlFor={field.name}>Nome completo*</FieldLabel>
              <Input
                id={field.name}
                value={field.state.value}
                onChange={(e) => field.handleChange(e.target.value)}
                onBlur={field.handleBlur}
                aria-invalid={invalid}
              />
              {invalid && <FieldError errors={field.state.meta.errors} />}
            </Field>
          )
        }}
      </form.Field>

      <div className="flex gap-4">
        <form.Field name="sexo">
          {(field) => {
            const invalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
            return (
              <Field>
                <FieldLabel htmlFor={field.name}>Sexo*</FieldLabel>
                <SexoSelect
                  id={field.name}
                  value={field.state.value}
                  handleBlur={field.handleBlur}
                  handleChange={field.handleChange}
                />
                {invalid && <FieldError errors={field.state.meta.errors} />}
              </Field>
            )
          }}
        </form.Field>

        <form.Field name="dataNascimento">
          {(field) => {
            const invalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
            return (
              <Field className="ml-auto">
                <FieldLabel>Data de nascimento*</FieldLabel>
                <DataNascimentoInput
                  value={field.state.value}
                  onChangeValue={field.handleChange}
                  onBlur={field.handleBlur}
                  isInvalid={invalid}
                />
                {invalid && <FieldError errors={field.state.meta.errors} />}
              </Field>
            )
          }}
        </form.Field>
      </div>

      <form.Field name="email">
        {(field) => {
          const invalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
          return (
            <Field>
              <form.Subscribe selector={(s) => ehMenor(s.values.dataNascimento)}>
                {(menor) => (
                  <FieldLabel htmlFor={field.name}>{menor ? 'E-mail (opcional)' : 'E-mail*'}</FieldLabel>
                )}
              </form.Subscribe>
              <Input
                id={field.name}
                value={field.state.value}
                onChange={(e) => field.handleChange(e.target.value)}
                onBlur={field.handleBlur}
                aria-invalid={invalid}
              />
              {invalid && <FieldError errors={field.state.meta.errors} />}
            </Field>
          )
        }}
      </form.Field>

      <form.Field name="telefone">
        {(field) => {
          const invalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
          return (
            <Field>
              <FieldLabel htmlFor={field.name}>Telefone</FieldLabel>
              <TelefoneInput
                id={field.name}
                value={field.state.value}
                onChangeValue={field.handleChange}
                onBlur={field.handleBlur}
              />
              {invalid && <FieldError errors={field.state.meta.errors} />}
            </Field>
          )
        }}
      </form.Field>

      <form.Field name="endereco">
        {(field) => {
          const invalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
          return (
            <Field>
              <FieldLabel htmlFor={field.name}>Endereço</FieldLabel>
              <Input
                id={field.name}
                value={field.state.value}
                onChange={(e) => field.handleChange(e.target.value)}
                onBlur={field.handleBlur}
                aria-invalid={invalid}
              />
              {invalid && <FieldError errors={field.state.meta.errors} />}
            </Field>
          )
        }}
      </form.Field>
    </div>
  ),
})