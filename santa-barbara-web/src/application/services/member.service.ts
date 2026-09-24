import { Api } from "@/application/api/Api";
import type { MemberData } from "@/application/model/MemberData";
import axios from "axios";

interface GetMembersParams {
    page: number;
    limit: number;
    papel?: string;
}

interface MembersPageResponse {
    content: MemberData[];
}

export interface UpdateMemberRequest {
    nome?: string;
    sobrenome?: string;
    email?: string;
    papeis?: string[];
    telefone?: string;
    endereco?: string;
    status?: string;
    dataNascimento?: string;
}


export const memberService = {
   getMembers: async ({ page, limit, papel }: GetMembersParams): Promise<MembersPageResponse> => {
        
        const PageIndex = page - 1;

        let endpoint = `${Api.getRooutResource()}membros?page=${PageIndex}&size=${limit}`;

        if (papel && papel.trim() !== "") {
            endpoint += `&papel=${encodeURIComponent(papel)}`;
        }

        const response = await axios.get<MembersPageResponse>(endpoint, { withCredentials: true });

        return response.data;
    },

    getMemberById: async (id: string): Promise<MemberData> => {
        const response = await axios.get<MemberData>(
            `${Api.getRooutResource()}membros/${id}`,
            { withCredentials: true }
        );

        return response.data;
    },

    updateMember: async (id: string, data: UpdateMemberRequest): Promise<void> => {
        await axios.patch(
            `${Api.getRooutResource()}membros/${id}`,
            data,
            { withCredentials: true }
        );
    },
};

