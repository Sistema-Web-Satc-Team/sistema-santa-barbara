import { Api } from "@/application/api/Api";
import type { GetProfileResponse } from "@/application/model/auth/GetProfileResponse";
import type { LoginMemberRequest } from "@/application/model/auth/LoginMemberRequest";
import type { UpdateProfileRequest } from "@/application/model/auth/UpdateProfileRequest";
import type { AuthService } from "@/application/services/interfaces/auth.service";
import axios from "axios";

class AuthServiceApi implements AuthService {

    async login(request: LoginMemberRequest): Promise<void> {

        await axios.post(
            Api.getAuthResource() + "login", 
            request, 
            { withCredentials: true }
        )

    }

   async isAutenticado(): Promise<boolean> {
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


    async me(): Promise<GetProfileResponse> {
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
            } as GetProfileResponse
        } catch (err) {
            console.log(err);
            const message = axios.isAxiosError(err)
                ? err.response?.data?.message || "Falha na autenticação."
                : "Falha na autenticação.";
            throw new Error(message, { cause: err });
        }
    }

    async updateMe(newData: Partial<UpdateProfileRequest>): Promise<void> {
        try {

            const payload: Record<string, any> = {};
            
            if (newData.email !== undefined && newData.email !== "") {
                payload.email = newData.email;
            }
            if (newData.telefone !== undefined && newData.telefone !== "") {
                payload.telefone = newData.telefone;
            }
            if (newData.endereco !== undefined && newData.endereco !== "") {
                payload.endereco = newData.endereco;
            }

            await axios.patch(
                
                Api.getAuthResource() + "me",
                payload,
                { withCredentials: true }
            );
        } catch (err) {
            console.log(err);
            const message = axios.isAxiosError(err)
                ? err.response?.data?.message || "Falha ao atualizar o perfil."
                : "Falha ao atualizar o perfil.";
            throw new Error(message, { cause: err });
        }
    }
}

export { AuthServiceApi };
