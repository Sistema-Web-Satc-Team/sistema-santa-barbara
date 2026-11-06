import type { LoginRequest, MeProfileResponse, MeUpdateRequest } from "#/api/auth";
import React from "react";

export interface AuthContextProps {
    state: {
        profile: MeProfileResponse | null;
        isLoading: boolean;
        error: Error | null;
        isUpdating: boolean;
        currentRole: string | null;
        loggedIn: () => boolean;
    },
    actions: {
        updateProfile: (newData: MeUpdateRequest) => Promise<void>;
        logout: () => Promise<void>;
        login: (data: LoginRequest) => Promise<void>;
        setCurrentRole: (value: string) => void;
    }
}

const AuthContext = React.createContext<AuthContextProps | undefined>(undefined);

export { AuthContext };
