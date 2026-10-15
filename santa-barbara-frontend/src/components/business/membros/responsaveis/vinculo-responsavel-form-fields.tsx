import { Field, FieldError, FieldLabel } from '#/components/ui/field'
import { withForm } from '#/lib/form'
import type { VinculoResponsavelValues } from '#/schemas/form-schema-vinculo-responsavel'
import { ParentescoSelect } from './parentesco-select'
import { ResponsavelSelect } from './responsavel-select'

export const VinculoResponsavelFormFields = withForm({
  defaultValues: {} as VinculoResponsavelValues,
  props: {
    nomeFixo: undefined as string | undefined,
    excludeIds: [] as string[],
  },

  render: ({ form, nomeFixo, excludeIds }) => (
    <div className="space-y-4">
      <form.Field name="idResponsavel">
        {(field) => {
          const invalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
          return (
            <Field>
              <FieldLabel htmlFor={field.name}>Responsável*</FieldLabel>
              {nomeFixo ? (
                <p className="rounded-md border bg-muted/40 px-3 py-2 text-sm">{nomeFixo}</p>
              ) : (
                <ResponsavelSelect
                  id={field.name}
                  value={field.state.value}
                  onChangeValue={field.handleChange}
                  onBlur={field.handleBlur}
                  isInvalid={invalid}
                  excludeIds={excludeIds}
                />
              )}
              {invalid && <FieldError errors={field.state.meta.errors} />}
            </Field>
          )
        }}
      </form.Field>

      <form.Field name="grauRelacao">
        {(field) => {
          const invalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
          return (
            <Field>
              <FieldLabel htmlFor={field.name}>Grau de relação*</FieldLabel>
              <ParentescoSelect
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
    </div>
  ),
})