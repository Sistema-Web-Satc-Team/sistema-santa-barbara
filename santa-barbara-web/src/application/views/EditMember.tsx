import { Button } from "@/ui/components/button";
import { Card } from "@/ui/components/card";
import { ErrorCard } from "@/ui/components/error-card";
import { Input } from "@/ui/components/input";
import { SuccessCard } from "@/ui/components/sucess-card";
import type { FormEvent } from "react";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router";
import { memberService, type UpdateMemberRequest } from "../services/member.service";

function EditMember() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

    const [formData, setFormData] = useState<UpdateMemberRequest>({
        nome: "",
        sobrenome: "",
        email: "",
        telefone: "",
        endereco: "",
        status: "",
    });

    useEffect(() => {
        async function loadMember() {
            if (!id) return;
            try {
                setLoading(true);
                const data = await memberService.getMemberById(id);
                setFormData({
                    nome: data.nome || "",
                    sobrenome: data.sobrenome || "",
                    email: data.email || "",
                    telefone: data.telefone || "",
                    endereco: data.endereco || "",
                    status: data.status || "",
                });
            } catch {
                setErrorMessage("Erro ao carregar os dados do membro.");
            } finally {
                setLoading(false);
            }
        }
        loadMember();
    }, [id]);

    const handleInputChange = (field: keyof UpdateMemberRequest, value: string) => {
        setFormData((prev) => ({ ...prev, [field]: value }));
        if (fieldErrors[field]) {
            setFieldErrors((prev) => ({ ...prev, [field]: "" }));
        }
    };

    const validateForm = () => {
        const newErrors: Record<string, string> = {};

        if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
            newErrors.email = "E-mail inválido.";
        }

        const telefoneLimpo = (formData.telefone || "").replace(/\D/g, "");
        if (formData.telefone && telefoneLimpo.length > 0 && (telefoneLimpo.length < 11 || telefoneLimpo.length > 15)) {
            newErrors.telefone = "O telefone deve ter entre 11 e 15 dígitos.";
        }

        const endereco = formData.endereco || "";
        if (endereco.length > 0 && (endereco.length < 2 || endereco.length > 200)) {
            newErrors.endereco = "O endereço deve ter entre 2 e 200 caracteres.";
        }

        setFieldErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSave = async (e: FormEvent) => {
        e.preventDefault();
        if (!validateForm() || !id) return;

        try {
            setSaving(true);
            setErrorMessage("");
            
            await memberService.updateMember(id, formData);

            setSuccessMessage("Membro atualizado com sucesso!");
            setTimeout(() => {
                navigate("/membros");
            }, 1500);
        } catch (err) {
            setErrorMessage(err instanceof Error ? err.message : "Não foi possível atualizar o membro.");
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return <div className="p-8 text-center text-(--neutral-color)">A carregar dados do membro...</div>;
    }

    return (
        <div className="max-w-3xl mx-auto p-6 space-y-6">
            <h1 className="text-2xl font-bold text-(--strong-foreground-color)">Editar Membro (Administrador)</h1>

            <SuccessCard message={successMessage} />
            <ErrorCard message={errorMessage} />

            <Card className="p-6">
                <form onSubmit={handleSave} className="space-y-4">
                    
                    <div>
                        <label className="block text-sm font-medium mb-1">Nome</label>
                        <Input
                            value={formData.nome || ""}
                            onChange={(e) => handleInputChange("nome", e.target.value)}
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">Sobrenome</label>
                        <Input
                            value={formData.sobrenome || ""}
                            onChange={(e) => handleInputChange("sobrenome", e.target.value)}
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">E-mail</label>
                        <Input
                            type="email"
                            value={formData.email || ""}
                            onChange={(e) => handleInputChange("email", e.target.value)}
                        />
                        {fieldErrors.email && <span className="text-sm text-(--error-color) mt-1 block">{fieldErrors.email}</span>}
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">Telefone</label>
                        <Input
                            type="tel"
                            value={formData.telefone || ""}
                            onChange={(e) => handleInputChange("telefone", e.target.value)}
                        />
                        {fieldErrors.telefone && <span className="text-sm text-(--error-color) mt-1 block">{fieldErrors.telefone}</span>}
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">Endereço</label>
                        <Input
                            value={formData.endereco || ""}
                            onChange={(e) => handleInputChange("endereco", e.target.value)}
                        />
                        {fieldErrors.endereco && <span className="text-sm text-(--error-color) mt-1 block">{fieldErrors.endereco}</span>}
                    </div>

                    <div className="flex justify-end gap-3 pt-4 border-t">
                        <Button type="button" variant="outline" onClick={() => navigate("/membros")}>
                            Cancelar
                        </Button>
                        <Button type="submit" variant="normal" disabled={saving}>
                            {saving ? "Salvando..." : "Salvar Alterações"}
                        </Button>
                    </div>
                </form>
            </Card>
        </div>
    );
}

export default EditMember;