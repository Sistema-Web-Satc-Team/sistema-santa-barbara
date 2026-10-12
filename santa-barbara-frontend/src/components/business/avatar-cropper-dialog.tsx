import { Button } from '#/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '#/components/ui/dialog'
import { Spinner } from '#/components/ui/spinner'
import { getCroppedBlob, type PreparedImage } from '#/lib/image-crop'
import { useEffect, useState } from 'react'
import Cropper from 'react-easy-crop'
import type { Area, Point } from 'react-easy-crop'
import { toast } from 'sonner'

interface AvatarCropperDialogProps {
  /** null = diálogo fechado. */
  image: PreparedImage | null
  isSaving: boolean
  onCancel: () => void
  onConfirm: (photo: Blob) => void
}

interface CropperBodyProps {
  image: PreparedImage
  isSaving: boolean
  onCancel: () => void
  onConfirm: (photo: Blob) => void
}


function CropperBody({ image, isSaving, onCancel, onConfirm }: CropperBodyProps) {
  const [crop, setCrop] = useState<Point>({ x: 0, y: 0 })
  const [zoom, setZoom] = useState(1)
  const [area, setArea] = useState<Area | null>(null)
  const [ready, setReady] = useState(false)
  const [isCutting, setIsCutting] = useState(false)

  useEffect(() => {
    const timer = setTimeout(() => setReady(true), 250)
    return () => clearTimeout(timer)
  }, [])

  const busy = isSaving || isCutting

  async function handleConfirm() {
    if (!area) return
    setIsCutting(true)
    try {
      onConfirm(await getCroppedBlob(image.src, area))
    } catch {
      toast.error('Não foi possível recortar a foto. Tente outra imagem.')
    } finally {
      setIsCutting(false)
    }
  }

  return (
    <>
      <DialogHeader>
        <DialogTitle>Ajustar foto de perfil</DialogTitle>
        <DialogDescription>
          Arraste a foto e use o zoom para deixar o rosto dentro do círculo.
        </DialogDescription>
      </DialogHeader>

      <div className="relative h-72 w-full overflow-hidden rounded-md bg-muted sm:h-80">
        {ready ? (
          <Cropper
            image={image.src}
            crop={crop}
            zoom={zoom}
            minZoom={1}
            maxZoom={image.maxZoom}
            aspect={1}
            cropShape="round"
            showGrid={false}
            onCropChange={setCrop}
            onZoomChange={setZoom}
            onCropComplete={(_, pixels) => setArea(pixels)}
          />
        ) : (
          <div className="absolute inset-0 flex items-center justify-center">
            <Spinner />
          </div>
        )}
      </div>

      <div className="flex items-center gap-3">
        <label htmlFor="avatar-zoom" className="text-sm text-muted-foreground">
          Zoom
        </label>
        <input
          id="avatar-zoom"
          type="range"
          min={1}
          max={image.maxZoom}
          step={0.01}
          value={zoom}
          disabled={image.maxZoom <= 1 || busy}
          onChange={(event) => setZoom(Number(event.target.value))}
          className="w-full accent-primary"
        />
      </div>

      <DialogFooter>
        <Button type="button" variant="outline" onClick={onCancel} disabled={busy}>
          Cancelar
        </Button>
        <Button
          type="button"
          onClick={() => void handleConfirm()}
          disabled={!area || busy}
        >
          {busy && <Spinner />}
          Salvar foto
        </Button>
      </DialogFooter>
    </>
  )
}

export function AvatarCropperDialog({
  image,
  isSaving,
  onCancel,
  onConfirm,
}: AvatarCropperDialogProps) {
  return (
    <Dialog
      open={image !== null}
      onOpenChange={(open) => {
        if (!open && !isSaving) onCancel()
      }}
    >
      <DialogContent className="sm:max-w-md" showCloseButton={!isSaving}>
        {image && (
          <CropperBody
            image={image}
            isSaving={isSaving}
            onCancel={onCancel}
            onConfirm={onConfirm}
          />
        )}
      </DialogContent>
    </Dialog>
  )
}
