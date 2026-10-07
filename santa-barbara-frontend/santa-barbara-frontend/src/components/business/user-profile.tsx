import { useAuth } from "#/hooks/use-auth";
import { ChevronsUpDown } from "lucide-react";
import { Avatar, AvatarFallback, AvatarImage } from "../ui/avatar";


export function UserProfile() {
    const { data } = useAuth();

    return (
        <div className={`
            flex items-center gap-3 p-2 text-left text-sm
            rounded-lg cursor-pointer 
            transition-colors hover:bg-sidebar-accent hover:text-sidebar-accent-foreground    
        `}
        >
            <Avatar size="lg">
                <AvatarImage src={data.urlAvatar ?? ""} alt={data.nome} />
                <AvatarFallback>
                    {data.nome.length > 0 ? data.nome[0] : "UK"}
                </AvatarFallback>
            </Avatar>
            <div className="grid flex-1 text-left w-fit">
                <span className="text-sm text-foreground font-bold whitespace-nowrap">{data.nome.length > 0 ? data.nome : "Unknown"} {data.sobrenome}</span>
                <span className="text-xs text-muted-foreground whitespace-nowrap">{data.email.length > 0 ? data.email : "unknown@example.com"}</span>
            </div>
            <ChevronsUpDown className="w-4 h-4 text-sidebar-foreground" />
        </div>
    )
}