import { Avatar } from "@/ui/components/avatar";
import { Button } from "@/ui/components/button";
import { Card } from "@/ui/components/card";
import { ErrorCard } from "@/ui/components/error-card";
import { Input } from "@/ui/components/input";
import { SuccessCard } from "@/ui/components/sucess-card";
import { AlertTriangle, Edit2, Save, X } from "lucide-react";
import { useEffect, useState } from "react";
import type { ProfileData } from "../model/ProfileData";
import { AuthService } from "../services/auth.service";


const initialProfile: ProfileData = {
    nome: "",
    sobrenome: "",
    email: "",
    nomeUsuario: "",
    telefone: "",
    endereco: "",
    instrumentos: "",
    papeis: []
}

export function ProfilePage() {
    const [isEditing, setIsEditing] = useState(false);

    const [successMessage, setSuccessMessage] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    const [errorsField, setFieldErrors] = useState<Record<string, string>>({});
    const [hasUnsavedChanges, setHasUnsavedChanges] = useState(false);
    const [showExitModal, setShowExitModal] = useState(false);

    const [originalProfile, setOriginalProfile] = useState<ProfileData>(initialProfile);
    const [profile, setProfile] = useState<ProfileData>(initialProfile);

    useEffect(() => {
        (async () => {
            try {
                const profileData = await AuthService.me();
                setOriginalProfile(profileData);
                setProfile(profileData);
            } catch(err) {
                setErrorMessage(err instanceof Error ? err.message : String(err));
            }
        })()
    }, [])

    const handleEditClick = () => {
        setOriginalProfile(profile);
        setFieldErrors({});
        setHasUnsavedChanges(false);
        setIsEditing(true);
    };

    const handleInputChange = (field: keyof ProfileData, value: string) => {

        setProfile((previousProfile) => ({ ...previousProfile, [field]: value }));
        setHasUnsavedChanges(true);

        if (errorsField[field]) {
            setFieldErrors((previousErrors) => ({ ...previousErrors, [field]: "" }));
        }
    };

    const validateForm = () => {
        const newErrors: Record<string, string> = {};

        if (!profile.email.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(profile.email)) {
            newErrors.email = "E-mail inválido ou obrigatório.";
        }

        if (!profile.telefone.trim() || profile.telefone.replace(/\D/g, "").length < 10) {
            newErrors.telefone = "Informe um telefone válido com DDD.";
        }

        if (!profile.endereco.trim()) {
            newErrors.endereco = "O endereço não pode estar vazio.";
        }

        if (profile.instrumentos.length < 1) {
            newErrors.instrumentos = "Informe pelo menos um instrumento.";
        }

        setFieldErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSave = async () => {
        if (!validateForm()) return;
        
        try {
            AuthService.updateMe(profile);
            setOriginalProfile(profile);
            setSuccessMessage("Dados atualizados com sucesso!");
            setIsEditing(false);
            setHasUnsavedChanges(false);
            setFieldErrors({});
            setTimeout(() => setSuccessMessage(""), 3000);
        } catch(e) {
            setErrorMessage(e.message);
        }
    };

    const handleCancelClick = () => {
        if (hasUnsavedChanges) {
            setShowExitModal(true);
            return;
        }

        setFieldErrors({});
        setIsEditing(false);
    };

    const confirmExit = () => {
        setProfile(originalProfile);
        setFieldErrors({});
        setHasUnsavedChanges(false);
        setShowExitModal(false);
        setIsEditing(false);

    };

    return (
        <div className="max-w-5xl mx-auto p-6 space-y-6 relative">
            <h1 className="text-2xl font-bold text-(--strong-foreground-color)">Meu Perfil</h1>

            <SuccessCard message={successMessage} />
            <ErrorCard message={errorMessage} />

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
                            {`${profile.nome} ${profile.sobrenome}`}
                        </h2>
                        {
                            profile.papeis?.map(
                                (p: string) => 
                                    (<span className="text-xl text-(--strong-foreground-color)">{p}</span>)
                            )
                        }
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
                                <span className="text-(--strong-foreground-color) text-lg mt-1">{profile.telefone}</span>
                            ) : (
                                <>
                                    <Input
                                        value={profile.telefone}
                                        onChange={(event) => handleInputChange("telefone", event.target.value)}
                                        className={`mt-1 text-lg text-(--strong-foreground-color) ${errorsField.telefone ? "border-(--failure-color) focus:ring-(--failure-color)" : ""}`}
                                    />
                                    {errorsField.telefone && <span className="text-sm text-(--failure-color) mt-1">{errorsField.telefone}</span>}
                                </>
                            )}
                        </div>
                    </div>
                    

                    <div className="flex flex-col sm:flex-row sm:items-start justify-start gap-2 border-b text-(--light-neutral-color) pb-3">
                        <span className="text-2xl font-semibold text-(--strong-foreground-color) mt-1 sm:w-40">Endereço:</span>
                        <div className="flex flex-col flex-1">
                            {!isEditing ? (
                                <span className="text-(--strong-foreground-color) text-lg mt-1">{profile.endereco}</span>
                            ) : (
                                <>
                                    <Input
                                        value={profile.endereco}
                                        onChange={(event) => handleInputChange("endereco", event.target.value)}
                                        className={`mt-1 text-lg text-(--strong-foreground-color) ${errorsField.endereco ? "border-(--failure-color) focus:ring-(--failure-color)" : ""}`}
                                    />
                                    {errorsField.endereco && <span className="text-sm text-(--failure-color) mt-1">{errorsField.endereco}</span>}
                                </>
                            )}
                        </div>
                    </div>
                    

                    <div className="flex flex-col sm:flex-row sm:items-start justify-start gap-2 border-b text-(--light-neutral-color) pb-3">
                        <span className="text-2xl font-semibold text-(--strong-foreground-color) mt-1 sm:w-40">Email:</span>
                        <div className="flex flex-col flex-1">
                            {!isEditing ? (
                                <span className="text-(--strong-foreground-color) text-lg mt-1">{profile.email}</span>
                            ) : (
                                <>
                                    <Input
                                        value={profile.email}
                                        onChange={(event) => handleInputChange("email", event.target.value)}
                                        className={`mt-1 text-lg text-(--strong-foreground-color) ${errorsField.email ? "border-(--failure-color) focus:ring-(--failure-color)" : ""}`}
                                    />
                                    {errorsField.email && <span className="text-sm text-(--failure-color) mt-1">{errorsField.email}</span>}
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
                                        className={`mt-1 text-lg text-(--strong-foreground-color) ${errorsField.instrumentos ? "border-(--failure-color) focus:ring-(--failure-color)" : ""}`}
                                    />
                                    {errorsField.instrumentos && <span className="text-sm text-(--failure-color) mt-1">{errorsField.instrumentos}</span>}
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