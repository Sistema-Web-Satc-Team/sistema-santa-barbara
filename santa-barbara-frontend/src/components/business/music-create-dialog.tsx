import type { MusicaInput } from '#/api/musica'
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
  titulo: z.string().trim().min(1, 'O título é obrigatório.'),
  compositor: z.string().trim(),
  generos: z.array(z.string()),
})

const initialGenres: string[] = []

interface CreateFormProps {
  onClose: () => void
  onSubmit: (values: MusicaInput) => Promise<void>
}


function CreateForm({ onClose, onSubmit }: CreateFormProps) {
  const form = useForm({
    defaultValues: {
      titulo: '',
      compositor: '',
      generos: initialGenres,
    },
    validators: {
      onSubmit: formSchema,
    },
    onSubmit: async ({ value }) => {
      try {
        await onSubmit({
          titulo: value.titulo.trim(),
          compositor: value.compositor.trim(),
          generos: value.generos,
        })
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
              <FieldLabel htmlFor={field.name}>Nome da música*</FieldLabel>
              <Input
                id={field.name}
                name={field.name}
                value={field.state.value}
                onBlur={field.handleBlur}
                onChange={(e) => field.handleChange(e.target.value)}
                aria-invalid={isInvalid}
                placeholder="Ex.: Sleigh Ride"
              />
              {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
            </Field>
          )
        }}
      </form.Field>

      <form.Field name="compositor">
        {(field) => (
          <Field className="text-foreground">
            <FieldLabel htmlFor={field.name}>Compositor</FieldLabel>
            <Input
              id={field.name}
              name={field.name}
              value={field.state.value}
              onBlur={field.handleBlur}
              onChange={(e) => field.handleChange(e.target.value)}
              placeholder="Ex.: Leroy Anderson"
            />
          </Field>
        )}
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
              placeholder="Escolha ou digite um gênero"
            />
            <p className="text-sm text-muted-foreground">
              Usado para organizar o acervo e os programas de concertos.
            </p>
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
              Adicionar música
            </Button>
          )}
        </form.Subscribe>
      </DialogFooter>
    </form>
  )
}

interface MusicCreateDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSubmit: (values: MusicaInput) => Promise<void>
}

export function MusicCreateDialog({ open, onOpenChange, onSubmit }: MusicCreateDialogProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle className="text-xl">Adicionar música</DialogTitle>
          <DialogDescription>
            Ela aparecerá na lista, pronta para receber partituras.
          </DialogDescription>
        </DialogHeader>

        <CreateForm onClose={() => onOpenChange(false)} onSubmit={onSubmit} />
      </DialogContent>
    </Dialog>
  )
}
