import type { LoginRequest, UpdateProfileFields } from "#/api/auth";
import type { UUID } from "crypto";
import React from "react";

export interface AuthContextProps {
    data: {
        id: UUID | string;
        nome: string;
        sobrenome: string;
        nomeUsuario: string;
        email: string;
        urlAvatar: string;
        telefone: string;
        endereco: string;
        papeis: string[],
        age: number,
        dataNascimento: string
    },
    state: {
        isLoading: boolean;
        error: Error | null;
        isUpdating: boolean;
    },
    actions: {
        updateProfile: (newData: UpdateProfileFields) => Promise<void>;
        logout: () => Promise<void>;
        login: (data: LoginRequest) => Promise<void>;
    }
}

const AuthContext = React.createContext<AuthContextProps>({
    data: {
        id: "",
        nome: "",
        email: "",
        urlAvatar: "",
        sobrenome: "",
        nomeUsuario: "",
        telefone: "",
        endereco: "",
        papeis: [],
        age: 0,
        dataNascimento: ""

    },
    state: {
        isLoading: false,
        error: null,
        isUpdating: false
    },
    actions: {
        updateProfile: async () => {},
        logout: async () => {},
        login: async () => {}
    }
});

export { AuthContext };
