import { Search } from "lucide-react";
import { Input } from "@/ui/components/input";

interface SearchBoxProps {
    value: string;
    onChange: (value: string) => void;
    placeholder?: string;
}

export function SearchBox({ value, onChange, placeholder = "Pesquisar..." }: SearchBoxProps) {
    return (
        <div className="relative w-72">
            <Search className="w-5 h-5 absolute left-3 top-1/2 -translate-y-1/2 text-(--light-neutral-color)" />
            <Input
                placeholder={placeholder}
                className="pl-10 w-full bg-(--surface-color) border-(--light-neutral-color)"
                value={value}
                onChange={(e) => onChange(e.target.value)}
            />
        </div>
    );
}