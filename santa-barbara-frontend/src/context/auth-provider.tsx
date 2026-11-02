import type { LoginRequest, MeUpdateRequest } from "#/api/auth";
import { fetchUserProfile, login, logout, updateUserProfile } from "#/api/auth";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import axios from "axios";
import React from "react";
import { AuthContext } from "./auth-context";


export function AuthProvider({ children }: { children: React.ReactNode }) {

    const [currentRole, setCurrentRole] = React.useState<string | null>(null);

    const queryClient = useQueryClient();

    const { data: user, isLoading, error } = useQuery({
        queryKey: ['user-profile'],
        queryFn: fetchUserProfile,
        staleTime: 1000 * 60 * 5,
        retry: false,                    
        refetchOnWindowFocus: false,
    })

    React.useEffect(() => {
        if (currentRole === null && user?.funcoes.length === 1) {
            setCurrentRole(user.funcoes[0])
        }
    }, [currentRole, user])


    const updateMutation = useMutation({
        mutationFn: updateUserProfile,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['user-profile'] });
        }
    })

    const logoutMutation = useMutation({
        mutationFn: logout,
        onSuccess: () => {
            setCurrentRole(null);
            queryClient.setQueryData(['user-profile'], null);
            queryClient.invalidateQueries({ queryKey: ['user-profile'] });
        }
    })

    const loginMutation = useMutation({
        mutationFn: login,
        onSuccess: () => {
            setCurrentRole(null);
            queryClient.invalidateQueries({ queryKey: ['user-profile'] });
        }
    })



    const contextValue = {
        state: {
            profile: user ?? null,
            isLoading,
            error,
            isUpdating: updateMutation.isPending,
            currentRole,
            loggedIn: () => { return user != null; }
        },
        actions: {
            updateProfile: (newData: MeUpdateRequest) => updateMutation.mutateAsync(newData),
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
            },
            setCurrentRole: (value: string) => { setCurrentRole(value) }
           
        }
    }


    return (
        <AuthContext.Provider value={contextValue}>
            {children}
        </AuthContext.Provider>
    )
}
