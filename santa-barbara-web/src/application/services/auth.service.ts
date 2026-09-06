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
}

export { AuthService };
