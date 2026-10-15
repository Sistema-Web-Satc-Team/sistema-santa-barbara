import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '#/components/ui/select'
import { RELACOES, type RelacaoValue } from '#/lib/relacao-responsavel'

interface Props {
  id?: string
  value: string
  onChangeValue: (value: RelacaoValue) => void
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
        {RELACOES.map((r) => (
          <SelectItem key={r.value} value={r.value}>{r.label}</SelectItem>
        ))}
      </SelectContent>
    </Select>
  )
}