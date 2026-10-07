
import { AuthContext } from "#/context/auth-context";
import { useContext } from "react";


export function useAuth() {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error("User Context não está definido corretamente em `use-user.ts`")
    }

    return context;
}