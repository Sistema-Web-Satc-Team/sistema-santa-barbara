import { Input } from "#/components/ui/input";

export interface DataNascimentoValue {
    dia: string;
    mes: string;
    ano: string;
}

interface DataNascimentoInputProps {
    value: DataNascimentoValue;
    onChangeValue: (value: DataNascimentoValue) => void;
    onBlur?: () => void;
    isInvalid: boolean
}

const onlyDigits = (value: string, max: number) => value.replace(/\D/g, '').slice(0, max);

export function DataNascimentoInput({
    value,
    onChangeValue,
    onBlur,
    isInvalid
}: DataNascimentoInputProps) {
    const { dia = '', mes = '', ano = '' } = value || { dia: '', mes: '', ano: '' };

    const updatePart = (part: keyof DataNascimentoValue, max: number, raw: string) => {
        onChangeValue({
            dia,
            mes,
            ano,
            [part]: onlyDigits(raw, max),
        });
    };

    return (
        <div className="flex gap-2">
            <Input
                inputMode="numeric"
                placeholder="DD"
                value={dia}
                onBlur={onBlur}
                onChange={(e) => updatePart('dia', 2, e.target.value)}
                aria-invalid={isInvalid}
                className="text-center max-w-15"
            />
            <Input
                inputMode="numeric"
                placeholder="MM"
                aria-label="Mês"
                value={mes}
                onBlur={onBlur}
                onChange={(e) => updatePart('mes', 2, e.target.value)}
                aria-invalid={isInvalid}
                className="text-center max-w-15"
            />
            <Input
                inputMode="numeric"
                placeholder="AAAA"
                aria-label="Ano"
                value={ano}
                onBlur={onBlur}
                onChange={(e) => updatePart('ano', 4, e.target.value)}
                aria-invalid={isInvalid}
                className="text-center max-w-20"
            />
        </div>
    );
}