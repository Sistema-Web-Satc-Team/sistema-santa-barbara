import { AvatarCropperDialog } from '#/components/business/avatar-cropper-dialog'
import { Avatar, AvatarFallback, AvatarImage } from '#/components/ui/avatar'
import { Button } from '#/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '#/components/ui/dropdown-menu'
import { Spinner } from '#/components/ui/spinner'
import { useAuth } from '#/hooks/use-auth'
import { useCoarsePointer } from '#/hooks/use-coarse-pointer'
import { useProfilePhoto } from '#/hooks/use-profile-photo'
import type { PreparedImage } from '#/lib/image-crop'
import { prepareImage } from '#/lib/image-crop'
import { capitalizeAll } from '#/lib/utils'
import { CameraIcon, ImageIcon, Trash2Icon } from 'lucide-react'
import type { ChangeEvent } from 'react'
import { useEffect, useRef, useState } from 'react'
import { toast } from 'sonner'
import { initials } from './membros/members'

export function ProfileAvatarEditor() {
  const { state } = useAuth()
  const photoUrl = state.profile?.avatarURL ?? ''
  const displayName = capitalizeAll(state.profile?.nome ?? '');
  const isTouchDevice = useCoarsePointer()
  const { save, remove } = useProfilePhoto()

  const [image, setImage] = useState<PreparedImage | null>(null)
  const galleryInput = useRef<HTMLInputElement>(null)
  const cameraInput = useRef<HTMLInputElement>(null)

  const hasPhoto = photoUrl !== ''
  const busy = save.isPending || remove.isPending
  const pickLabel = hasPhoto ? 'Trocar foto' : 'Adicionar foto'


  useEffect(() => {
    return () => {
      if (image) URL.revokeObjectURL(image.src)
    }
  }, [image])

  async function handleFile(event: ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0]

    event.target.value = ''
    if (!file) return

    try {
      setImage(await prepareImage(file))
    } catch (error) {
      toast.error(
        error instanceof Error ? error.message : 'Não foi possível abrir a imagem.',
      )
    }
  }

  function handleConfirm(photo: Blob) {
    save.mutate({ photo }, { onSuccess: () => setImage(null) })
  }

  return (
    <div className="flex items-center gap-5">
      <Avatar className="size-20">
        {hasPhoto && (
          <AvatarImage src={photoUrl} alt={`Foto de perfil de ${displayName}`} />
        )}
        <AvatarFallback>
          {state.profile && initials(displayName)}
        </AvatarFallback>
      </Avatar>

      <div className="flex flex-wrap items-center gap-2">
        {isTouchDevice ? (
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button
                type="button"
                size="sm"
                variant={hasPhoto ? 'outline' : 'default'}
                disabled={busy}
              >
                {remove.isPending ? <Spinner /> : <CameraIcon />}
                {pickLabel}
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="start">
              <DropdownMenuItem onSelect={() => galleryInput.current?.click()}>
                <ImageIcon />
                Escolher da galeria
              </DropdownMenuItem>
              <DropdownMenuItem onSelect={() => cameraInput.current?.click()}>
                <CameraIcon />
                Tirar foto
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        ) : (
          <Button
            type="button"
            size="sm"
            variant={hasPhoto ? 'outline' : 'default'}
            disabled={busy}
            onClick={() => galleryInput.current?.click()}
          >
            <CameraIcon />
            {pickLabel}
          </Button>
        )}

        {hasPhoto && (
          <Button
            type="button"
            size="sm"
            variant="ghost"
            className="text-destructive hover:text-destructive"
            disabled={busy}
            onClick={() => remove.mutate()}
          >
            {remove.isPending ? <Spinner /> : <Trash2Icon />}
            Remover foto
          </Button>
        )}
      </div>

      <input
        ref={galleryInput}
        type="file"
        accept="image/*"
        className="hidden"
        onChange={(event) => void handleFile(event)}
      />
      <input
        ref={cameraInput}
        type="file"
        accept="image/*"
        capture="user"
        className="hidden"
        onChange={(event) => void handleFile(event)}
      />

      <AvatarCropperDialog
        image={image}
        isSaving={save.isPending}
        onCancel={() => setImage(null)}
        onConfirm={handleConfirm}
      />
    </div>
  )
}
