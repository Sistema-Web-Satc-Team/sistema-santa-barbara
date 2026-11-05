import { useAuth } from "#/hooks/use-auth";
import { capitalizeAll } from "#/lib/utils";
import { ChevronsUpDown } from "lucide-react";
import { Avatar, AvatarFallback, AvatarImage } from "../ui/avatar";
import { initials } from "./membros/members";

interface UserProfileProps {
    className?: string;
    withChevronsUpDown?: boolean;
    avatarSide?: 'Left' | 'Right';
    size?: 'Default' | 'Large';
}

export function UserProfile({ className = "", withChevronsUpDown = true, avatarSide = 'Left', size = 'Default'}: UserProfileProps) {
    const { state } = useAuth();

    return (
        <div className={`
            flex items-center gap-3 px-4 py-2
            h-fit self-center
            ${avatarSide == 'Left' ? 'text-left' : 'text-right' }
            ${size == 'Default' ? 'text-sm' : ''}
            ${size == 'Large' ? 'text-md' : ''}
            rounded-lg cursor-pointer 
            transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground    
             ${className}
        `}
        >
            { avatarSide == 'Right' && <div className="grid flex-1 text-right w-fit">
                <span className={`
                    ${size == 'Default' ? 'text-sm' : ''}
                    ${size == 'Large' ? 'text-md' : ''}
                    text-foreground font-bold whitespace-nowrap
                `}>
                    {state.profile && state.profile.nome.length > 0 ? capitalizeAll(state.profile.nome) : "Unknown"}
                </span>
                <span className={`
                    ${size == 'Default' ? 'text-xs' : ''}
                    ${size == 'Large' ? 'text-sm' : ''} 
                    text-muted-foreground whitespace-nowrap
                `}>
                    {state.profile && state.profile.email.length > 0 ? state.profile.email : "unknown@example.com"}
                </span>
            </div> }
            <Avatar size={selectSizeAvatar(size)}>
                <AvatarImage src={state.profile?.avatarURL ?? ""} alt={capitalizeAll(state.profile?.nome ?? '')} />
                <AvatarFallback>
                    {state.profile && initials(state.profile.nome)}
                </AvatarFallback>
            </Avatar>
            { avatarSide == 'Left' && <div className="grid flex-1 text-left w-fit">
                <span className="text-sm text-foreground font-bold whitespace-nowrap">{state.profile && state.profile.nome.length > 0 ? capitalizeAll(state.profile.nome) : "Unknown"}</span>
                <span className="text-xs text-muted-foreground whitespace-nowrap">{state.profile && state.profile.email.length > 0 ? state.profile.email : "unknown@example.com"}</span>
            </div> }
            { withChevronsUpDown && <ChevronsUpDown className="w-4 h-4 text-sidebar-foreground" />}
        </div>
    )
}

function selectSizeAvatar(size: 'Default' | 'Large'): "default" | "sm" | "lg" | "xl" {

    if (size == 'Default') return "lg";

    if (size == 'Large') return "xl";

    return "default"
}