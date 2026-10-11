import { Badge } from '#/components/ui/badge';
import { Card, CardContent } from '#/components/ui/card';

const roleDescriptions: Record<string, string> = {
  ADMIN: 'Acesso total ao sistema',
  MAESTRO: 'Dirige e coordena os grupos musicais',
  ALUNO: 'Acesso básico ao sistema',
};

const formatRoleName = (role: string) =>
  role.charAt(0).toUpperCase() + role.slice(1).toLowerCase();

interface RoleCardProps {
  role: string;
  memberCount?: number;
}

export function RoleCard({ role, memberCount }: RoleCardProps) {
  return (
    <Card>
      <CardContent className="space-y-3">
        <div className="flex items-center gap-3">
          <Badge className="px-3 py-1 text-sm">{formatRoleName(role)}</Badge>
          {memberCount !== undefined && (
            <span className="text-sm text-muted-foreground">{memberCount}</span>
          )}
        </div>
        <p className="text-sm text-muted-foreground">
          {roleDescriptions[role.toUpperCase()] ?? 'Sem descrição'}
        </p>
      </CardContent>
    </Card>
  );
}