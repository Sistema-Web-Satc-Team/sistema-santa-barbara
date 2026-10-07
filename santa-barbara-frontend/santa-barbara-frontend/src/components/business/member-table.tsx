import type { Member } from '#/api/member';
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow
} from '#/components/ui/table';
import { capitalize } from '#/lib/utils';
import { Badge } from '../ui/badge';

const memberHeaderLabels: Record<keyof Member, string> = {
    id: "ID",
    nome: "Nome",
    nomeDeUsuario: "Nome de Usuário",
    telefone: "Telefone",
    endereco: "Endereço",
    email: "E-mail",
    papeis: "Papéis",
    dataNascimento: "Data de Nascimento",
    idade: "Idade",
    status: "Status"
};

const memberViews: Partial<Record<keyof Member, (value: any, item: Member) => React.ReactNode>> = {
    papeis: (values) => {
        return ( values.map((value: string) => (<Badge variant="outline">{capitalize(value.toLocaleLowerCase().replaceAll('_', ' '))}</Badge>)) )
    },
    status: (value) => (
        <Badge variant={value === 'INATIVO' ? 'destructive' : 'default'}>
            {capitalize(String(value).toLocaleLowerCase())}
        </Badge>
    ),
    telefone: (value: any) => {
        const stringValue = String(value || '');
        const apenasNumeros = stringValue.replace(/\D/g, '');

        if (stringValue.includes('(') || stringValue.includes('-')) {
            return <span>{stringValue}</span>;
        }

        if (apenasNumeros.length === 11) {
            const celularFormatado = apenasNumeros.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1) $2-$3');
            return <span>{celularFormatado}</span>;
        }

        if (apenasNumeros.length === 10) {
            const fixoFormatado = apenasNumeros.replace(/^(\d{2})(\d{4})(\d{4})$/, '($1) $2-$3');
            return <span>{fixoFormatado}</span>;
        }

        return <span>{stringValue}</span>;
    }

    
};

interface MemberTableProps {
    membros: Member[];
    columnVisibility: Record<keyof Member, boolean>;
}

export function MemberTable({ membros, columnVisibility }: MemberTableProps) {
    const headers = Object.keys(memberHeaderLabels) as (keyof Member)[];

    return (
        <div className='w-full max-w-full overflow-x-auto'>
            <Table>
                <TableHeader>
                    <TableRow>
                        {headers.map((header) => (
                            columnVisibility[header] && (
                                <TableHead key={String(header)}>
                                    {memberHeaderLabels[header] ?? String(header)}
                                </TableHead>
                            )
                        ))}
                    </TableRow>
                </TableHeader>
                <TableBody>
                    {membros.map((member, index) => (
                        <TableRow key={member.id ?? index}>
                            {headers.map((header) => {
                                if (!columnVisibility[header]) return null;

                                const rawValue = member[header];
                                const view = memberViews[header];

                                return (
                                    <TableCell key={String(header)}>
                                        {view 
                                            ? view(rawValue, member) 
                                            : String(rawValue ?? '')}
                                    </TableCell>
                                );
                            })}
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </div>
    );
}