import { fetchRoles } from '#/api/papeis';
import { Badge } from '#/components/ui/badge';
import { Button } from '#/components/ui/button';
import {
    Command,
    CommandEmpty,
    CommandGroup,
    CommandItem,
    CommandList,
} from '#/components/ui/command';
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
} from '#/components/ui/dialog';
import { Field, FieldError, FieldLabel } from '#/components/ui/field';
import { Input } from '#/components/ui/input';
import { Popover, PopoverContent, PopoverTrigger } from '#/components/ui/popover';
import { Spinner } from '#/components/ui/spinner';
import { useForm } from '@tanstack/react-form';
import { Check, ChevronDown, X } from 'lucide-react';
import { useEffect, useState } from 'react';
import { toast } from 'sonner';
import z from 'zod';


const formSchema = z.object({
    nome: z.string().trim().min(2, 'O nome deve ter pelo menos 2 caracteres.'),
    sobrenome: z.string().trim().min(2, 'O sobrenome deve ter pelo menos 2 caracteres.'),
    email: z.string().trim().regex(/^[^\s@]+@[^\s@]+\.[^\s@]+$/, 'Insira um e-mail válido.'),
    papeis: z.array(z.string()).min(1, 'Selecione pelo menos um papel.'),
    telefone: z
        .string()
        .refine(
            (v) => v === '' || v.replace(/\D/g, '').length >= 11,
            'O telefone deve ter pelo menos 11 dígitos.',
        ),
    dataNascimento: z
        .object({ dia: z.string(), mes: z.string(), ano: z.string() })
        .refine(({ dia, mes, ano }) => {
            if (!/^\d{2}$/.test(dia) || !/^\d{2}$/.test(mes) || !/^\d{4}$/.test(ano)) return false;
            const parsed = new Date(`${ano}-${mes}-${dia}T00:00:00`);
            return !Number.isNaN(parsed.getTime()) && parsed < new Date();
        }, 'Informe uma data de nascimento válida no passado.'),
    endereco: z.string().trim().min(2, 'O endereço deve ter pelo menos 2 caracteres.'),
});

export type MemberFormValues = z.infer<typeof formSchema>;

const defaultValues: MemberFormValues = {
    nome: '',
    sobrenome: '',
    email: '',
    papeis: [],
    telefone: '',
    dataNascimento: { dia: '', mes: '', ano: '' },
    endereco: '',
};

