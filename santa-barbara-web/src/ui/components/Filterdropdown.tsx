import { useState, useRef, useEffect } from "react";
import { Filter } from "lucide-react";
import { Button } from "@/ui/components/button";
import "@/ui/styles/filter-dropdown.css";

interface FilterDropdownProps {
    label: string;
    options: string[];
    selected: string;
    onSelect: (value: string) => void;
}

export function FilterDropdown({ label, options, selected, onSelect }: FilterDropdownProps) {
    const [isOpen, setIsOpen] = useState(false);
    const dropdownRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const handleSelect = (value: string) => {
        onSelect(value);
        setIsOpen(false);
    };

    return (
        <div className="filter-dropdown" ref={dropdownRef}>
            <Button
                variant="outline"
                className="filter-dropdown__trigger"
                onClick={() => setIsOpen(!isOpen)}
            >
                <Filter className="filter-dropdown__icon" size={16} /> {label} {selected ? `(${selected})` : ""}
            </Button>
            
            {isOpen && (
                <div className="filter-dropdown__menu">
                    <div className="filter-dropdown__heading">
                        Filtrar por papel
                    </div>
                    {["", ...options].map((opcao) => (
                        <button
                            key={opcao || "todos"}
                            onClick={() => handleSelect(opcao)}
                            className={`filter-dropdown__option ${selected === opcao ? "filter-dropdown__option--selected" : ""}`}
                        >
                            {opcao ? opcao.charAt(0) + opcao.slice(1).toLowerCase() : "Todos os Papéis"}
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
}