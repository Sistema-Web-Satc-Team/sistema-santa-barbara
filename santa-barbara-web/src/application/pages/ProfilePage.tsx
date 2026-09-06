import { useState } from "react";
import { Card } from "@/ui/components/card";
import { Avatar } from "@/ui/components/avatar";
import { Input } from "@/ui/components/input";
import { Button } from "@/ui/components/button";
import { SuccessCard } from "@/ui/components/sucessCard";
import { AlertTriangle, Edit2, Save, X } from "lucide-react";

interface ProfileData {
    fullName: string;
    email: string;
    role: string;
    phone: string;
    adress: string;
    instrumentos: string;
}

const initialProfile: ProfileData = {
    fullName: "Felipe Elias Leal",
    email: "Testes123@gmail.com",
    role: "Aluno",
    phone: "(48) 99173-2269",
    adress: "45 R. Pref. Flávio Righeto",
    instrumentos: "Trompete",
};

export function ProfilePage() {
    const [isEditing, setIsEditing] = useState(false);

    const [successMessage, setSuccessMessage] = useState("");

    const [errors, setErrors] = useState<Record<string, string>>({});
    const [hasUnsavedChanges, setHasUnsavedChanges] = useState(false);
    const [showExitModal, setShowExitModal] = useState(false);

    const [originalProfile, setOriginalProfile] = useState(initialProfile);
    const [profile, setProfile] = useState(initialProfile);

    const handleEditClick = () => {
        setOriginalProfile(profile);
        setErrors({});
        setHasUnsavedChanges(false);
        setIsEditing(true);
    };

    const handleInputChange = (field: keyof ProfileData, value: string) => {
        setProfile((previousProfile) => ({ ...previousProfile, [field]: value }));
        setHasUnsavedChanges(true);

        if (errors[field]) {
            setErrors((previousErrors) => ({ ...previousErrors, [field]: "" }));
        }
    };

    const validateForm = () => {
        const newErrors: Record<string, string> = {};

        if (!profile.email.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(profile.email)) {
            newErrors.email = "E-mail inválido ou obrigatório.";
        }

        if (!profile.phone.trim() || profile.phone.replace(/\D/g, "").length < 10) {
            newErrors.phone = "Informe um telefone válido com DDD.";
        }

        if (!profile.adress.trim()) {
            newErrors.adress = "O endereço não pode estar vazio.";
        }

        if (!profile.instrumentos.trim()) {
            newErrors.instrumentos = "Informe pelo menos um instrumento.";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSave = async () => {
        if (!validateForm()) return;

        setOriginalProfile(profile);
        setSuccessMessage("Dados atualizados com sucesso!");
        setIsEditing(false);
        setHasUnsavedChanges(false);
        setErrors({});

        setTimeout(() => setSuccessMessage(""), 3000);
    };

    const handleCancelClick = () => {
        if (hasUnsavedChanges) {
            setShowExitModal(true);
            return;
        }

        setErrors({});
        setIsEditing(false);
    };

    const confirmExit = () => {
        setProfile(originalProfile);
        setErrors({});
        setHasUnsavedChanges(false);
        setShowExitModal(false);
        setIsEditing(false);

    };

    return (
        <div className="max-w-5xl mx-auto p-6 space-y-6 relative">
            <h1 className="text-2xl font-bold text-(--strong-foreground-color)">Meu Perfil</h1>

            <SuccessCard successMessage={successMessage} />

            {showExitModal && (
                <div className="fixed inset-0 bg-(--strong-foreground-color)/50 flex items-center justify-center z-50 p-4">
                    <Card className="w-full max-w-100 p-6 space-y-4 shadow-xl">
                        <div className="flex items-center gap-3 text-(--failure-color)">
                            <AlertTriangle className="w-6 h-6" />
                            <h2 className="text-xl font-bold">Atenção</h2>
                        </div>
                        <p className="text-(--neutral-color) font-medium">
                            Você tem alterações não salvas. Deseja realmente sair e descartar as mudanças?
                        </p>
                        <div className="flex justify-end gap-3 mt-4 pt-4 border-t">
                            <Button variant="outline" onClick={() => setShowExitModal(false)}>
                                Continuar Editando
                            </Button>
                            <Button variant="normal" onClick={confirmExit}>
                                Sim, sair
                            </Button>
                        </div>
                    </Card>
                </div>
            )}

            <Card className="flex items-center justify-between">
                <div className="flex items-center gap-4">
                    <Avatar  className="w-20 h-20" />
                    <div>
                        <h2 className="text-4xl font-bold text-(--strong-foreground-color)">
                            {profile.fullName}
                        </h2>
                        <p className="text-xl text-(--strong-foreground-color)">{profile.role}</p>
                    </div>
                </div>

                <div>
                    {!isEditing ? (
                        <Button variant="normal" onClick={handleEditClick}>
                            <Edit2 className="w-4 h-4 mr-2 inline" /> Editar
                        </Button>
                    ) : (
                        <div className="flex gap-2">
                            <Button variant="outline" onClick={handleCancelClick}>
                                <X className="w-4 h-4 mr-2 inline" /> Cancelar
                            </Button>
                            <Button variant="normal" onClick={handleSave}>
                                <Save className="w-4 h-4 mr-2 inline" /> Salvar
                            </Button>
                        </div>
                    )}
                </div>
            </Card>

            <Card className="space-y-4">
                <div className="flex flex-col gap-4">
                    <div className="flex flex-col sm:flex-row sm:items-start justify-start gap-2 border-b text-(--light-neutral-color) pb-3">
                        <span className="text-2xl font-semibold text-(--strong-foreground-color) mt-1 sm:w-40">Telefone:</span>
                        <div className="flex flex-col flex-1">
                            {!isEditing ? (
                                <span className="text-(--strong-foreground-color) text-lg mt-1">{profile.phone}</span>
                            ) : (
                                <>
                                    <Input
                                        value={profile.phone}
                                        onChange={(event) => handleInputChange("phone", event.target.value)}
                                        className={`mt-1 text-lg text-(--strong-foreground-color) ${errors.phone ? "border-(--failure-color) focus:ring-(--failure-color)" : ""}`}
                                    />
                                    {errors.phone && <span className="text-sm text-(--failure-color) mt-1">{errors.phone}</span>}
                                </>
                            )}
                        </div>
                    </div>

                    <div className="flex flex-col sm:flex-row sm:items-start justify-start gap-2 border-b text-(--light-neutral-color) pb-3">
                        <span className="text-2xl font-semibold text-(--strong-foreground-color) mt-1 sm:w-40">Endereço:</span>
                        <div className="flex flex-col flex-1">
                            {!isEditing ? (
                                <span className="text-(--strong-foreground-color) text-lg mt-1">{profile.adress}</span>
                            ) : (
                                <>
                                    <Input
                                        value={profile.adress}
                                        onChange={(event) => handleInputChange("adress", event.target.value)}
                                        className={`mt-1 text-lg text-(--strong-foreground-color) ${errors.adress ? "border-(--failure-color) focus:ring-(--failure-color)" : ""}`}
                                    />
                                    {errors.adress && <span className="text-sm text-(--failure-color) mt-1">{errors.adress}</span>}
                                </>
                            )}
                        </div>
                    </div>

                    <div className="flex flex-col sm:flex-row sm:items-start justify-start gap-2 border-b text-(--light-neutral-color) pb-3">
                        <span className="text-2xl font-semibold text-(--strong-foreground-color) mt-1 sm:w-40">Gmail:</span>
                        <div className="flex flex-col flex-1">
                            {!isEditing ? (
                                <span className="text-(--strong-foreground-color) text-lg mt-1">{profile.email}</span>
                            ) : (
                                <>
                                    <Input
                                        value={profile.email}
                                        onChange={(event) => handleInputChange("email", event.target.value)}
                                        className={`mt-1 text-lg text-(--strong-foreground-color) ${errors.email ? "border-(--failure-color) focus:ring-(--failure-color)" : ""}`}
                                    />
                                    {errors.email && <span className="text-sm text-(--failure-color) mt-1">{errors.email}</span>}
                                </>
                            )}
                        </div>
                    </div>

                    <div className="flex flex-col sm:flex-row sm:items-start justify-start gap-2">
                        <span className="text-2xl font-semibold text-(--strong-foreground-color) mt-1 sm:w-40">Instrumentos:</span>
                        <div className="flex flex-col flex-1">
                            {!isEditing ? (
                                <span className="text-lg text-(--strong-foreground-color) mt-1">{profile.instrumentos}</span>
                            ) : (
                                <>
                                    <Input
                                        value={profile.instrumentos}
                                        onChange={(event) => handleInputChange("instrumentos", event.target.value)}
                                        className={`mt-1 text-lg text-(--strong-foreground-color) ${errors.instrumentos ? "border-(--failure-color) focus:ring-(--failure-color)" : ""}`}
                                    />
                                    {errors.instrumentos && <span className="text-sm text-(--failure-color) mt-1">{errors.instrumentos}</span>}
                                </>
                            )}
                        </div>
                    </div>
                </div>
            </Card>
        </div>
    );
}

export default ProfilePage;