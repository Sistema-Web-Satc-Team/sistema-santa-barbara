import { Api } from "@/application/api/Api";
import type { LoginRequest } from "@/application/model/LoginRequest";
import type { ProfileData } from "@/application/model/ProfileData";
import axios from "axios";


class AuthService {

    static async login(request: LoginRequest): Promise<void> {
        try {
            const response = await axios.post(
                Api.getAuthResource() + "login", 
                request, 
                { withCredentials: true }
            )
        } catch(err) {
            throw err;
        }
    }

   static async isAutenticado(): Promise<boolean> {
        try {
            const response = await axios.get(
                Api.getAuthResource() + "me",
                { withCredentials: true }
            );

            return response.status === 200;
        } catch (err) {
            return false;
        }
    }


     static async me(): Promise<ProfileData> {
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
            } as ProfileData
        } catch (err) {
            console.log(err)
            const message = err.response?.data?.message || "Falha na autenticação.";
            throw new Error(message);
        }
    }

    static async updateMe(newData: ProfileData): Promise<void> {
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
            console.log(err)
            const message = err.response?.data?.message || "Falha ao atualizar o perfil.";
            throw new Error(message);
        }
    }
}

export { AuthService };
