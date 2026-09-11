import { Api } from "@/application/api/Api";
import type { LoginRequest } from "@/application/model/LoginRequest";
import type { ProfileResponseApi } from "@/application/model/ProfileResponseApi";
import axios from "axios";
import type { ProfileUpdateRequest } from "../model/ProfileUpdateRequest";


class AuthService {

    static async login(request: LoginRequest): Promise<void> {

        await axios.post(
            Api.getAuthResource() + "login", 
            request, 
            { withCredentials: true }
        )

    }

   static async isAutenticado(): Promise<boolean> {
        try {
            const response = await axios.get(
                Api.getAuthResource() + "me",
                { withCredentials: true }
            );

            return response.status === 200;
        } catch  {
            return false;
        }
    }


     static async me(): Promise<ProfileResponseApi> {
        try {
            const response = await axios.get(
                Api.getAuthResource() + "me",
                { withCredentials: true }
            );

            const data = response.data ?? {};

            return {
                nome: data.nome ?? "",
                sobrenome: data.sobrenome ?? "",
                email: data.email ?? "",
                nomeUsuario: data.nomeUsuario ?? "",
                telefone: data.telefone ?? "",
                endereco: data.endereco ?? "",
                instrumentos: Array.isArray(data.instrumentos) ? data.instrumentos : [],
                papeis: Array.isArray(data.papeis) ? data.papeis : []
            } as ProfileResponseApi
        } catch (err) {
            console.log(err);
            const message = axios.isAxiosError(err)
                ? err.response?.data?.message || "Falha na autenticação."
                : "Falha na autenticação.";
            throw new Error(message);
        }
    }

    static async updateMe(newData: ProfileUpdateRequest): Promise<void> {
        try {
            await axios.patch(
                Api.getAuthResource() + "me",
                {
                    email: newData.email ?? "",
                    nomeUsuario: newData.nomeUsuario ?? "",
                    telefone: newData.telefone ?? "",
                    endereco: newData.endereco ?? "",
                },
                { withCredentials: true }
            );
        } catch (err) {
            console.log(err);
            const message = axios.isAxiosError(err)
                ? err.response?.data?.message || "Falha ao atualizar o perfil."
                : "Falha ao atualizar o perfil.";
            throw new Error(message);
        }
    }
}

export { AuthService };
