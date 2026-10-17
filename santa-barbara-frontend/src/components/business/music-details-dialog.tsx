import type { Musica, MusicaInput } from '#/api/musica'
import { GenreMultiSelect } from '#/components/business/genre-multi-select'
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
import { Spinner } from '#/components/ui/spinner'
import { useForm } from '@tanstack/react-form'
import z from 'zod'

const formSchema = z.object({
  titulo: z.string().trim().min(1, 'Informe o título da música.'),
  compositor: z.string().trim().min(1, 'Informe o compositor.'),
  generos: z.array(z.string()),
})

interface MusicFormProps {
  musica: Musica
  onClose: () => void
  onSubmit: (values: MusicaInput) => Promise<void>
}


function MusicForm({ musica, onClose, onSubmit }: MusicFormProps) {
  const form = useForm({
    defaultValues: {
      titulo: musica.titulo,
      compositor: musica.compositor,
      generos: musica.generos,
    },
    validators: {
      onSubmit: formSchema,
    },
    onSubmit: async ({ value }) => {
      const values: MusicaInput = {
        titulo: value.titulo.trim(),
        compositor: value.compositor.trim(),
        generos: value.generos,
      }

      const unchanged =
        values.titulo === musica.titulo &&
        values.compositor === musica.compositor &&
        values.generos.length === musica.generos.length &&
        values.generos.every((genero, index) => genero === musica.generos[index])

      try {

        if (!unchanged) await onSubmit(values)
        onClose()
      } catch {

      }
    },
  })

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault()
        e.stopPropagation()
        void form.handleSubmit()
      }}
      className="space-y-4"
    >
      <form.Field name="titulo">
        {(field) => {
          const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
          return (
            <Field data-invalid={isInvalid} className="text-foreground">
              <FieldLabel htmlFor={field.name}>Título*</FieldLabel>
              <Input
                id={field.name}
                name={field.name}
                value={field.state.value}
                onBlur={field.handleBlur}
                onChange={(e) => field.handleChange(e.target.value)}
                aria-invalid={isInvalid}
                placeholder="Ex.: Evidências"
              />
              {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
            </Field>
          )
        }}
      </form.Field>

      <form.Field name="compositor">
        {(field) => {
          const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0
          return (
            <Field data-invalid={isInvalid} className="text-foreground">
              <FieldLabel htmlFor={field.name}>Compositor*</FieldLabel>
              <Input
                id={field.name}
                name={field.name}
                value={field.state.value}
                onBlur={field.handleBlur}
                onChange={(e) => field.handleChange(e.target.value)}
                aria-invalid={isInvalid}
                placeholder="Ex.: José Augusto"
              />
              {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
            </Field>
          )
        }}
      </form.Field>

      <form.Field name="generos">
        {(field) => (
          <Field className="text-foreground">
            <FieldLabel htmlFor={field.name}>Gêneros</FieldLabel>
            <GenreMultiSelect
              id={field.name}
              value={field.state.value}
              onChange={field.handleChange}
              allowCreate
              showChips
              placeholder="Selecione ou adicione gêneros"
            />
          </Field>
        )}
      </form.Field>

      <DialogFooter>
        <Button type="button" variant="outline" onClick={onClose}>
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
  )
}

interface MusicDetailsDialogProps {
  /** null = diálogo fechado. */
  musica: Musica | null
  onClose: () => void
  onSubmit: (values: MusicaInput) => Promise<void>
}

export function MusicDetailsDialog({ musica, onClose, onSubmit }: MusicDetailsDialogProps) {
  return (
    <Dialog
      open={musica !== null}
      onOpenChange={(open) => {
        if (!open) onClose()
      }}
    >
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle className="text-xl">Detalhes da música</DialogTitle>
          <DialogDescription>Veja e edite as informações da música.</DialogDescription>
        </DialogHeader>

        {musica && <MusicForm musica={musica} onClose={onClose} onSubmit={onSubmit} />}
      </DialogContent>
    </Dialog>
  )
}
