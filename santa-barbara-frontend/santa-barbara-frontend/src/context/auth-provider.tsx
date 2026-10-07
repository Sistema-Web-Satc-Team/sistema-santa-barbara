import { fetchUserProfile, login, logout, updateUserProfile, type LoginRequest, type UpdateProfileFields } from "#/api/auth";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import type React from "react";
import { AuthContext } from "./auth-context";


export function AuthProvider({ children }: { children: React.ReactNode }) {

    const queryClient = useQueryClient();

    const { data: user, isLoading, error } = useQuery({
        queryKey: ['user-profile'],
        queryFn: fetchUserProfile,
        staleTime: 1000 * 60 * 5,
        retry: false,                    
        refetchOnWindowFocus: false,
    })



    const updateMutation = useMutation({
        mutationFn: updateUserProfile,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['user-profile'] });
        }
    })

    const logoutMutation = useMutation({
        mutationFn: logout,
        onSuccess: () => {
            queryClient.setQueryData(['user-profile'], null);
            queryClient.invalidateQueries({ queryKey: ['user-profile'] });
        }
    })

    const loginMutation = useMutation({
        mutationFn: login,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['user-profile'] });
        }
    })

    const dataNascimentoRegex = /^\d{2}-\d{2}-\d{4}$/;

    const isValidDataNascimento = user?.dataNascimento && dataNascimentoRegex.test(user.dataNascimento);

    const formatarDataSegura = (dataStr: string) => {
        if (!dataStr) return "";
        const [ano, mes, dia] = dataStr.split('T')[0].split('-');
        return `${dia}/${mes}/${ano}`;
    };

    const contextValue = {
        data: {
            id: user?.id ?? "",
            nome: user?.nome ?? "",
            email: user?.email ?? "",
            urlAvatar: user?.urlAvatar ?? "",
            sobrenome: user?.sobrenome ?? "",
            nomeUsuario: user?.nomeUsuario ?? "",
            telefone: user?.telefone ?? "",
            endereco: user?.endereco ?? "",
            papeis: user?.papeis ?? [],
            age: user?.age ?? 0,
            dataNascimento: isValidDataNascimento ? 
                formatarDataSegura(user.dataNascimento) : ""
        },
        state: {
            isLoading,
            error: error as Error | null,
            isUpdating: updateMutation.isPending
        },
        actions: {
            updateProfile: (newData: UpdateProfileFields) => updateMutation.mutateAsync(newData),
            logout: () => logoutMutation.mutateAsync(),
            login: async (credentials: LoginRequest) => {
                try {
                    await loginMutation.mutateAsync(credentials);
                } catch (err) {
                    console.error(err);

                    if (axios.isAxiosError(err)) {
                        if (err.response && err.response.status === 400) {
                            const data = err.response.data as { message?: string };
                            const errorMessage = data.message || err.message;
                            
                            throw new Error(errorMessage);
                        }
                    }

                    throw new Error("Ocorreu um erro interno.");
                }
            }
        }
    }


    return (
        <AuthContext.Provider value={contextValue}>
            {children}
        </AuthContext.Provider>
    )
}