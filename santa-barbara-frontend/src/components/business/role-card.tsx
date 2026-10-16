import { Badge } from '#/components/ui/badge';
import { Card, CardContent } from '#/components/ui/card';



interface RoleCardProps {
  nome: string;
  descricao?: string;
}

export function RoleCard({ nome, descricao }: RoleCardProps) {
  return (
    <Card>
      <CardContent className="space-y-2">
        <div className="flex items-center gap-2">
          <Badge className="px-2 text-xs font-semibold">{nome}</Badge>
        </div>
        <p className="text-sm text-muted-foreground">
          {descricao ?? 'Sem descrição'}
        </p>
      </CardContent>
    </Card>
  );
}