import { normalizeInstrumentType } from '#/api/instrumento'
import type { InstrumentType } from '#/api/instrumento'
import { DrumIcon, GuitarIcon, MusicIcon, PianoIcon, WindIcon } from 'lucide-react'
import type { LucideIcon } from 'lucide-react'

const icons: Record<InstrumentType, LucideIcon> = {
  sopro: WindIcon,
  teclas: PianoIcon,
  cordas: GuitarIcon,
  percussao: DrumIcon,
}

interface InstrumentIconProps {
  tipo: string
  className?: string
}

/** Ícone da categoria do instrumento. Categoria desconhecida usa uma nota musical. */
export function InstrumentIcon({ tipo, className }: InstrumentIconProps) {
  const type = normalizeInstrumentType(tipo)
  const Icon = type ? icons[type] : MusicIcon

  return <Icon className={className} aria-hidden />
}
