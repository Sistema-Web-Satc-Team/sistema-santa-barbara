import { Field, FieldError, FieldLabel } from '#/components/ui/field'
import { Input } from '#/components/ui/input'
import { withFieldGroup } from '#/lib/form'
import { responsavelDefaultValues } from '#/schemas/form-schema-responsavel'
import { DataNascimentoInput } from '../data-nascimento'
import { TelefoneInput } from '../telefone-input'
import { ParentescoSelect } from './parentesco-select'
import { ResponsavelModoRadio } from './responsavel-modo-radio'
import { ResponsavelSelect } from './responsavel-select'

export const ResponsavelFormFields = withFieldGroup({
  defaultValues: responsavelDefaultValues,

  render: function Render({ group }) {
    return (
      <div className="space-y-4 rounded-lg">
        <group.Field name="modo">
          {(field) => (
            <Field className='mb-8 mt-4'>
              <ResponsavelModoRadio value={field.state.value} onChangeValue={field.handleChange} />
            </Field>
          )}
        </group.Field>

        <group.Field name="parentesco">
          {(field) => {
            const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
            return (
              <Field>
                <FieldLabel htmlFor={field.name}>Grau de parentesco*</FieldLabel>
                <ParentescoSelect
                  id={field.name}
                  value={field.state.value}
                  onChangeValue={field.handleChange}
                  onBlur={field.handleBlur}
                  isInvalid={isInvalid}
                />
                {isInvalid && <FieldError errors={field.state.meta.errors} />}
              </Field>
            )
          }}
        </group.Field>

        <group.Subscribe selector={(s) => s.values.modo}>
          {(modo) =>
            modo === 'existente' ? (
              <group.Field name="id">
                {(field) => {
                  const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
                  return (
                    <Field>
                      <FieldLabel htmlFor={field.name}>Responsável cadastrado*</FieldLabel>
                      <ResponsavelSelect
                        id={field.name}
                        value={field.state.value}
                        onChangeValue={field.handleChange}
                        onBlur={field.handleBlur}
                        isInvalid={isInvalid}
                        onCreateNew={(termo) => {
                          group.setFieldValue('modo', 'novo')
                          group.setFieldValue('nome', termo)
                        }}
                      />
                      {isInvalid && <FieldError errors={field.state.meta.errors} />}
                    </Field>
                  )
                }}
              </group.Field>
            ) : (
              <div className="space-y-4 border-t pt-4">
                <group.Field name="nome">
                  {(field) => {
                    const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
                    return (
                      <Field>
                        <FieldLabel htmlFor={field.name}>Nome do responsável*</FieldLabel>
                        <Input
                          id={field.name}
                          value={field.state.value}
                          onChange={(e) => field.handleChange(e.target.value)}
                          onBlur={field.handleBlur}
                          aria-invalid={isInvalid}
                          placeholder="Nome completo"
                        />
                        {isInvalid && <FieldError errors={field.state.meta.errors} />}
                      </Field>
                    )
                  }}
                </group.Field>

                <group.Field name="dataNascimento">
                  {(field) => {
                    const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
                    return (
                      <Field>
                        <FieldLabel>Data de nascimento*</FieldLabel>
                        <DataNascimentoInput
                          value={field.state.value}
                          onChangeValue={field.handleChange}
                          onBlur={field.handleBlur}
                          isInvalid={isInvalid}
                        />
                        {isInvalid && <FieldError errors={field.state.meta.errors} />}
                      </Field>
                    )
                  }}
                </group.Field>

                <group.Field name="email">
                  {(field) => {
                    const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
                    return (
                      <Field>
                        <FieldLabel htmlFor={field.name}>E-mail*</FieldLabel>
                        <Input
                          id={field.name}
                          value={field.state.value}
                          onChange={(e) => field.handleChange(e.target.value)}
                          onBlur={field.handleBlur}
                          aria-invalid={isInvalid}
                          placeholder="email@exemplo.com"
                        />
                        {isInvalid && <FieldError errors={field.state.meta.errors} />}
                      </Field>
                    )
                  }}
                </group.Field>

                <group.Field name="telefone">
                  {(field) => {
                    const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
                    return (
                      <Field>
                        <FieldLabel htmlFor={field.name}>Telefone*</FieldLabel>
                        <TelefoneInput
                          id={field.name}
                          value={field.state.value}
                          onChangeValue={field.handleChange}
                          onBlur={field.handleBlur}
                        />
                        {isInvalid && <FieldError errors={field.state.meta.errors} />}
                      </Field>
                    )
                  }}
                </group.Field>
              </div>
            )
          }
        </group.Subscribe>
      </div>
    )
  },
})