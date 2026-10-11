import { fetchUserProfile, login, logout, updateUserProfile, type LoginRequest, type MeUpdateRequest } from "#/api/auth";
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



    const contextValue = {
        state: {
            profile: user ?? null,
            isLoading,
            error: error as Error | null,
            isUpdating: updateMutation.isPending
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
            }
        }
    }


    return (
        <AuthContext.Provider value={contextValue}>
            {children}
        </AuthContext.Provider>
    )
}