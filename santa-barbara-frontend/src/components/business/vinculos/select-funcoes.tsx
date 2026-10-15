import { fetchRoles } from '#/api/funcoes'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '#/components/ui/select'
import { useQuery } from '@tanstack/react-query'

interface Props {
  id?: string
  value: string
  onChangeValue: (code: string) => void
  onBlur?: () => void
  isInvalid?: boolean
  excludeCodes?: string[]
}

export function SelectFuncoes({ id, value, onChangeValue, onBlur, isInvalid, excludeCodes = [] }: Props) {
  const { data: funcoes = [], isLoading } = useQuery({
    queryKey: ['funcoes'],
    queryFn: fetchRoles,
    staleTime: 1000 * 60 * 30,
  })

  return (
    <Select value={value} onValueChange={onChangeValue} disabled={isLoading}>
      <SelectTrigger id={id} onBlur={onBlur} aria-invalid={isInvalid}>
        <SelectValue placeholder={isLoading ? 'Carregando...' : 'Selecione a função'} />
      </SelectTrigger>
      <SelectContent>
        {funcoes
          .filter((f) => !excludeCodes.includes(f.code))
          .map((f) => (
            <SelectItem key={f.code} value={f.code}>
              {f.nome}
            </SelectItem>
          ))}
      </SelectContent>
    </Select>
  )
}