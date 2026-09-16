import { useState, useRef, useEffect } from "react";
import type { ReactNode } from "react";
import { MoreVertical } from "lucide-react";


export interface DropdownOption {
    label: string;
    icon?: ReactNode;
    onClick: () => void;
    isDanger?: boolean; 
}

interface DropdownActionsProps {
    options: DropdownOption[];
}

export function DropdownActions({ options }: DropdownActionsProps) {
    const [isOpen, setIsOpen] = useState(false);
    const menuRef = useRef<HTMLDivElement>(null);


    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
                setIsOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    return (
        <div className="relative flex justify-center" ref={menuRef}>
            <button 
                onClick={() => setIsOpen(!isOpen)} 
                className="p-1 hover:bg-(--strong-surface-color) rounded text-(--neutral-color) transition-colors"
            >
                <MoreVertical className="w-5 h-5" />
            </button>

            {isOpen && (
                <div className="absolute right-0 mt-8 w-44 bg-(--surface-color) border border-(--light-neutral-color) rounded-md shadow-lg z-50 py-1 text-left">
                    {options.map((option, index) => (
                        <button 
                            key={index}
                            onClick={() => {
                                option.onClick();
                                setIsOpen(false); 
                            }}
                            className={`w-full px-4 py-2 text-xs flex items-center gap-2 hover:bg-(--strong-surface-color) transition-colors
                                ${option.isDanger ? "text-(--error-color)" : "text-(--neutral-color)"}
                            `}
                        >
                            {option.icon} {option.label}
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
}