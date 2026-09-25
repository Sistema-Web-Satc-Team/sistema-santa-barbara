import { useState } from "react";
import type { LoginMemberRequest } from "@/application/model/auth/LoginMemberRequest";
import { useServices } from "@/application/hook/useServices";


interface LoginData {
    login: string;
    senha: string;
}

interface LoginState {
    isCarregando: boolean;
    hasErro: () => boolean;

    erro: string | null;
}

interface LoginActions {
  onChangeCampo: (campo: keyof LoginData, valor: string) => void;
  onSubmit: () => Promise<void>;
}


function useLogin():  { data: LoginData, state: LoginState, actions: LoginActions }  {
    const {authService} = useServices();

    // Controle do Estado dos Dados
    const [login, setLogin] = useState<LoginData>({
        login: "",
        senha: ""
    });

    // Controle do Estado
    const [isCarregando, setIsCarregando] = useState<boolean>(false);
    const [erro, setErro] = useState<null | string>(null);

    function hasErro(): boolean {
        return erro !== null && erro !== "";
    }

    // Ações
    function onChangeCampo(campo: keyof LoginData, valor: string) {
        setLogin(prev => ({
        ...prev,
        [campo]: valor
        }));
    }

    async function onSubmit() {
        setIsCarregando(true);
        setErro(null);

        try {

            await authService.login({
                login: login.login,
                senha: login.senha
            } as LoginMemberRequest);

        } catch {

            setErro("Login ou senha inválidos.");

        } finally {

            setIsCarregando(false);

        }
    }

    return {
        data: login,
        state: {
            isCarregando,
            hasErro,
            erro
        },
        actions: {
            onChangeCampo,
            onSubmit
        }
    }
}

export { useLogin };
export type { LoginActions, LoginData, LoginState };

