import type { AuthService } from "@/application/services/interfaces/auth.service";
import { AuthServiceApi } from "../services/auth.service.api";

export interface ServiceContainer {
  authService: AuthService;
}


export const globalServices: ServiceContainer = {
  authService: new AuthServiceApi(),
};