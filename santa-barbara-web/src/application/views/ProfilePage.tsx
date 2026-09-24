import { Avatar } from "@/ui/components/avatar";
import { Button } from "@/ui/components/button";
import { Card } from "@/ui/components/card";
import { ErrorCard } from "@/ui/components/error-card";
import { Input } from "@/ui/components/input";
import { SuccessCard } from "@/ui/components/sucess-card";
import "@/ui/styles/profile.css";
import { useProfile } from "../hook/useProfile";


export function Profile() {
    const { data, state, actions } = useProfile();

    return (
        <div className="lg:w-[600px] md:w-[550px] mx-auto p-6 space-y-6">
            {state.isInitialized && <h1>Meu Perfil:</h1>}

            <SuccessCard message={state.successMessage} />
            <ErrorCard message={state.errorMessage} />

            {state.isInitialized && (
            
            <>
            <Card className="flex items-center gap-6 lg:px-12 md:px-8 px-6 py-6 ">
                <Avatar className="w-25 h-25" nome={data.nome}/>
                <div className="flex flex-col gap-1">
                    <h2>
                        {`${data.nome} ${data.sobrenome}`}
                    </h2>
                    {data.papeis?.map((papel: string) => (
                        <span key={papel} className="text-sm text-(--neutral-color) block">{papel}</span>
                    ))}
                </div>
            </Card>

            <Card className="lg:px-12 md:px-8 px-6 py-8 space-y-6">
                <div className="flex flex-col gap-6">
                    <div className="flex flex-col gap-1">
                        <label>Telefone:</label>
                        <Input
                            value={data.telefone}
                            variant="discreet"
                            onChange={(event) => actions.onChangeCampo("telefone", event.target.value)}
                            className={state.errorsFields.telefone ? "border-(--failure-color)" : ""}
                        />
                        {state.errorsFields.telefone && <span className="text-xs text-(--failure-color)">{state.errorsFields.telefone}</span>}
                    </div>

                    <div className="flex flex-col gap-1">
                        <label>Endereço:</label>
                        <Input
                            value={data.endereco}
                            variant="discreet"
                            onChange={(event) => actions.onChangeCampo("endereco", event.target.value)}
                            className={state.errorsFields.endereco ? "border-(--failure-color)" : ""}
                        />
                        {state.errorsFields.endereco && <span className="text-xs text-(--failure-color)">{state.errorsFields.endereco}</span>}
                    </div>

                    <div className="flex flex-col gap-1">
                        <label>Email:</label>
                        <Input
                            value={data.email}
                            variant="discreet"
                            onChange={(event) => actions.onChangeCampo("email", event.target.value)}
                            className={state.errorsFields.email ? "border-(--failure-color)" : ""}
                        />
                        {state.errorsFields.email && <span className="text-xs text-(--failure-color)">{state.errorsFields.email}</span>}
                    </div>
                </div>

                <div className="flex justify-end mt-18">
                    <Button 
                        variant="normal" 
                        onClick={actions.save}
                        disabled={state.isLoading || !state.canSave()}
                    >
                        {state.isLoading ? "Salvando..." : "Salvar"}
                    </Button>
                </div>
            </Card>
            </>
            
            )}
        </div>
    );
}

export default Profile;
