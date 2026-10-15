import type { Member, Sexo, StatusCredencialAcesso } from '#/api/member';
import { Button } from '#/components/ui/button';
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger
} from '#/components/ui/dropdown-menu';
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow
} from '#/components/ui/table';
import { capitalizeAll, cn } from '#/lib/utils';
import { Briefcase, Ellipsis, Lock, LockOpen, Mail, MapPin, Pencil, Phone, Users, UsersRound } from 'lucide-react';
import { Badge, type VariantBadgeType } from '../../ui/badge';


/* ───────────── Status de acesso ───────────── */

const STATUS: Record<string, { label: string; variant: VariantBadgeType }> = {
    ATIVO: { label: 'Ativo', variant: 'success' },
    BLOQUEADO: { label: 'Bloqueado', variant: 'danger' },
    SUSPENSO: { label: 'Suspenso', variant: 'warning' },
    CONVIDADO: { label: 'Convidado', variant: 'info' },
    SEM_ACESSO: { label: 'Sem acesso', variant: 'muted' }
};

const SEXO: Record<Sexo ,{ label: string }> = {
    MASCULINO: { label: "masculino"},
    FEMININO: { label: "feminino" },
    NAO_INFORMADO: { label: "não informado"}
}

function StatusBadge({ value }: { value: StatusCredencialAcesso }) {
    const key = String(value);
    const status = STATUS[key] ?? { label: key.charAt(0) + key.slice(1).toLowerCase().replaceAll('_', ' '), variant: 'muted' as const };

    return (
        <Badge variant={status.variant}>
            <span className="size-1.5 rounded-full bg-current" aria-hidden="true" />
            {status.label}
        </Badge>
    );
}

/* ───────────── Pequenos utilitários de apresentação ───────────── */

function formatPhone(value?: string | null): string {
    const raw = String(value ?? '');
    if (raw.includes('(') || raw.includes('-')) return raw;
    const digits = raw.replace(/\D/g, '');
    if (digits.length === 11) return digits.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1) $2-$3');
    if (digits.length === 10) return digits.replace(/^(\d{2})(\d{4})(\d{4})$/, '($1) $2-$3');
    return raw;
}

function initials(name: string): string {
    const parts = name.trim().split(/\s+/).filter(Boolean);
    if (parts.length === 0) return '?';
    const first = parts[0][0] ?? '';
    const last = parts.length > 1 ? parts[parts.length - 1][0] : '';
    return (first + last).toUpperCase();
}

function MemberAvatar({ name, minor, avatar }: { name: string; minor: boolean, avatar?: string }) {
    return (
        <span
           aria-hidden="true"
            className={cn(
                'inline-flex size-10 shrink-0 items-center justify-center rounded-full text-sm font-bold overflow-hidden',
                minor ? 'bg-warning/15 text-warning' : 'bg-primary/10 text-primary'
            )}

        >
            {avatar ? <img src={avatar} alt="Foto de perfil do membro"  className="w-full h-full object-cover" /> :
                initials(name)}
        </span>
    );
}

function ContactLine({ icon: Icon, label, children }: { icon: typeof Mail; label: string; children: React.ReactNode }) {
    return (
        <div className="flex min-w-0 items-center gap-2">
            <Icon className="size-3.5 shrink-0 text-muted-foreground" aria-hidden="true" />
            <span className="sr-only">{label}:</span>
            <span className="min-w-0 truncate text-sm font-medium text-foreground">{children}</span>
        </div>
    );
}

/* ───────────── Tabela ───────────── */

export interface MemberTableProps {
    membros: Member[];
    onManageRoles?: (member: Member) => void;
    onEdit?: (member: Member) => void;
    onBlock?: (member: Member) => void;
    onUnblock?: (member: Member) => void;
    onManageResponsaveis?: (member: Member) => void;
}

