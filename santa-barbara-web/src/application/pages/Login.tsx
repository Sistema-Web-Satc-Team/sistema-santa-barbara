import { Button } from "@/ui/components/button";
import { Input } from "@/ui/components/input";

import "@/ui/styles/login.css";
import { useState } from "react";
import { useNavigate } from "react-router";
import { AuthService } from "../services/auth.service";

function Login() {

    const [login, setLogin] = useState("");
    const [senha, setSenha] = useState("");
    const [carregando, setCarregando] = useState(false);
    const [erro, setErro] = useState<string | null>(null);

    const navigate = useNavigate();

    const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        setErro(null);
        setCarregando(true);

        try {
            await AuthService.login({ login, senha });
            console.log("Login efetuado! O cookie HttpOnly já está gravado no navegador.");
            navigate("/dashboard/members");
        } catch (err) {
            console.error(err);
            setErro("Credenciais inválidas ou erro ao conectar ao servidor.");
        } finally {
            setCarregando(false);
        }
    };

    return (
        <form 
            onSubmit={handleSubmit}
            className={`
                w-[90%]
                xl:w-[35%]
                h-fit

                border-1 border-solid border-[var(--light-neutral-color)]
                
                2xl:px-[72px] 2xl:pb-[64px] 2xl:pt-[36px]
                xl:px-[48px] xl:pb-[32px] xl:pt-[18px]
                px-[32px] pb-[16px] pt-[9px]
                
                rounded-[16px]
                
                mx-auto
                my-auto
                gap-[24px]

                flex
                flex-col
            `}
        >
        
            <h1 className="2xl:py-[32px] py-[16px] mx-auto login__title">Login</h1>

            { /* [WARN] CRIAR COMPONENTE ERROR CARD */ }
            {erro && (
                <div className="p-3 text-sm text-red-600 bg-red-100 rounded-lg text-center">
                    {erro}
                </div>
            )}

            <div className="flex flex-col 2xl:gap-[12px] gap-[6px]">
                <label htmlFor="username" className="login__label">Nome de usuário ou Email:</label>
                <Input 
                    name="username" 
                    variant="normal" 
                    placeholder="Email ou Nome de Usuário" 
                    value={login}
                    onChange={(e) => setLogin(e.target.value)}
                />
            </div>

            <div className="flex flex-col 2xl:gap-[12px] gap-[6px]">
                <label htmlFor="password" className="login__label">Senha:</label>
                <Input 
                    name="password" 
                    variant="normal" 
                    placeholder="Senha"
                    value={senha}
                    onChange={(e) => setSenha(e.target.value)}
                />
            </div>

            <div className="flex flex-row w-[100%] 2xl:pt-[64px] pt-[24px]">
                <Button type="submit" className="w-[100%]" disabled={carregando}>
                    {carregando ? "Entrando..." : "Entrar"}
                </Button>
            </div>
                
        </form>
    )
}

export default Login;