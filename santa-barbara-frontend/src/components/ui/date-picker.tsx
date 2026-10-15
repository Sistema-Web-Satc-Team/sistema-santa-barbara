import { format, isValid, parse } from "date-fns"
import { CalendarIcon } from "lucide-react"
import React from "react"
import { Button } from "./button"
import { Calendar } from "./calendar"
import { Popover, PopoverContent, PopoverTrigger } from "./popover"

interface ZodDateValue {
  dia: string;
  mes: string;
  ano: string;
}

interface DatePickerProps {
  value: ZodDateValue | undefined;
  onChangeValue: (value: ZodDateValue | undefined) => void;
  isInvalid?: boolean;
}

export function DatePicker({ value, onChangeValue, isInvalid }: DatePickerProps) {
  
  const jsDate = React.useMemo(() => {
    if (!value?.ano || !value?.mes || !value?.dia) return undefined;
    const dateString = `${value.dia.padStart(2, '0')}/${value.mes.padStart(2, '0')}/${value.ano}`;
    const parsedDate = parse(dateString, "dd/MM/yyyy", new Date());
    return isValid(parsedDate) ? parsedDate : undefined;
  }, [value]);


  const handleSelect = (selectedDate: Date | undefined) => {
    if (!selectedDate) {
      onChangeValue(undefined);
      return;
    }
    
    onChangeValue({
      dia: format(selectedDate, "dd"),
      mes: format(selectedDate, "MM"),
      ano: format(selectedDate, "yyyy")
    });
  };

  return (
    <Popover>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          data-empty={!jsDate}
          data-invalid={isInvalid}
          className="justify-start text-left font-normal data-[empty=true]:text-muted-foreground data-[invalid=true]:border-destructive w-[280px]"
        >
          <CalendarIcon className="mr-2 h-4 w-4" />
          {jsDate ? format(jsDate, "dd/MM/yyyy") : <span>Selecione uma data</span>}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-auto p-0">
        <Calendar 
          mode="single" 
          selected={jsDate} 
          onSelect={handleSelect} 
        />
      </PopoverContent>
    </Popover>
  )
}
