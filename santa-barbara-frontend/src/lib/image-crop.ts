import type { Area } from 'react-easy-crop'

export const AVATAR_OUTPUT_SIZE = 512


export const AVATAR_MIN_SIDE = 512

const JPEG_QUALITY = 0.9

export interface PreparedImage {
 
  src: string
 
  maxZoom: number
}

function loadImage(src: string): Promise<HTMLImageElement> {
  return new Promise((resolve, reject) => {
    const image = new Image()
    image.onload = () => resolve(image)
    image.onerror = () =>
      reject(
        new Error(
          'Não foi possível abrir esta imagem. Use um arquivo JPG, PNG ou WebP.',
        ),
      )
    image.src = src
  })
}


export async function prepareImage(file: File): Promise<PreparedImage> {
  if (!file.type.startsWith('image/')) {
    throw new Error('Selecione um arquivo de imagem.')
  }

  const src = URL.createObjectURL(file)

  try {
    const image = await loadImage(src)
    const shortestSide = Math.min(image.naturalWidth, image.naturalHeight)

    if (shortestSide < AVATAR_MIN_SIDE) {
      throw new Error(
        `A imagem é muito pequena. Use uma foto com pelo menos ${AVATAR_MIN_SIDE} × ${AVATAR_MIN_SIDE} pixels.`,
      )
    }

   
    return { src, maxZoom: Math.max(1, shortestSide / AVATAR_OUTPUT_SIZE) }
  } catch (error) {
    URL.revokeObjectURL(src)
    throw error
  }
}

export async function getCroppedBlob(src: string, area: Area): Promise<Blob> {
  const image = await loadImage(src)

  const canvas = document.createElement('canvas')
  canvas.width = AVATAR_OUTPUT_SIZE
  canvas.height = AVATAR_OUTPUT_SIZE

  const context = canvas.getContext('2d')
  if (!context) {
    throw new Error('Seu navegador não conseguiu processar a imagem.')
  }


  context.fillStyle = '#ffffff'
  context.fillRect(0, 0, AVATAR_OUTPUT_SIZE, AVATAR_OUTPUT_SIZE)
  context.imageSmoothingQuality = 'high'
  context.drawImage(
    image,
    area.x,
    area.y,
    area.width,
    area.height,
    0,
    0,
    AVATAR_OUTPUT_SIZE,
    AVATAR_OUTPUT_SIZE,
  )

  return new Promise((resolve, reject) => {
    canvas.toBlob(
      (blob) => {
        if (blob) {
          resolve(blob)
        } else {
          reject(new Error('Não foi possível gerar a imagem recortada.'))
        }
      },
      'image/jpeg',
      JPEG_QUALITY,
    )
  })
}
