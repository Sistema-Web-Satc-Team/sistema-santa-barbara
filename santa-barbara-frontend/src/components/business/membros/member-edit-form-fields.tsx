import { Field, FieldError, FieldLabel } from '#/components/ui/field'
import { Input } from '#/components/ui/input'
import { Button } from '#/components/ui/button'
import { withForm } from '#/lib/form'
import { ehMenor } from '#/lib/idade'
import type { MemberUpdateValues } from '#/schemas/form-schema-member-update'
import { Star } from 'lucide-react'
import { useState } from 'react'
import { DataNascimentoInput } from './data-nascimento'
import { SexoSelect } from './sexo-select'
import { TelefoneInput } from './telefone-input'

function RatingStars({ value, onChange }: { value: number; onChange: (value: number) => void }) {
  const [hoverValue, setHoverValue] = useState<number | null>(null)
  const displayedValue = hoverValue ?? value

  function valueAtPointer(index: number, clientX: number, left: number, width: number) {
    return index * 10 + (clientX - left < width / 2 ? 5 : 10)
  }

  return (
    <div className="flex items-center gap-1">
      <div className="flex" role="group" aria-label="Avaliação do membro">
        {Array.from({ length: 5 }, (_, index) => {
          const fill = Math.max(0, Math.min(1, displayedValue / 10 - index))
          return (
            <button
              key={index}
              type="button"
              className="relative rounded-sm p-1 text-primary outline-none focus-visible:ring-2 focus-visible:ring-ring"
              aria-label={`${index + 1} ${index === 0 ? 'estrela' : 'estrelas'}; clique na metade esquerda para meia estrela`}
              aria-pressed={value >= (index + 1) * 10}
              title={`${index + 1} ${index === 0 ? 'estrela' : 'estrelas'}; clique na metade esquerda para meia estrela`}
              onPointerMove={(event) => {
                if (event.pointerType === 'touch') return
                const rect = event.currentTarget.getBoundingClientRect()
                setHoverValue(valueAtPointer(index, event.clientX, rect.left, rect.width))
              }}
              onPointerLeave={() => setHoverValue(null)}
              onClick={(event) => {
                if (event.detail === 0) {
                  onChange((index + 1) * 10)
                  return
                }
                const rect = event.currentTarget.getBoundingClientRect()
                onChange(valueAtPointer(index, event.clientX, rect.left, rect.width))
              }}
              onKeyDown={(event) => {
                if (event.key === 'ArrowRight' || event.key === 'ArrowUp') {
                  event.preventDefault()
                  onChange(Math.min(50, Math.max(5, value + 5)))
                } else if (event.key === 'ArrowLeft' || event.key === 'ArrowDown') {
                  event.preventDefault()
                  onChange(Math.max(0, value - 5))
                }
              }}
            >
              <Star className="size-6 text-muted-foreground/40" aria-hidden="true" />
              {fill > 0 && (
                <span className="absolute inset-y-1 left-1 overflow-hidden" style={{ width: `${fill * 24}px` }}>
                  <Star className="size-6 fill-current" aria-hidden="true" />
                </span>
              )}
            </button>
          )
        })}
      </div>
      {value > 0 ? (
        <Button type="button" variant="ghost" size="sm" onClick={() => onChange(0)}>
          Remover
        </Button>
      ) : (
        <span className="text-sm text-muted-foreground">Sem avaliação</span>
      )}
      {value > 0 && <span className="sr-only">{(value / 10).toLocaleString('pt-BR')} de 5 estrelas</span>}
    </div>
  )
}

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

      <form.Field name="avaliacao">
        {(field) => (
          <Field>
            <FieldLabel htmlFor={field.name}>Estrelas (opcional)</FieldLabel>
            <RatingStars value={field.state.value} onChange={field.handleChange} />
          </Field>
        )}
      </form.Field>
    </div>
  ),
})