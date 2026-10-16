import { Field, FieldError, FieldLabel } from '#/components/ui/field'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '#/components/ui/select'
import { withForm } from '#/lib/form'
import type { ConviteFormValues } from '#/schemas/form-schema-convite'
import { CANAIS } from './convites'
import { MembroConviteSelect } from './membro-convite-select'

export const ConviteFormFields = withForm({
  defaultValues: {} as ConviteFormValues,

  render: ({ form }) => (
    <div className="space-y-4">
      <form.Field name="idMembro">
        {(field) => {
          const invalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
          return (
            <Field>
              <FieldLabel htmlFor={field.name}>Membro*</FieldLabel>
              <MembroConviteSelect
                id={field.name}
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

      <form.Field name="canal">
        {(field) => {
          const invalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
          return (
            <Field>
              <FieldLabel htmlFor={field.name}>Enviar por*</FieldLabel>
              <Select value={field.state.value} onValueChange={field.handleChange}>
                <SelectTrigger id={field.name} onBlur={field.handleBlur} aria-invalid={invalid}>
                  <SelectValue placeholder="Selecione o canal" />
                </SelectTrigger>
                <SelectContent>
                  {CANAIS.map((c) => (
                    <SelectItem key={c.value} value={c.value}>{c.label}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {invalid && <FieldError errors={field.state.meta.errors} />}
            </Field>
          )
        }}
      </form.Field>
    </div>
  ),
})