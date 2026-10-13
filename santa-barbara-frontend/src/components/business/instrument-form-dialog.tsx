import { INSTRUMENT_TYPES, normalizeInstrumentType } from '#/api/instrumento'
import type { Instrumento, InstrumentoInput } from '#/api/instrumento'
import { InstrumentIcon } from '#/components/business/instrument-icon'
import { Button } from '#/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '#/components/ui/dialog'
import { Field, FieldError, FieldLabel } from '#/components/ui/field'
import { Input } from '#/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '#/components/ui/select'
import { Spinner } from '#/components/ui/spinner'
import { Textarea } from '#/components/ui/textarea'
import { useForm } from '@tanstack/react-form'
import z from 'zod'

const formSchema = z.object({
  nome: z.string().trim().min(2, 'O nome deve ter pelo menos 2 caracteres.'),
  descricao: z.string().trim().min(1, 'Informe a descrição do instrumento.'),
  tipo: z
    .string()
    .refine(
      (value) => normalizeInstrumentType(value) !== null,
      'Selecione o tipo do instrumento.',
    ),
})

interface InstrumentFormDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void

  instrumento: Instrumento | null
  onSubmit: (values: InstrumentoInput) => Promise<void>
}


export function InstrumentFormDialog({
  open,
  onOpenChange,
  instrumento,
  onSubmit,
}: InstrumentFormDialogProps) {
  const isEditing = instrumento !== null

  const form = useForm({
    defaultValues: {
      nome: instrumento?.nome ?? '',
      descricao: instrumento?.descricao ?? '',
      tipo: instrumento ? (normalizeInstrumentType(instrumento.tipo) ?? '') : '',
    },
    validators: {
      onSubmit: formSchema,
    },
    onSubmit: async ({ value }) => {
      const tipo = normalizeInstrumentType(value.tipo)
      if (!tipo) return

      try {
        await onSubmit({
          nome: value.nome.trim(),
          descricao: value.descricao.trim(),
          tipo,
        })
        handleOpenChange(false)
      } catch {

      }
    },
  })

  function handleOpenChange(next: boolean) {
    if (!next) form.reset()
    onOpenChange(next)
  }

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle className="text-xl">
            {isEditing ? 'Editar instrumento' : 'Novo instrumento'}
          </DialogTitle>
          <DialogDescription>
            Informe o nome, o tipo e uma descrição do instrumento.
          </DialogDescription>
        </DialogHeader>

        <form
          onSubmit={(e) => {
            e.preventDefault()
            e.stopPropagation()
            void form.handleSubmit()
          }}
          className="space-y-4"
        >
          <form.Field name="nome">
            {(field) => {
              const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
              return (
                <Field data-invalid={isInvalid} className="text-foreground">
                  <FieldLabel htmlFor={field.name}>Nome*</FieldLabel>
                  <Input
                    id={field.name}
                    name={field.name}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => field.handleChange(e.target.value)}
                    aria-invalid={isInvalid}
                    placeholder="Ex.: Trompete"
                  />
                  {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
                </Field>
              )
            }}
          </form.Field>

          <form.Field name="tipo">
            {(field) => {
              const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
              return (
                <Field data-invalid={isInvalid} className="text-foreground">
                  <FieldLabel htmlFor={field.name}>Tipo*</FieldLabel>
                  <Select value={field.state.value} onValueChange={field.handleChange}>
                    <SelectTrigger
                      id={field.name}
                      className="w-full"
                      aria-invalid={isInvalid}
                      onBlur={field.handleBlur}
                    >
                      <SelectValue placeholder="Selecione o tipo" />
                    </SelectTrigger>
                    <SelectContent>
                      {INSTRUMENT_TYPES.map((type) => (
                        <SelectItem key={type.value} value={type.value}>
                          <InstrumentIcon tipo={type.value} className="size-4" />
                          {type.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
                </Field>
              )
            }}
          </form.Field>

          <form.Field name="descricao">
            {(field) => {
              const isInvalid =
                field.state.meta.isTouched && field.state.meta.errors.length > 0
              return (
                <Field data-invalid={isInvalid} className="text-foreground">
                  <FieldLabel htmlFor={field.name}>Descrição*</FieldLabel>
                  <Textarea
                    id={field.name}
                    name={field.name}
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => field.handleChange(e.target.value)}
                    aria-invalid={isInvalid}
                    placeholder="Fale um pouco sobre o instrumento"
                    rows={4}
                  />
                  {isInvalid && (
                    <FieldError errors={field.state.meta.errors} className="-mt-1" />
                  )}
                </Field>
              )
            }}
          </form.Field>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => handleOpenChange(false)}>
              Cancelar
            </Button>
            <form.Subscribe selector={(state) => [state.canSubmit, state.isSubmitting]}>
              {([canSubmit, isSubmitting]) => (
                <Button type="submit" disabled={!canSubmit}>
                  {isSubmitting ? <Spinner className="mr-2 size-4" /> : null}
                  Salvar
                </Button>
              )}
            </form.Subscribe>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
