import "@/ui/styles/badge.css";

interface BadgeProps {
    children: string;
    variant?: "active" | "inactive";
}

export function Badge({ children, variant = "active" }: BadgeProps) {
    return <span className={`badge badge--${variant}`}>{children}</span>;
}