export function MemberTable({ membros, onManageRoles, onEdit, onBlock, onUnblock, onManageResponsaveis }: MemberTableProps) {

    



    if (membros.length === 0) {
        return (
            <div className="flex flex-col items-center rounded-xl border border-dashed bg-card px-6 py-16 text-center">
                <span className="mb-4 inline-flex size-14 items-center justify-center rounded-full bg-primary/10 text-primary">
                    <Users className="size-6" aria-hidden="true" />
                </span>
                <h3 className="font-display text-lg font-semibold text-foreground">Nenhum membro encontrado</h3>
                <p className="mt-1 max-w-sm text-sm text-muted-foreground">
                    Cadastre o primeiro membro ou ajuste a busca e os filtros para ver resultados.
                </p>
            </div>
        );
    }

    return (
        <div className="w-full max-w-full overflow-hidden rounded-xl border bg-card shadow-xs">

            <Table>
                <TableHeader className="bg-muted/60">
                    <TableRow className="hover:bg-transparent">
                        {['Membro', 'Meios de contato', 'Endereço'].map((title) => (
                            <TableHead
                                key={title}
                                className="h-11 px-4 text-xs font-semibold uppercase tracking-wider text-muted-foreground"
                            >
                                {title}
                            </TableHead>
                        ))}
                        <TableHead className="h-11 w-16 px-4 text-right text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                            Ações
                        </TableHead>
                    </TableRow>
                </TableHeader>

                <TableBody>
                    {membros.map((member, index) => {
                        const nome = capitalizeAll(String(member.nome).toLocaleLowerCase());
                        const minor = Number(member.idade) < 18;
                        const status = String(member.statusCredencialAcesso);
                        const hasCredential = status === 'ATIVO' || status === 'SUSPENSO' || status === 'BLOQUEADO';
                        const telefone = member.telefone ? formatPhone(member.telefone) : '-';

                        return (
                            <TableRow key={member.id ?? index} className="align-top">
                                <TableCell className="align-top px-4 py-4">
                                    <div className="flex items-start gap-3">
                                        <MemberAvatar name={nome} minor={minor} avatar={member.avatarUrl} />
                                        <div className="flex min-w-0 flex-col">
                                            <span className="text-[0.95rem] font-semibold leading-tight text-foreground">{nome}</span>
                                            <span className="mt-0.5 text-xs text-muted-foreground">
                                                {member.idade} anos • sexo {SEXO[member.sexo].label}
                                            </span>
                                            <div className="mt-2 flex flex-wrap items-center gap-1.5">
                                                <StatusBadge value={member.statusCredencialAcesso} />
                                                {minor && <Badge variant="warning">Menor</Badge>}
                                            </div>
                                        </div>
                                    </div>
                                </TableCell>

                                <TableCell className="align-top max-w-72 px-4 py-4">
                                    <div className="flex flex-col gap-1.5">
                                        <ContactLine icon={Mail} label="E-mail">
                                            {member.email || '-'}
                                        </ContactLine>
                                        <ContactLine icon={Phone} label="Telefone">
                                            <span className="tabular-nums">{telefone}</span>
                                        </ContactLine>
                                    </div>
                                </TableCell>

                                <TableCell className="align-top max-w-72 px-4 py-4">
                                    <div className="flex items-start gap-2">
                                        <MapPin className="mt-0.5 size-3.5 shrink-0 text-muted-foreground" aria-hidden="true" />
                                        {member.endereco ? (
                                            <span className="line-clamp-2 text-sm leading-relaxed text-foreground/80">
                                                {capitalizeAll(member.endereco)}
                                            </span>
                                        ) : (
                                            <span className="text-sm italic text-muted-foreground">Endereço não informado</span>
                                        )}
                                    </div>
                                </TableCell>

                                <TableCell className="px-4 py-3 text-right">
                                    <DropdownMenu>
                                        <DropdownMenuTrigger asChild>
                                            <Button variant="ghost" size="icon" aria-label={`Ações de ${nome}`} className="text-muted-foreground">
                                                <Ellipsis className="size-5" />
                                            </Button>
                                        </DropdownMenuTrigger>
                                        <DropdownMenuContent align="end" className="w-60">
                                            <DropdownMenuItem onSelect={() => onManageRoles?.(member)}>
                                                <Briefcase /> Gerenciar vínculos e funções
                                            </DropdownMenuItem>
                                            <DropdownMenuItem onSelect={() => onEdit?.(member)}>
                                                <Pencil /> Editar
                                            </DropdownMenuItem>
                                            <DropdownMenuItem onSelect={() => onManageResponsaveis?.(member)}>
                                                <UsersRound /> Gerenciar responsáveis
                                            </DropdownMenuItem>
                                            {hasCredential && <DropdownMenuSeparator />}
                                            {hasCredential && status !== 'BLOQUEADO' && (
                                                <DropdownMenuItem variant="destructive" onSelect={() => onBlock?.(member)}>
                                                    <Lock /> Bloquear membro
                                                </DropdownMenuItem>
                                            )}
                                            {status === 'BLOQUEADO' && (
                                                <DropdownMenuItem onSelect={() => onUnblock?.(member)}>
                                                    <LockOpen /> Desbloquear membro
                                                </DropdownMenuItem>
                                            )}
                                        </DropdownMenuContent>
                                    </DropdownMenu>
                                </TableCell>
                            </TableRow>
                        );
                    })}
                </TableBody>
            </Table>
        </div>
    );
}