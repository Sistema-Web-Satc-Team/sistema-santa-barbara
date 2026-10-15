// components/.../responsavel-modo-radio.tsx
import { Label } from '#/components/ui/label'
import { RadioGroup, RadioGroupItem } from '#/components/ui/radio-group'

export type ResponsavelModo = 'existente' | 'novo'

interface Props {
  value: ResponsavelModo
  onChangeValue: (value: ResponsavelModo) => void
}

export function ResponsavelModoRadio({ value, onChangeValue }: Props) {
  return (
    <RadioGroup
      value={value}
      onValueChange={(v) => onChangeValue(v as ResponsavelModo)}
      className="flex gap-4"
    >
      <div className="flex items-center gap-2">
        <RadioGroupItem value="existente" id="r-existente" />
        <Label htmlFor="r-existente" className="cursor-pointer font-normal">
          Buscar existente
        </Label>
      </div>
      <div className="flex items-center gap-2">
        <RadioGroupItem value="novo" id="r-novo" />
        <Label htmlFor="r-novo" className="cursor-pointer font-normal">
          Cadastrar novo
        </Label>
      </div>
    </RadioGroup>
  )
}