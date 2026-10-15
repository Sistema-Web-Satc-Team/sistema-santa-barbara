import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '#/components/ui/select'

interface Props {
  id?: string
  value: string
  onChangeValue: (value: string) => void
  onBlur?: () => void
  isInvalid?: boolean
}

export function ParentescoSelect({ id, value, onChangeValue, onBlur, isInvalid }: Props) {
  return (
    <Select value={value} onValueChange={onChangeValue}>
      <SelectTrigger id={id} onBlur={onBlur} aria-invalid={isInvalid}>
        <SelectValue placeholder="Selecione o parentesco" />
      </SelectTrigger>
      <SelectContent>
        <SelectItem value="PROGENITORES">Pai/Mãe</SelectItem>
        <SelectItem value="AVO">Avô/Avó</SelectItem>
        <SelectItem value="TIO">Tio/Tia</SelectItem>
        <SelectItem value="PADRINHO">Padrinho/Madrinha</SelectItem>
        <SelectItem value="RESPONSAVEL_LEGAL">Responsável legal</SelectItem>
      </SelectContent>
    </Select>
  )
}