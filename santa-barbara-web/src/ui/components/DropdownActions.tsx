import { useState, useRef, useEffect } from "react";
import type { ReactNode } from "react";
import { MoreVertical } from "lucide-react";
import "@/ui/styles/dropdown-actions.css";


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
        <div className="dropdown-actions" ref={menuRef}>
            <button 
                onClick={() => setIsOpen(!isOpen)} 
                className="dropdown-actions__trigger"
            >
                <MoreVertical size={20} />
            </button>

            {isOpen && (
                <div className="dropdown-actions__menu">
                    {options.map((option, index) => (
                        <button 
                            key={index}
                            onClick={() => {
                                option.onClick();
                                setIsOpen(false); 
                            }}
                            className={`dropdown-actions__option ${option.isDanger ? "dropdown-actions__option--danger" : ""}`}
                        >
                            {option.icon} {option.label}
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
}