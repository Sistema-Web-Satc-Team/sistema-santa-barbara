import "@/ui/styles/sucess-card.css";
import type { HTMLAttributes } from "react";

export interface SuccessCardProps extends HTMLAttributes<HTMLDivElement> {
    message: string;
}

export function SuccessCard({ message, className = "", ...props }: SuccessCardProps) {
    if (!message) return <></>;

    return (
        <div className={`success-card ${className}`.trim()} {...props}>
            {message}
        </div>
    );
}