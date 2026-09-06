import { Api } from "@/application/api/Api";
import type { LoginRequest } from "@/application/model/LoginRequest";
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
}

export { AuthService };
