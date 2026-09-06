import "../styles/card.css";;
import { type ReactNode, type HTMLAttributes } from "react";

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