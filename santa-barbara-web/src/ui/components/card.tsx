import { type HTMLAttributes, type ReactNode } from "react";
import "../styles/card.css";

export interface CardProps extends HTMLAttributes<HTMLDivElement> {
    children: ReactNode;
}

export function Card({ children, className = "", ...props }: CardProps) {
    return (
        <div className={`card ${className}`.trim()} {...props}>
            {children}
        </div>
    );
}
