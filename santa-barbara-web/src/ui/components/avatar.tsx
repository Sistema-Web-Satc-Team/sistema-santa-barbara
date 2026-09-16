import { User } from "lucide-react";
import "../styles/avatar.css"; 

interface AvatarProps {
    src?: string;
    alt?: string;
    className?: string;
}

export function Avatar({ src, alt = "Foto de perfil", className = "" }: AvatarProps) {
    return (
        <div className={`avatar-container ${className}`.trim()}>
            {src ? (
                <img src={src} alt={alt} className="avatar-image" />
            ) : (
                <User className="avatar-fallback-icon" />
            )}
        </div>
    );
}