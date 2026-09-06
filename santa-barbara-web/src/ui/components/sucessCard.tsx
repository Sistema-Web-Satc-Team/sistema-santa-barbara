import "../styles/sucessCard.css";
import type { HTMLAttributes } from "react";

export interface SuccessCardProps extends HTMLAttributes<HTMLDivElement> {
    successMessage: string;
}

export function SuccessCard({ successMessage, className = "", ...props }: SuccessCardProps) {
    if (!successMessage) return null;

    return (
        <div className={`success-card ${className}`.trim()} {...props}>
            {successMessage}
        </div>
    );
}