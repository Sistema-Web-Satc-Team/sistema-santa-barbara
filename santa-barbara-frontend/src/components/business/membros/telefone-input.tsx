import { Input } from "@/components/ui/input";

export interface TelefoneInputProps extends React.ComponentProps<"input"> {
  value: string
  onChangeValue: (value: string) => void
}

function formatPhone(value: string) {
  const digits = value.replace(/\D/g, '').slice(0, 11);
  if (digits.length <= 2) return digits;
  if (digits.length <= 7) return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
  return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7)}`;
}

export function TelefoneInput({ value, onChangeValue, ...props }: TelefoneInputProps) {
  return (
    <Input
      {...props}
      type="text"
      inputMode="tel"
      value={formatPhone(value)}
      onChange={(e) => {
        const rawDigits = e.target.value.replace(/\D/g, '');
        onChangeValue(rawDigits); 
      }}
      placeholder="(00) 00000-0000"
    />
  )
}