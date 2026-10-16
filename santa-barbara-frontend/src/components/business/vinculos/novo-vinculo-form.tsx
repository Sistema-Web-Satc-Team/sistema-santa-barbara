// components/.../vinculos/novo-vinculo-form.tsx
import { Button } from '#/components/ui/button'
import { DatePicker } from '#/components/ui/date-picker'
import { Field, FieldError, FieldLabel } from '#/components/ui/field'
import { hojeParts } from '#/lib/datas'
import { useAppForm } from '#/lib/form'
import { mensagemDeErro } from '#/routes/_auth/dashboard/_layout.membros/registrar-membro'
import { formNovoVinculoSchema } from '#/schemas/form-schema-novo-vinculo'
import { toast } from 'sonner'
import { SelectFuncoes } from './select-funcoes'

interface Props {
  onSubmit: (v: { funcao: string; dataInicio: { dia: string; mes: string; ano: string } }) => Promise<void>
  onCancel: () => void
}

export function NovoVinculoForm({ onSubmit, onCancel }: Props) {
  const form = useAppForm({
    defaultValues: { funcao: '', dataInicio: hojeParts() },
    validators: { onSubmit: formNovoVinculoSchema },
    onSubmit: async ({ value }) => {
      try {
        await onSubmit(value)
        toast.success('Função atribuída com sucesso.')
        form.reset()
        onCancel()
      } catch (err) {
        toast.error(mensagemDeErro(err))
      }
    },
  })

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault()
        e.stopPropagation()
        form.handleSubmit()
      }}
      className="space-y-4 rounded-xl border bg-muted/20 p-4"
    >
      <form.Field name="funcao">
        {(field) => {
          const invalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
          return (
            <Field>
              <FieldLabel htmlFor={field.name}>Função*</FieldLabel>
              <SelectFuncoes
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

      <form.Field name="dataInicio">
        {(field) => {
          const invalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
          return (
            <Field>
              <FieldLabel>Data de início*</FieldLabel>
              <DatePicker
                value={field.state.value}
                onChangeValue={(newValue) => {
                    if (newValue) {
                    field.handleChange(newValue)
                    }
                }}
                isInvalid={invalid}
              />
              {invalid && <FieldError errors={field.state.meta.errors} />}
            </Field>
          )
        }}
      </form.Field>

      <div className="flex justify-end gap-2">
        <Button type="button" variant="outline" size="sm" onClick={onCancel}>
          Cancelar
        </Button>
        <form.Subscribe selector={(s) => s.isSubmitting}>
          {(busy) => (
            <Button type="submit" size="sm" disabled={busy}>
              {busy ? 'Salvando...' : 'Atribuir'}
            </Button>
          )}
        </form.Subscribe>
      </div>
    </form>
  )
}