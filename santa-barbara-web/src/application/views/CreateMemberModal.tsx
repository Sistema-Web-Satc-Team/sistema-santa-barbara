import { Combobox, ComboboxContent, ComboboxEmpty, ComboboxItem, ComboboxList, ComboboxTrigger, useComboboxAnchor } from "@/ui/components/combobox";
import { Button } from "@/ui/components/button";
import { Input } from "@/ui/components/input";
import { X } from "lucide-react";
import { useState } from "react";
import type { CreateMemberRequest } from "../services/member.service";

interface MemberFormData {
	nome: string;
	email: string;
	papeis: string[];
	telefone: string;
	dataNascimento: { dia: string; mes: string; ano: string };
	endereco: string;
}

interface CreateMemberModalProps {
	isOpen: boolean;
	papeisDisponiveis: string[];
	onClose: () => void;
	onSave: (data: CreateMemberRequest) => Promise<void> | void;
}

const initialFormData: MemberFormData = {
	nome: "",
	email: "",
	papeis: [],
	telefone: "",
	dataNascimento: { dia: "", mes: "", ano: "" },
	endereco: "",
};

function toDatePayload(dataNascimento: MemberFormData["dataNascimento"]): string {
	return `${dataNascimento.dia}/${dataNascimento.mes}/${dataNascimento.ano}`;
}

