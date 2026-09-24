import { Button } from "@/ui/components/button";
import { Input } from "@/ui/components/input";

import "@/ui/styles/login.css";
import { useLogin } from "../hook/useLogin";

function Login() {
    const { data, actions, state } = useLogin();
   

    return (
        <form 
            onSubmit={
                async (e: React.SubmitEvent<HTMLFormElement>) => {
                    e.preventDefault();
                    await actions.onSubmit();
                }
            }
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
            {state.hasErro() && (
                <div className="p-3 text-sm text-red-600 bg-red-100 rounded-lg text-center">
                    {state.erro}
                </div>
            )}

            <div className="flex flex-col 2xl:gap-[12px] gap-[6px]">
                <label htmlFor="username" className="login__label">Nome de usuário ou Email:</label>
                <Input 
                    name="username" 
                    variant="normal" 
                    placeholder="Email ou Nome de Usuário" 
                    value={data.login}
                    onChange={(e) => actions.onChangeCampo("login", e.target.value)}
                />
            </div>

            <div className="flex flex-col 2xl:gap-[12px] gap-[6px]">
                <label htmlFor="password" className="login__label">Senha:</label>
                <Input 
                    name="password" 
                    variant="normal" 
                    placeholder="Senha"
                    value={data.senha}
                    onChange={(e) => actions.onChangeCampo("senha", e.target.value)}
                />
            </div>

            <div className="flex flex-row w-[100%] 2xl:pt-[64px] pt-[24px]">
                <Button type="submit" className="w-[100%]" disabled={state.isCarregando}>
                    {"Entrar"}
                </Button>
            </div>
                
        </form>
    )
}

export default Login;