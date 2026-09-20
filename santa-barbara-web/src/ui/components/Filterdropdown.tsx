import { useState, useRef, useEffect } from "react";
import { Filter } from "lucide-react";
import { Button } from "@/ui/components/button";

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
        <div className="relative" ref={dropdownRef}>
            <Button
                variant="outline"
                className="flex items-center gap-2 bg-(--surface-color) border-(--light-neutral-color)"
                onClick={() => setIsOpen(!isOpen)}
            >
                {label} {selected ? `(${selected})` : ""} <Filter className="w-4 h-4" />
            </Button>
            
            {isOpen && (
                <div className="absolute top-full mt-2 left-0 w-48 bg-(--surface-color) border border-(--light-neutral-color) rounded-md shadow-lg z-50 py-1 text-left">
                    <div className="px-4 py-2 text-xs font-semibold text-(--neutral-color) border-b border-(--light-neutral-color)">
                        Filtrar por papel
                    </div>
                    {["", ...options].map((opcao) => (
                        <button
                            key={opcao || "todos"}
                            onClick={() => handleSelect(opcao)}
                            className={`w-full px-4 py-2 text-sm text-left transition-colors ${
                                selected === opcao 
                                    ? "bg-(--strong-surface-color) font-semibold text-(--strong-foreground-color)" 
                                    : "text-(--neutral-color) hover:bg-(--strong-surface-color)"
                            }`}
                        >
                            {opcao ? opcao.charAt(0) + opcao.slice(1).toLowerCase() : "Todos os Papéis"}
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
}