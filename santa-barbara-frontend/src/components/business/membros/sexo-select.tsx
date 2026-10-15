import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "#/components/ui/select";

export interface SexoSelectProps {
    value: string;
    id: string;
    handleBlur: () => void;
    handleChange: (value: any) => void;
}


export function SexoSelect({ ...props }: SexoSelectProps) {

    return <Select
        value={props.value} 
        onValueChange={(value) => props.handleChange(value)}
    >
        <SelectTrigger id={props.id} onBlur={props.handleBlur}>
            <SelectValue placeholder="Selecione..." />
        </SelectTrigger>

        <SelectContent>
          <SelectItem value="MASCULINO">Masculino</SelectItem>
          <SelectItem value="FEMININO">Feminino</SelectItem>
          <SelectItem value="NAO_INFORMADO">Não Informado</SelectItem>
        </SelectContent>

    </Select>
}