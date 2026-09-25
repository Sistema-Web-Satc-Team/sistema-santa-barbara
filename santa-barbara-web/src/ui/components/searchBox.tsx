import { Input } from "@/ui/components/input";
import "@/ui/styles/search-box.css";
import { Search } from "lucide-react";

interface SearchBoxProps {
    value: string;
    onChange: (value: string, e?: React.ChangeEvent<HTMLInputElement, Element>) => void;
    placeholder?: string;
}

export function SearchBox({ value, onChange, placeholder = "Placeholder" }: SearchBoxProps) {
    return (
        <div className="search-box">
            <Input
                placeholder={placeholder}
                className="search-box__input"
                value={value}
                onChange={(e) => onChange(e.target.value, e)}
            />
            <Search className="search-box__icon" size={20} />
        </div>
    );
}