import { User } from "lucide-react";
import "../styles/avatar.css";

interface AvatarProps {
    src?: string;
    alt?: string;
    className?: string;
    nome?: string;
}

export function Avatar({ src, alt = "Foto de perfil", className = "", nome }: AvatarProps) {
    const temNomeValido = nome && nome.trim().length > 0;

    return (
        <div className={`avatar-container ${className}`.trim()}>
            {src ? (
                <img src={src} alt={alt} className="avatar-image" />
            ) : temNomeValido ? (
                <span className="avatar-initial">{nome.trim()[0].toUpperCase()}</span>
            ) : (
                <User className="avatar-fallback-icon" />
            )}
        </div>
    );
}