import type { Role } from "#/api/funcoes";
import { Card, CardContent, CardHeader } from "#/components/ui/card";
import { useNavigate } from "@tanstack/react-router";


interface HubCardProps {
    role: Role;
    route: string;
    callback: () => void;
}

export function HubCard({ role, route, callback }: HubCardProps) {
    const navigate = useNavigate();

    return <>

        <Card 
            onClick={async () => {
                callback();
                await navigate({ to: route })
            }}
        
            className="hover:bg-accent-foreground  hover:text-primary-foreground cursor-pointer transition"
        >
            <CardHeader>
                <h1 className="text-2xl font-bold">{role.nome}</h1>
            </CardHeader>
            <CardContent>
                <span>{role.descricao}</span>
            </CardContent>
        </Card>
    
    </>
}