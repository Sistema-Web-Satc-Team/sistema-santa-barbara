import axios from "axios";
import { Api } from "@/application/api/Api";
import type { MemberData } from "@/application/model/MemberData";

interface GetMembersParams {
    page: number;
    limit: number;
}

interface MembersPageResponse {
    content: MemberData[];
}

export const memberService = {
    getMembers: async ({ page, limit }: GetMembersParams): Promise<MembersPageResponse> => {
        const endpoint = `${Api.getRooutResource()}/membros?page=${page}&limit=${limit}`;

        const response = await axios.get<MembersPageResponse>(endpoint);

        return response.data;
    }
};