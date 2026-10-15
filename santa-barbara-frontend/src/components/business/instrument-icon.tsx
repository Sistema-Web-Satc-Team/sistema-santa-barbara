import { normalizeInstrumentType } from '#/api/instrumento'
import type { InstrumentType } from '#/api/instrumento'
import {
  DrumIcon,
  GuitarIcon,
  MusicIcon,
  PianoIcon,
  WindIcon,
} from 'lucide-react'
import type { LucideIcon } from 'lucide-react'

const icons: Record<InstrumentType, LucideIcon> = {
  SOPRO: WindIcon,
  TECLAS: PianoIcon,
  CORDA: GuitarIcon,
  PERCUSSAO: DrumIcon,
}

interface InstrumentIconProps {
  tipo: string
  className?: string
}

export function InstrumentIcon({ tipo, className }: InstrumentIconProps) {
  const type = normalizeInstrumentType(tipo)
  const Icon = type ? icons[type] : MusicIcon

  return <Icon className={className} aria-hidden />
}
