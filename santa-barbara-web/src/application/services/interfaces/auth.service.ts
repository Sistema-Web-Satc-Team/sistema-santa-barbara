import type { GetProfileResponse } from "@/application/model/auth/GetProfileResponse";
import type { LoginMemberRequest } from "@/application/model/auth/LoginMemberRequest";
import type { UpdateProfileRequest } from "@/application/model/auth/UpdateProfileRequest";

interface AuthService {
    login(request: LoginMemberRequest): Promise<void>;
    me(): Promise<GetProfileResponse>;
    updateMe(newData: Partial<UpdateProfileRequest>): Promise<void>;
    isAutenticado(): Promise<boolean>;
}

export type { AuthService };
