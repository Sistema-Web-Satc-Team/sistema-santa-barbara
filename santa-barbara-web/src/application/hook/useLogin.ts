import { useServices } from "@/application/hook/useServices";
import type { LoginMemberRequest } from "@/application/model/auth/LoginMemberRequest";
import { useState } from "react";
import { useNavigate } from "react-router";


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

    const navigate = useNavigate();

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

            navigate("/dashboard/membros")

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