export function CreateMemberModal({ isOpen, papeisDisponiveis, onClose, onSave }: CreateMemberModalProps) {
	const [formData, setFormData] = useState<MemberFormData>(initialFormData);
	const [erros, setErros] = useState<Partial<Record<keyof MemberFormData | "dataNascimento", string>>>({});
	const [papelSelecionado, setPapelSelecionado] = useState("");
	const [isSaving, setIsSaving] = useState(false);
	const [isPapeisOpen, setIsPapeisOpen] = useState(false);
	const papeisAnchor = useComboboxAnchor();

	if (!isOpen) return null;

	const updateField = <K extends keyof MemberFormData>(field: K, value: MemberFormData[K]) => {
		setFormData((previous) => ({ ...previous, [field]: value }));
		setErros((previous) => ({ ...previous, [field]: undefined }));
	};

	const updateDateField = (field: keyof MemberFormData["dataNascimento"], value: string) => {
		setFormData((previous) => ({
			...previous,
			dataNascimento: { ...previous.dataNascimento, [field]: value.replace(/\D/g, "") },
		}));
		setErros((previous) => ({ ...previous, dataNascimento: undefined }));
	};

	const validateForm = () => {
		const nextErrors: Partial<Record<keyof MemberFormData | "dataNascimento", string>> = {};
		const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
		const date = formData.dataNascimento;
		const dateValue = `${date.ano}-${date.mes}-${date.dia}`;
		const parsedDate = new Date(`${dateValue}T00:00:00`);

		if (formData.nome.trim().length < 2) nextErrors.nome = "O nome deve ter pelo menos 2 caracteres.";
		if (!emailRegex.test(formData.email.trim())) nextErrors.email = "Insira um e-mail válido.";
		if (formData.papeis.length === 0) nextErrors.papeis = "Selecione pelo menos um papel.";
		if (formData.telefone && formData.telefone.replace(/\D/g, "").length < 11) nextErrors.telefone = "O telefone deve ter pelo menos 11 dígitos.";
		if (!formData.endereco.trim() || formData.endereco.trim().length < 2) nextErrors.endereco = "O endereço deve ter pelo menos 2 caracteres.";
		if (!/^\d{2}$/.test(date.dia) || !/^\d{2}$/.test(date.mes) || !/^\d{4}$/.test(date.ano) || Number.isNaN(parsedDate.getTime()) || parsedDate >= new Date()) {
			nextErrors.dataNascimento = "Informe uma data de nascimento válida no passado.";
		}

		setErros(nextErrors);
		return Object.keys(nextErrors).length === 0;
	};

	const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();
		if (!validateForm()) return;

		setIsSaving(true);
		try {
			await onSave({
				nome: formData.nome.trim(),
				email: formData.email.trim(),
				papeis: formData.papeis,
				telefone: formData.telefone.replace(/\D/g, ""),
				endereco: formData.endereco.trim(),
				dataNascimento: toDatePayload(formData.dataNascimento),
			});
			onClose();
		} finally {
			setIsSaving(false);
		}
	};

	const addRole = (value: string | null) => {
		if (!value || formData.papeis.includes(value)) return;
		updateField("papeis", [...formData.papeis, value]);
		setPapelSelecionado("");
		setIsPapeisOpen(false);
	};

	const isFormComplete = formData.nome.trim() !== ""
		&& formData.email.trim() !== ""
		&& formData.papeis.length > 0
		&& formData.telefone.trim() !== ""
		&& formData.dataNascimento.dia !== ""
		&& formData.dataNascimento.mes !== ""
		&& formData.dataNascimento.ano !== ""
		&& formData.endereco.trim() !== "";

	return (
		<div className="fixed inset-0 z-50 flex items-center justify-center bg-(--strong-foreground-color)/50 p-4" onMouseDown={(event) => event.target === event.currentTarget && onClose()}>
			<div className="flex max-h-[90vh] w-full max-w-136 flex-col overflow-y-auto rounded-lg bg-(--surface-color) p-6 shadow-2xl" role="dialog" aria-modal="true" aria-labelledby="create-member-title">
				<div className="flex w-full items-center justify-between pb-4">
					<h2 id="create-member-title" className="text-xl font-bold text-(--strong-foreground-color)">Cadastrar Membro</h2>
					<button type="button" onClick={onClose} className="flex h-8 w-8 items-center justify-center rounded-md text-(--neutral-color) hover:bg-(--strong-surface-color)" aria-label="Fechar">
						<X size={18} />
					</button>
				</div>

				<form onSubmit={handleSubmit} className="mt-4 flex w-full flex-col space-y-6">
					<section className="space-y-4">
						<h3 className="text-xs font-medium uppercase tracking-wider text-(--neutral-color)">Informações básicas</h3>
						<div>
							<label htmlFor="create-member-name" className="mb-1 w-full block text-sm font-medium">Nome *</label>
							<Input id="create-member-name" variant="discreet" className="w-full" placeholder="placeholder" value={formData.nome} onChange={(event) => updateField("nome", event.target.value)} aria-invalid={Boolean(erros.nome)} />
							{erros.nome && <span className="text-xs text-(--error-color)">{erros.nome}</span>}
						</div>
						<div>
							<label htmlFor="create-member-email" className="mb-1 w-full block text-sm font-medium">Email *</label>
							<Input id="create-member-email" variant="discreet" type="email" className="w-full" placeholder="placeholder" value={formData.email} onChange={(event) => updateField("email", event.target.value)} aria-invalid={Boolean(erros.email)} />
							{erros.email && <span className="text-xs text-(--error-color)">{erros.email}</span>}
						</div>
						<div>
							<label className="mb-1 block text-sm font-medium">Papéis *</label>
							<div ref={papeisAnchor} onClick={() => setIsPapeisOpen(true)} className="flex min-h-12 w-full cursor-pointer flex-wrap items-center gap-2 rounded-lg border border-(--foreground-color) px-4 py-2 focus-within:border-(--hover-brand-color) focus-within:ring-1 focus-within:ring-(--hover-brand-color)">
								<Combobox open={isPapeisOpen} onOpenChange={setIsPapeisOpen} value={papelSelecionado} onValueChange={addRole} items={papeisDisponiveis}>
									<div className="contents">
										{formData.papeis.map((papel) => <span key={papel} className="flex items-center gap-1.5 rounded-full bg-(--brand-color) px-3 py-1.5 text-xs font-semibold text-(--surface-color)">{papel}<button type="button" onClick={(event) => { event.stopPropagation(); updateField("papeis", formData.papeis.filter((item) => item !== papel)); }} className="flex items-center" aria-label={`Remover ${papel}`}><X size={12} strokeWidth={3} /></button></span>)}
										<ComboboxTrigger showIcon={false} className="ml-auto flex size-6 shrink-0 items-center justify-center rounded-sm text-(--strong-foreground-color) hover:bg-(--strong-surface-color)" aria-label={isPapeisOpen ? "Fechar opções de papéis" : "Abrir opções de papéis"}>
											<span className={`border-l-[5px] border-r-[5px] border-t-[6px] border-l-transparent border-r-transparent border-t-(--strong-foreground-color) transition-transform ${isPapeisOpen ? "rotate-180" : ""}`} />
										</ComboboxTrigger>
									</div>
									<ComboboxContent anchor={papeisAnchor} className="border border-(--light-neutral-color) bg-(--surface-color) text-(--foreground-color)">
										<ComboboxList>
											<ComboboxEmpty>Nenhum papel encontrado.</ComboboxEmpty>
											{papeisDisponiveis.filter((papel) => !formData.papeis.includes(papel)).map((papel) => <ComboboxItem key={papel} value={papel}>{papel}</ComboboxItem>)}
										</ComboboxList>
									</ComboboxContent>
								</Combobox>
							</div>
							{erros.papeis && <span className="mt-1 block text-xs text-(--error-color)">{erros.papeis}</span>}
						</div>
					</section>

					<section className="space-y-4">
						<h3 className="text-xs font-medium uppercase tracking-wider text-(--neutral-color)">Informações complementares</h3>
						<div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
							<div>
								<label htmlFor="create-member-phone" className="mb-1 block text-sm font-medium">Telefone</label>
								<Input id="create-member-phone" variant="discreet" inputMode="numeric" placeholder="(00) 00000-0000" value={formData.telefone} onChange={(event) => updateField("telefone", event.target.value)} aria-invalid={Boolean(erros.telefone)} />
								{erros.telefone && <span className="text-xs text-(--error-color)">{erros.telefone}</span>}
							</div>
							<div>
								<span className="mb-1 block text-sm font-medium">Data de nascimento *</span>
								<div className="flex gap-2 w-2/3">
									<Input variant="discreet" className="w-14" inputMode="numeric" placeholder="DD" maxLength={2} value={formData.dataNascimento.dia} onChange={(event) => updateDateField("dia", event.target.value)} aria-label="Dia" />
									<Input variant="discreet" className="w-14" inputMode="numeric" placeholder="MM" maxLength={2} value={formData.dataNascimento.mes} onChange={(event) => updateDateField("mes", event.target.value)} aria-label="Mês" />
									<Input variant="discreet" className="w-20" inputMode="numeric" placeholder="AAAA" maxLength={4} value={formData.dataNascimento.ano} onChange={(event) => updateDateField("ano", event.target.value)} aria-label="Ano" />
								</div>
								{erros.dataNascimento && <span className="text-xs text-(--error-color)">{erros.dataNascimento}</span>}
							</div>
						</div>
						<div>
							<label htmlFor="create-member-address" className="mb-1 block  text-sm font-medium">Endereço</label>
							<Input id="create-member-address" variant="discreet" className="w-full" placeholder="placeholder" value={formData.endereco} onChange={(event) => updateField("endereco", event.target.value)} aria-invalid={Boolean(erros.endereco)} />
							{erros.endereco && <span className="text-xs text-(--error-color)">{erros.endereco}</span>}
						</div>
					</section>

					<div className="flex justify-end pt-10">
						<Button type="submit" variant="normal" disabled={isSaving || !isFormComplete}>{isSaving ? "Cadastrando..." : "Cadastrar"}</Button>
					</div>
				</form>
			</div>
		</div>
	);
}

export type { CreateMemberModalProps, MemberFormData };
