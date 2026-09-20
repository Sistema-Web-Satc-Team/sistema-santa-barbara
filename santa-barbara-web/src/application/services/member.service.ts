import axios from "axios";
import { Api } from "@/application/api/Api";
import type { MemberData } from "@/application/model/MemberData";

interface GetMembersParams {
    page: number;
    limit: number;
    papel?: string;
}

interface MembersPageResponse {
    content: MemberData[];
}

export const memberService = {
    getMembers: async ({ page, limit, papel }: GetMembersParams): Promise<MembersPageResponse> => {
        
        const PageIndex = page - 1;

        let endpoint = `${Api.getRooutResource()}membros?page=${PageIndex}&size=${limit}`;

        if (papel && papel.trim() !== "") {
            endpoint += `&papel=${encodeURIComponent(papel)}`;
        }

        const response = await axios.get<MembersPageResponse>(endpoint, 
            { withCredentials: true }
        );

        return response.data;
    }
};