function formatPhone(value: string) {
    const digits = value.replace(/\D/g, '').slice(0, 11);
    if (digits.length <= 2) return digits;
    if (digits.length <= 7) return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
    return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7)}`;
}

const onlyDigits = (value: string, max: number) => value.replace(/\D/g, '').slice(0, max);

interface MemberFormDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onSubmit: (values: MemberFormValues) => Promise<void>;
}

export function MemberFormDialog({ open, onOpenChange, onSubmit }: MemberFormDialogProps) {
    const [rolesOpen, setRolesOpen] = useState(false);

    // Mudar futuramente!
    const [roles, setRoles] = useState<string[]>([]);

    const form = useForm({
        defaultValues,
        validators: {
            onSubmit: formSchema,
        },
        onSubmit: async ({ value }) => {
            try {
                await onSubmit(value);
                toast.success('Membro cadastrado com sucesso.');
                form.reset();
                onOpenChange(false);
            } catch (err: any) {
                toast.error(err.message || 'Erro ao cadastrar o membro.');
            }
        },
    });

    function handleOpenChange(next: boolean) {
        if (!next) form.reset();
        onOpenChange(next);
    }

    // Mudar futuramente!
    useEffect(() => {
        fetchRoles().then(setRoles);
    }, []);

    

    return (
        <Dialog open={open} onOpenChange={handleOpenChange}>
            <DialogContent className="md:max-w-xl max-w-md max-h-[90%] overflow-y-auto">
                <DialogHeader>
                    <DialogTitle className="text-xl">Cadastrar Membro</DialogTitle>
                </DialogHeader>

                <form
                    onSubmit={(e) => {
                        e.preventDefault();
                        e.stopPropagation();
                        form.handleSubmit();
                    }}
                    className="space-y-6"
                >
                    <div className="space-y-4">
                        <p className="text-xs uppercase tracking-wide text-muted-foreground">
                            Informações básicas
                        </p>

                        <form.Field name="nome">
                            {(field) => {
                                const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0;
                                return (
                                    <Field data-invalid={isInvalid} className="text-foreground">
                                        <FieldLabel htmlFor={field.name}>Nome*</FieldLabel>
                                        <Input
                                            id={field.name}
                                            name={field.name}
                                            value={field.state.value}
                                            onBlur={field.handleBlur}
                                            onChange={(e) => field.handleChange(e.target.value)}
                                            aria-invalid={isInvalid}
                                            placeholder="Nome"
                                        />
                                        {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
                                    </Field>
                                );
                            }}
                        </form.Field>

                        <form.Field name="sobrenome">
                            {(field) => {
                                const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0;
                                return (
                                    <Field data-invalid={isInvalid} className="text-foreground">
                                        <FieldLabel htmlFor={field.name}>Sobrenome*</FieldLabel>
                                        <Input
                                            id={field.name}
                                            name={field.name}
                                            value={field.state.value}
                                            onBlur={field.handleBlur}
                                            onChange={(e) => field.handleChange(e.target.value)}
                                            aria-invalid={isInvalid}
                                            placeholder="Sobrenome"
                                        />
                                        {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
                                    </Field>
                                );
                            }}
                        </form.Field>

                        <form.Field name="email">
                            {(field) => {
                                const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0;
                                return (
                                    <Field data-invalid={isInvalid} className="text-foreground">
                                        <FieldLabel htmlFor={field.name}>Email*</FieldLabel>
                                        <Input
                                            id={field.name}
                                            name={field.name}
                                            type="email"
                                            value={field.state.value}
                                            onBlur={field.handleBlur}
                                            onChange={(e) => field.handleChange(e.target.value)}
                                            aria-invalid={isInvalid}
                                            placeholder="seu@email.com"
                                        />
                                        {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
                                    </Field>
                                );
                            }}
                        </form.Field>

                        <form.Field name="papeis">
                            {(field) => {
                                const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0;
                                const selected = field.state.value;

                                const toggle = (papel: string) =>
                                    field.handleChange(
                                        selected.includes(papel)
                                            ? selected.filter((p) => p !== papel)
                                            : [...selected, papel],
                                    );

                                return (
                                    <Field data-invalid={isInvalid} className="text-foreground">
                                        <FieldLabel htmlFor={field.name}>Papéis</FieldLabel>
                                        <Popover
                                            open={rolesOpen}
                                            onOpenChange={(next) => {
                                                setRolesOpen(next);
                                                if (!next) field.handleBlur();
                                            }}
                                        >
                                            <PopoverTrigger asChild>
                                                <div
                                                    id={field.name}
                                                    role="combobox"
                                                    aria-expanded={rolesOpen}
                                                    aria-invalid={isInvalid}
                                                    tabIndex={0}
                                                    className="flex min-h-9 w-full cursor-pointer items-center justify-between gap-2 rounded-md border bg-transparent px-2 py-1.5 text-sm shadow-xs aria-invalid:border-destructive"
                                                >
                                                    <div className="flex flex-wrap gap-1.5">
                                                        {selected.length === 0 && (
                                                            <span className="px-1 text-muted-foreground">
                                                                Selecione os papéis
                                                            </span>
                                                        )}
                                                        {selected.map((papel) => (
                                                            <Badge key={papel} className="gap-1 rounded-full pr-1.5">
                                                                {papel}
                                                                <button
                                                                    type="button"
                                                                    aria-label={`Remover ${papel}`}
                                                                    className="rounded-full opacity-80 hover:opacity-100"
                                                                    onClick={(e) => {
                                                                        e.stopPropagation();
                                                                        toggle(papel);
                                                                    }}
                                                                >
                                                                    <X className="size-3" />
                                                                </button>
                                                            </Badge>
                                                        ))}
                                                    </div>
                                                    <ChevronDown className="size-4 shrink-0 opacity-60" />
                                                </div>
                                            </PopoverTrigger>
                                            <PopoverContent
                                                align="start"
                                                className="w-(--radix-popover-trigger-width) p-0"
                                            >
                                                <Command>
                                                    <CommandList>
                                                        <CommandEmpty>Nenhum papel encontrado.</CommandEmpty>
                                                        <CommandGroup>
                                                            {roles.map((papel) => (
                                                                <CommandItem
                                                                    key={papel}
                                                                    value={papel}
                                                                    onSelect={() => toggle(papel)}
                                                                >
                                                                    <Check
                                                                        className={`mr-2 size-4 ${
                                                                            selected.includes(papel)
                                                                                ? 'opacity-100'
                                                                                : 'opacity-0'
                                                                        }`}
                                                                    />
                                                                    {papel}
                                                                </CommandItem>
                                                            ))}
                                                        </CommandGroup>
                                                    </CommandList>
                                                </Command>
                                            </PopoverContent>
                                        </Popover>
                                        {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
                                    </Field>
                                );
                            }}
                        </form.Field>
                    </div>

                    <div className="space-y-4">
                        <p className="text-xs uppercase tracking-wide text-muted-foreground">
                            Informações complementares
                        </p>

                        <div className="grid grid-cols-2 gap-4">
                            <form.Field name="telefone">
                                {(field) => {
                                    const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0;
                                    return (
                                        <Field data-invalid={isInvalid} className="text-foreground">
                                            <FieldLabel htmlFor={field.name}>Telefone</FieldLabel>
                                            <Input
                                                id={field.name}
                                                name={field.name}
                                                inputMode="tel"
                                                value={field.state.value}
                                                onBlur={field.handleBlur}
                                                onChange={(e) => field.handleChange(formatPhone(e.target.value))}
                                                aria-invalid={isInvalid}
                                                placeholder="(00) 00000-0000"
                                            />
                                            {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
                                        </Field>
                                    );
                                }}
                            </form.Field>

                            <form.Field name="dataNascimento">
                                {(field) => {
                                    const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0;
                                    const { dia, mes, ano } = field.state.value;
                                    const update = (part: 'dia' | 'mes' | 'ano', max: number, raw: string) =>
                                        field.handleChange({
                                            ...field.state.value,
                                            [part]: onlyDigits(raw, max),
                                        });

                                    return (
                                        <Field data-invalid={isInvalid} className="text-foreground">
                                            <FieldLabel htmlFor={`${field.name}-dia`}>Data de Nascimento</FieldLabel>
                                            <div className="flex gap-2">
                                                <Input
                                                    id={`${field.name}-dia`}
                                                    inputMode="numeric"
                                                    placeholder="DD"
                                                    value={dia}
                                                    onBlur={field.handleBlur}
                                                    onChange={(e) => update('dia', 2, e.target.value)}
                                                    aria-invalid={isInvalid}
                                                    className="px-2 text-center"
                                                />
                                                <Input
                                                    inputMode="numeric"
                                                    placeholder="MM"
                                                    aria-label="Mês"
                                                    value={mes}
                                                    onBlur={field.handleBlur}
                                                    onChange={(e) => update('mes', 2, e.target.value)}
                                                    aria-invalid={isInvalid}
                                                    className="px-2 text-center"
                                                />
                                                <Input
                                                    inputMode="numeric"
                                                    placeholder="YYYY"
                                                    aria-label="Ano"
                                                    value={ano}
                                                    onBlur={field.handleBlur}
                                                    onChange={(e) => update('ano', 4, e.target.value)}
                                                    aria-invalid={isInvalid}
                                                    className="min-w-16 px-2 text-center"
                                                />
                                            </div>
                                            {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
                                        </Field>
                                    );
                                }}
                            </form.Field>
                        </div>

                        <form.Field name="endereco">
                            {(field) => {
                                const isInvalid = field.state.meta.isTouched && field.state.meta.errors.length > 0;
                                return (
                                    <Field data-invalid={isInvalid} className="text-foreground">
                                        <FieldLabel htmlFor={field.name}>Endereço</FieldLabel>
                                        <Input
                                            id={field.name}
                                            name={field.name}
                                            value={field.state.value}
                                            onBlur={field.handleBlur}
                                            onChange={(e) => field.handleChange(e.target.value)}
                                            aria-invalid={isInvalid}
                                            placeholder="Rua Exemplo, 123"
                                        />
                                        {isInvalid && <FieldError errors={field.state.meta.errors} className="-mt-1" />}
                                    </Field>
                                );
                            }}
                        </form.Field>
                    </div>

                    <div className="flex justify-end pt-2">
                        <form.Subscribe
                            selector={(state) => [state.canSubmit, state.isSubmitting, state.isPristine]}
                        >
                            {([canSubmit, isSubmitting, isPristine]) => (
                                <Button type="submit" disabled={!canSubmit || isSubmitting || isPristine}>
                                    {isSubmitting ? <Spinner className="mr-2 size-4" /> : null}
                                    Cadastrar
                                </Button>
                            )}
                        </form.Subscribe>
                    </div>
                </form>
            </DialogContent>
        </Dialog>
    );
}
