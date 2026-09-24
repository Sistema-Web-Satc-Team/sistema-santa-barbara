import type { MemberData } from "@/application/model/MemberData";
import { Button } from "@/ui/components/button";
import { ErrorCard } from "@/ui/components/error-card";
import { Input } from "@/ui/components/input";
import { SuccessCard } from "@/ui/components/sucess-card";
import "@/ui/styles/edit-member-modal.css";
import { X } from "lucide-react";
import type { FormEvent } from "react";
import { useEffect, useState } from "react";
import { memberService, type UpdateMemberRequest } from "../services/member.service";

interface EditMemberModalProps {
    member: MemberData;
    onClose: () => void;
    onSuccess: () => void;
}

export function EditMemberModal({ member, onClose, onSuccess }: EditMemberModalProps) {
    const [saving, setSaving] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
    const [showExitConfirmation, setShowExitConfirmation] = useState(false);
    const [formData, setFormData] = useState<UpdateMemberRequest>({
        nome: member.nome || "",
        sobrenome: member.sobrenome || "",
        email: member.email || "",
        telefone: member.telefone || "",
        endereco: member.endereco || "",
        status: member.status || "",
    });

    const hasUnsavedChanges = formData.email !== (member.email || "")
        || formData.telefone !== (member.telefone || "")
        || formData.endereco !== (member.endereco || "");

    const handleClose = () => {
        if (hasUnsavedChanges && !saving) {
            setShowExitConfirmation(true);
            return;
        }

        onClose();
    };

    useEffect(() => {
        const handleEscape = (event: KeyboardEvent) => {
            if (event.key === "Escape") handleClose();
        };

        document.addEventListener("keydown", handleEscape);
        return () => document.removeEventListener("keydown", handleEscape);
    }, [handleClose]);

    const handleInputChange = (field: keyof UpdateMemberRequest, value: string) => {
        setFormData((previous) => ({ ...previous, [field]: value }));
        if (fieldErrors[field]) {
            setFieldErrors((previous) => ({ ...previous, [field]: "" }));
        }
    };

    const validateForm = () => {
        const errors: Record<string, string> = {};
        const phoneDigits = (formData.telefone || "").replace(/\D/g, "");
        const address = (formData.endereco || "").trim();

        if (!formData.email?.trim()) {
            errors.email = "O e-mail é obrigatório.";
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
            errors.email = "E-mail inválido.";
        }
        if (!phoneDigits) {
            errors.telefone = "O telefone é obrigatório.";
        } else if (phoneDigits.length < 11 || phoneDigits.length > 15) {
            errors.telefone = "O telefone deve ter entre 11 e 15 dígitos.";
        }
        if (!address) {
            errors.endereco = "O endereço é obrigatório.";
        } else if (address.length < 2 || address.length > 200) {
            errors.endereco = "O endereço deve ter entre 2 e 200 caracteres.";
        }

        setFieldErrors(errors);
        return Object.keys(errors).length === 0;
    };

    const handleSave = async (event: FormEvent) => {
        event.preventDefault();
        if (!validateForm()) return;

        try {
            setSaving(true);
            setErrorMessage("");
            await memberService.updateMember(member.id, {
                email: formData.email,
                telefone: formData.telefone,
                endereco: formData.endereco,
            });
            setSuccessMessage("Membro atualizado com sucesso!");
            setTimeout(onSuccess, 700);
        } catch (error) {
            setErrorMessage(error instanceof Error ? error.message : "Não foi possível atualizar o membro.");
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-(--strong-foreground-color)/50 p-4" onMouseDown={(event) => event.target === event.currentTarget && handleClose()}>
            <div className="flex max-h-[90vh] w-full max-w-2xl flex-col gap-9 overflow-y-auto rounded-lg bg-(--surface-color) p-9 shadow-2xl" role="dialog" aria-modal="true" aria-labelledby="edit-member-title">
                <div className="flex items-center justify-between border-b border-(--light-neutral-color) pb-4">
                    <div>
                        <h2 id="edit-member-title" className="text-xl font-bold text-(--strong-foreground-color)">Editar Membro</h2>
                    </div>
                    <button type="button" onClick={handleClose} className="flex h-8 w-8 items-center justify-center rounded-md border border-(--brand-color)/50 bg-(--surface-color) text-(--neutral-color) transition-colors hover:bg-(--strong-surface-color) hover:text-(--strong-foreground-color)" aria-label="Fechar">
                        <X size={16} strokeWidth={2} />
                    </button>
                </div>

                <SuccessCard message={successMessage} />
                <ErrorCard message={errorMessage} />

                <form onSubmit={handleSave} className="grid grid-cols-1 gap-10 sm:grid-cols-2">
                        <div className="space-y-1">
                            <span className="block text-sm font-medium text-(--foreground-color)">Membro</span>
                            <div className="flex min-h-10 items-center rounded-md border border-(--light-neutral-color) bg-(--strong-surface-color)/30 px-3 py-2">
                                <span className="block truncate text-sm text-(--strong-foreground-color)" title={`${formData.nome} ${formData.sobrenome}`}>
                                    {formData.nome} {formData.sobrenome}
                                </span>
                            </div>
                        </div>

                        <div className="space-y-1">
                            <label className="block text-sm font-medium text-(--foreground-color)">E-mail</label>
                            <Input type="email" value={formData.email || ""} onChange={(event) => handleInputChange("email", event.target.value)} className="edit-member-modal__input" />
                            {fieldErrors.email && <span className="mt-1 block text-sm text-(--error-color)">{fieldErrors.email}</span>}
                        </div>

                        <div className="space-y-1">
                            <label className="block text-sm font-medium text-(--foreground-color)">Telefone</label>
                            <Input type="tel" value={formData.telefone || ""} onChange={(event) => handleInputChange("telefone", event.target.value)} className="edit-member-modal__input" />
                            {fieldErrors.telefone && <span className="mt-1 block text-sm text-(--error-color)">{fieldErrors.telefone}</span>}
                        </div>

                        <div className="space-y-1">
                            <label className="block text-sm font-medium text-(--foreground-color)">Endereço</label>
                            <Input value={formData.endereco || ""} onChange={(event) => handleInputChange("endereco", event.target.value)} className="edit-member-modal__input" />
                            {fieldErrors.endereco && <span className="mt-1 block text-sm text-(--error-color)">{fieldErrors.endereco}</span>}
                        </div>

                        <div className="col-span-full mt-3 flex justify-end gap-4 border-t border-(--light-neutral-color) pt-7">
                            <Button type="button" variant="outline" onClick={handleClose}>Cancelar</Button>
                            <Button type="submit" variant="normal" disabled={saving || !hasUnsavedChanges}>{saving ? "Salvando..." : "Salvar Alterações"}</Button>
                        </div>
                    </form>

                {showExitConfirmation && (
                    <div className="fixed inset-0 z-60 flex items-center justify-center bg-(--strong-foreground-color)/40 p-4">
                        <div className="w-full max-w-md rounded-lg bg-(--surface-color) p-6 shadow-2xl" role="alertdialog" aria-modal="true" aria-labelledby="exit-member-title">
                            <h3 id="exit-member-title" className="text-lg font-bold text-(--strong-foreground-color)">Alterações não salvas</h3>
                            <p className="mt-2 text-sm text-(--neutral-color)">Você tem alterações não salvas. Deseja realmente sair?</p>
                            <div className="mt-6 flex justify-end gap-3">
                                <Button type="button" variant="outline" onClick={() => setShowExitConfirmation(false)}>Continuar editando</Button>
                                <Button type="button" variant="normal" onClick={onClose}>Sair sem salvar</Button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default EditMemberModal;
