import "@/ui/styles/error-card.css";
import type { HTMLAttributes } from "react";

export interface ErrorCardProps extends HTMLAttributes<HTMLDivElement> {
    message: string;
}

export function ErrorCard({ message, className = "", ...props }: ErrorCardProps) {
    if (!message) return <></>;

    return (
        <div className={`error-card ${className}`.trim()} {...props}>
            {message}
        </div>
    );
}