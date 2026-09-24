import { useEffect, useState } from "react";
import type { GetProfileResponse } from "../model/auth/GetProfileResponse";
import { useServices } from "./useServices";

interface ProfileData {
    nome: string,
    sobrenome: string,
    email: string,
    nomeUsuario: string,
    telefone: string,
    endereco: string,
    papeis: string[]
}

interface ProfileState {
    hasSuccess: () => boolean,
    successMessage: string,

    isLoading: boolean,
    isInitialized: boolean,

    hasError: () => boolean,
    errorMessage: string,

    hasFieldErrors: () => boolean,
    errorsFields: Record<string, string>,



    canSave: () => boolean
}

interface ProfileActions {
    onChangeCampo: (campo: keyof ProfileData, valor: string) => void;
    save: () => Promise<void>;
    clear: () => Promise<void>;
}

interface ProfileCache {
    data: GetProfileResponse,
    cachedAt: number
}

function useProfile(): {data: ProfileData, state: ProfileState, actions: ProfileActions} {
    const { authService } = useServices();

    const [profile, setProfile] = useState({
        nome: "",
        sobrenome: "",
        email: "",
        nomeUsuario: "",
        telefone: "",
        endereco: "",
        papeis: []
    })

    const [dirtyFields, setDirtyFields] = useState<
        Record<
            keyof Pick<ProfileData, "email" | "telefone" | "endereco">, 
            boolean
        >
    >({
        email: false,
        telefone: false,
        endereco: false,
    });


    const CACHE_TTL = 5 * 60 * 1000;

    const [cache, setCache] = useState<ProfileCache | null>(() => {
        const cacheStr = localStorage.getItem("profile-cache");
        return cacheStr ? JSON.parse(cacheStr) : null;
    });

    function isValidCache(currentCache: ProfileCache | null): boolean {
        if (!currentCache) return false;
        return Date.now() - currentCache.cachedAt < CACHE_TTL;
    }


    useEffect(() => {
        (async () => {
            try {
                setIsLoading(true);


                let dadosPerfil = cache?.data;
                
                if (!isValidCache(cache)) {
                    dadosPerfil = await authService.me();
                    
                    const novoCache: ProfileCache = {
                        data: dadosPerfil,
                        cachedAt: Date.now() 
                    };

                    localStorage.setItem("profile-cache",  JSON.stringify(novoCache));
                    setCache(novoCache);
                } 

                setProfile({
                    nome: dadosPerfil.nome,
                    sobrenome: dadosPerfil.sobrenome,
                    email: dadosPerfil.email,
                    nomeUsuario: dadosPerfil.nomeUsuario,
                    telefone: dadosPerfil.telefone,
                    endereco: dadosPerfil.endereco,
                    papeis: dadosPerfil.papeis
                })
                
                setIsInitialized(true);
            } catch (err) {
                setIsInitialized(false);
                setErrorMessage("Ocorreu um erro ao carregar os dados.")
            } finally {
                setIsLoading(false);
            }
        })()
    }, [])

    // States

    const [isInitialized, setIsInitialized] = useState<boolean>(false);
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const [successMessage, setSuccessMessage] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

    const [errorsFields, setFieldErrors] = useState<Record<string, string>>({});

    function hasSuccess() {
        return successMessage.trim() !== "";
    }

    function hasError() {
        return errorMessage.trim() !== "";
    }

    function hasFieldErrors() {
        return Object.keys(errorsFields).length > 0;
    }

    // Validações

    const validarCampos = (campoModificado: keyof ProfileData, novoValor: string) => {
        const novosErros = { ...errorsFields };
        const valorValidacao = novoValor.trim();

        if (campoModificado === "email") {
            if (!valorValidacao || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valorValidacao)) {
                novosErros.email = "E-mail inválido ou obrigatório.";
            } else {
                delete novosErros.email;
            }
        }

        if (campoModificado === "telefone") {
            
            if (valorValidacao.length > 0 && (valorValidacao.length < 11 || valorValidacao.length > 15)) {
                novosErros.telefone = "O telefone deve ter entre 11 e 15 dígitos numéricos.";
            } else if (valorValidacao.length === 0) {
                novosErros.telefone = "O telefone é obrigatório, uma vez que foi inserido.";
            } else {
                delete novosErros.telefone;
            }
        }

        if (campoModificado === "endereco") {
            if (valorValidacao.length > 0 && valorValidacao.length < 2) {
                novosErros.endereco = "O endereço deve ter pelo menos 2 caracteres.";
            } else if (valorValidacao.length === 0) {
                novosErros.endereco = "O endereço é obrigatório, uma vez que foi inserido.";
            } else {
                delete novosErros.endereco;
            }
        }

        setFieldErrors(novosErros);
    };

    function canSave(): boolean {
        return dirtyFields.email || dirtyFields.endereco || dirtyFields.telefone;
    }

    // Actions

    function onChangeCampo(campo: keyof ProfileData, valor: string) {
        setSuccessMessage("");

        let valorFormatado = valor;

        if (campo === "telefone") {
            valorFormatado = valor.replace(/\D/g, "");
        }

        setProfile((prev) => ({...prev, [campo]: valorFormatado}));
        validarCampos(campo, valorFormatado);

        setDirtyFields((v) => ({...v, [campo]: cache.data[campo] !== valorFormatado}));        
    }

    async function save() {
        if(!canSave()) { return; } 

        if (hasFieldErrors()) {
            setErrorMessage("Arrume os campos inválidos!");
            return;
        }

        try {
            setIsLoading(true);
            setErrorMessage("");
            setSuccessMessage("");

            await authService.updateMe({
                email: dirtyFields.email ? profile.email : null,
                telefone: dirtyFields.telefone ? profile.telefone : null,
                endereco: dirtyFields.endereco ? profile.endereco : null,
            })

            const dadosAtualizados = await authService.me();
            const novoCache: ProfileCache = { data: dadosAtualizados, cachedAt: Date.now() };
            localStorage.setItem("profile-cache", JSON.stringify(novoCache));
            setCache(novoCache);

            setDirtyFields({ email: false, telefone: false, endereco: false });
            setSuccessMessage("Perfil atualizado com sucesso!");

        } catch {
            setErrorMessage("Ocorreu um erro inesperado!");
        }finally {
            setIsLoading(false);
        }
    }

    async function clear() {
        try {

            setIsLoading(false);
            setErrorMessage("");
            setSuccessMessage("");
            setFieldErrors({})

            setDirtyFields({
                email: false,
                telefone: false,
                endereco: false,
            })

            let dadosPerfil = cache?.data;

            if (!isValidCache(cache)) {
                dadosPerfil = await authService.me();
                        
                const novoCache: ProfileCache = {
                    data: dadosPerfil,
                    cachedAt: Date.now() 
                };

                localStorage.setItem("profile-cache",  JSON.stringify(novoCache));
                setCache(novoCache);
            }

            setProfile({
                nome: dadosPerfil.nome,
                sobrenome: dadosPerfil.sobrenome,
                email: dadosPerfil.email,
                nomeUsuario: dadosPerfil.nomeUsuario,
                telefone: dadosPerfil.telefone,
                endereco: dadosPerfil.endereco,
                papeis: dadosPerfil.papeis
            })
        } catch {
            setErrorMessage("Ocorreu um erro inesperado!");
        }
    }

    return {
        data: profile,
        state: {
            successMessage,
            errorsFields,
            errorMessage,
            hasError,
            hasSuccess,
            hasFieldErrors,
            isInitialized,
            isLoading,
            canSave
        },
        actions: {
            save,
            clear,
            onChangeCampo
        }
    }
}

export { useProfile };
export type { ProfileActions, ProfileCache, ProfileData, ProfileState };

